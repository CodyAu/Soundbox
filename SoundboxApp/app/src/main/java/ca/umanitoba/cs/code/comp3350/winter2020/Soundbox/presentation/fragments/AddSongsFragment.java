package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaylistController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.SongController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils.SongFilters;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongCollection;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.adapters.TracklistAdapter;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Dispatchers;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.SearchSuggestions;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Themes;

public class AddSongsFragment extends DialogFragment {
    public static final String TAG = "dialog_add_songs";

    public static final String PLAYLIST_ARG = "playlist_id";

    private MainActivity mainActivity;

    private SharedPreferences sharedPreferences;

    private Toolbar toolbar;
    private SearchView searchBar;
    private SimpleCursorAdapter simpleCursorAdapter;

    private RecyclerView recyclerView;
    private TracklistAdapter adapter;

    private Playlist playlist;
    private SongCollection addSongs;

    private SongFilters.SortOrder songSortOrder;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(requireContext(), getTheme()) {
            @Override
            public void onBackPressed() {
                dismissDialog();
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        sharedPreferences = mainActivity.getSharedPreferences(mainActivity.getString(R.string.shared_pref), Context.MODE_PRIVATE);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
        this.addSongs = new SongCollection();

        simpleCursorAdapter = SearchSuggestions.createSimpleCursorAdapter(mainActivity);

        songSortOrder = SongFilters.SortOrder.fromOrdinal(sharedPreferences.getInt(getString(R.string.shared_pref_sort_tracklist), 0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_add_songs, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            long playlistId = arguments.getLong(PLAYLIST_ARG);
            this.playlist = (new PlaylistController()).getPlaylistById(playlistId);

            populateSongs();
        }

        toolbar = view.findViewById(R.id.toolbar);

        adapter = new TracklistAdapter(mainActivity, null); // Keep different sets of data
        adapter.addAll(this.addSongs.getSongs());

        adapter.setOnClickListener(new TracklistAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, Song song, int position) {
                enableActionMode(position);
            }

            @Override
            public void onItemLongClick(View view, Song song, int position) {
                // Pass
            }
        });
        adapter.setOnContextMenuClickListener(new TracklistAdapter.OnContextMenuClickListener() {
            @Override
            public void onItemClick(MenuItem menuItem, Song song, int position) {
                switch (menuItem.getItemId()) {
                    case R.id.action_details:
                        DetailsFragment.openDialog(mainActivity, song);
                        break;
                    case R.id.action_play_next:
                        Dispatchers.information(mainActivity, "Play next");

                        PlaybackController.playNext(song);
                        break;
                    case R.id.action_add_queue:
                        Dispatchers.information(mainActivity, "Added to queue");

                        PlaybackController.addPlaybackQueue(song);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public boolean filterItem(MenuItem menuItem, Song song, int position) {
                return menuItem.getItemId() != R.id.action_remove;
            }
        });
        adapter.setOnCreateRowListener(new TracklistAdapter.OnCreateRowListener() {
            @Override
            public void onCreateRow(TracklistAdapter.SongViewHolder holder, View view, int viewType) {
                holder.add.setVisibility(View.GONE);
                holder.drag.setVisibility(View.GONE);
            }
        });

        recyclerView = view.findViewById(R.id.tracklist_recycle_view);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(recyclerView.getLayoutParams());
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, 0);
        recyclerView.setLayoutParams(layoutParams);
        recyclerView.requestLayout();

        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), LinearLayout.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter((RecyclerView.Adapter) adapter);

        initToolbar();
        initSearchBar();
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    private void initToolbar() {
        toolbar.setTitle(R.string.add_songs);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                switch (itemId) {
                    case R.id.action_sort_default:
                    case R.id.action_sort_name:
                    case R.id.action_sort_artist:
                    case R.id.action_sort_album:
                    case R.id.action_sort_genre:
                        if (itemId == R.id.action_sort_default) {
                            songSortOrder = SongFilters.SortOrder.DEFAULT;
                            addSongs.setComparator(Song::compareToById);
                        }
                        if (itemId == R.id.action_sort_name) {
                            songSortOrder = SongFilters.SortOrder.NAME;
                            addSongs.setComparator(Song::compareToByName);
                        }
                        if (itemId == R.id.action_sort_artist) {
                            songSortOrder = SongFilters.SortOrder.ARTIST;
                            addSongs.setComparator(Song::compareToByArtist);
                        }
                        if (itemId == R.id.action_sort_album) {
                            songSortOrder = SongFilters.SortOrder.ALBUM;
                            addSongs.setComparator(Song::compareToByAlbum);
                        }
                        if (itemId == R.id.action_sort_genre) {
                            songSortOrder = SongFilters.SortOrder.GENRE;
                            addSongs.setComparator(Song::compareToByGenre);
                        }

                        sharedPreferences.edit().putInt(getString(R.string.shared_pref_sort_tracklist), songSortOrder.ordinal()).apply();

                        if (!item.isChecked() && item.isCheckable()) {
                            item.setChecked(true);
                            adapter.setSongs(addSongs.getSongs());
                        }
                        return true;
                    case R.id.action_add:
                        addSongs();
                        disableActionMode();
                        return true;
                    case R.id.action_search:
                        return true;
                    default:
                        return false;
                }
            }
        });

        toolbar.inflateMenu(R.menu.menu_sort);
        toolbar.inflateMenu(R.menu.dialog_add_songs);

        // Set the correct checked sort filter
        switch (songSortOrder) {
            case DEFAULT:
                toolbar.getMenu().findItem(R.id.action_sort_default).setChecked(true);
                break;
            case NAME:
                toolbar.getMenu().findItem(R.id.action_sort_name).setChecked(true);
                break;
            case ARTIST:
                toolbar.getMenu().findItem(R.id.action_sort_artist).setChecked(true);
                break;
            case ALBUM:
                toolbar.getMenu().findItem(R.id.action_sort_album).setChecked(true);
                break;
            case GENRE:
                toolbar.getMenu().findItem(R.id.action_sort_genre).setChecked(true);
                break;
            default:
                break;
        }
    }

    private void initSearchBar() {
        try {
            searchBar = (SearchView) toolbar.getMenu().findItem(R.id.action_search).getActionView();
        } catch (Exception e) {
            e.printStackTrace();
        }

        searchBar.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchBar.setIconified(true);
        searchBar.setFocusable(true);
        searchBar.setQueryHint(mainActivity.getString(R.string.search_audio));
        searchBar.setSuggestionsAdapter(simpleCursorAdapter);

        searchBar.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                searchBar.setQuery(SearchSuggestions.getSuggestion(simpleCursorAdapter, position), true);
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }
        });

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                SearchSuggestions.populateAdapterSongs(simpleCursorAdapter, newText, adapter.getSongs());

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                List<Song> songs = SongFilters.getSongsLike(query, adapter.getSongs().getSongs());

                adapter.setSongs(songs);

                searchBar.clearFocus();

                return false;
            }
        });

        searchBar.setOnCloseListener(new SearchView.OnCloseListener() { // On close doesn't get called sometimes, known but with action bars
            @Override
            public boolean onClose() {
                populateSongs();
                adapter.setSongs(addSongs.getSongs());

                return false;
            }
        });
        toolbar.getMenu().findItem(R.id.action_search).setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                populateSongs();
                adapter.setSongs(addSongs.getSongs());

                return true;
            }
        });
    }

    private void enableActionMode(int position) {
        if (adapter.getSelectedItemCount() <= 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            toolbar.getMenu().findItem(R.id.action_add).setVisible(true);
            toolbar.invalidate();

            int color = Themes.getThemeColor(mainActivity, R.attr.themeColorAccent);
            Themes.setSystemBarColor(mainActivity, color);
            toolbar.setBackgroundColor(color);
        }

        toggleSelection(position);
    }

    private void disableActionMode() {
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        toolbar.getMenu().findItem(R.id.action_add).setVisible(false);
        toolbar.setTitle(R.string.add_songs);
        toolbar.invalidate();

        int colorPrimary = Themes.getThemeColor(mainActivity, R.attr.themeColorPrimary);
        int colorPrimaryDark = Themes.getThemeColor(mainActivity, R.attr.themeColorPrimaryDark);
        Themes.setSystemBarColor(mainActivity, colorPrimaryDark);
        toolbar.setBackgroundColor(colorPrimary);
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 0) {
            disableActionMode();
        } else {
            toolbar.setTitle(String.valueOf(count));
            toolbar.invalidate();
        }
    }

    private void addSongs() {
        if (this.playlist != null) {
            List<Song> newSongs = adapter.getSelectedSongs();
            if (newSongs.size() > 0) {
                for (Song song : newSongs) {
                    this.playlist.insertSong(song);
                    this.addSongs.deleteSong(song);
                }
                adapter.clear();
                adapter.addAll(this.addSongs.getSongs());

                (new PlaylistController()).updatePlaylist(this.playlist);
            }
        }
    }

    private void populateSongs() {
        // Only display songs that the user can add (If the song is already in the playlist don't display it)
        if (this.playlist != null) {
            addSongs.clearSongs();
            for (Song song : (new SongController()).getAllSongs()) {
                if (!this.playlist.getSongs().contains(song))
                    this.addSongs.insertSong(song);
            }

            if (songSortOrder.equals(SongFilters.SortOrder.DEFAULT))
                addSongs.setComparator(Song::compareToById);
            if (songSortOrder.equals(SongFilters.SortOrder.NAME))
                addSongs.setComparator(Song::compareToByName);
            if (songSortOrder.equals(SongFilters.SortOrder.ARTIST))
                addSongs.setComparator(Song::compareToByArtist);
            if (songSortOrder.equals(SongFilters.SortOrder.ALBUM))
                addSongs.setComparator(Song::compareToByAlbum);
            if (songSortOrder.equals(SongFilters.SortOrder.GENRE))
                addSongs.setComparator(Song::compareToByGenre);

        } else this.addSongs = null;
    }

    private void dismissDialog() {
        if (adapter.getSelectedItemCount() > 0) {
            disableActionMode();
        } else {
            dismiss();
        }
        adapter.clearSelections();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        try {
            FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
            TracklistFragment fragment = (TracklistFragment) fragmentManager.findFragmentByTag(TracklistFragment.TAG);
            fragment.updateSongs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openDialog(FragmentActivity activity, Playlist playlist) {
        if (playlist != null) {
            Bundle bundle = new Bundle();
            bundle.putLong(AddSongsFragment.PLAYLIST_ARG, playlist.getPlaylistId());

            AddSongsFragment addSongsFragment = new AddSongsFragment();
            addSongsFragment.setArguments(bundle);

            addSongsFragment.show(activity.getSupportFragmentManager(), AddSongsFragment.TAG);
        }
    }

}