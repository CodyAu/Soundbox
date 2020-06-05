package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaylistController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.SongController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils.SongFilters;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongCollection;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.ItemTouchHelperCallback;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.adapters.TracklistAdapter;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Dispatchers;

public class TracklistFragment extends Fragment {
    public static final String TAG = "TRACKLIST_FRAGMENT";

    public static final String PLAYLIST_ARG = "playlist_id";
    public static final String SONGS_ARG = "song_ids";
    public static final String TITLE_ARG = "title";

    private MainActivity mainActivity;

    private SharedPreferences sharedPreferences;

    private Toolbar toolbar;

    private PlaylistController playlistController;

    private View emptyTracklist;

    private RecyclerView recyclerView;
    private ItemTouchHelper itemTouchHelper;
    private TracklistAdapter adapter;

    private Playlist playlist;
    private SongCollection songs;
    private String title;

    private AlertDialog dialog;

    private SongFilters.SortOrder songSortOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        sharedPreferences = mainActivity.getSharedPreferences(mainActivity.getString(R.string.shared_pref), Context.MODE_PRIVATE);

        playlistController = new PlaylistController();

        songSortOrder = SongFilters.SortOrder.fromOrdinal(sharedPreferences.getInt(getString(R.string.shared_pref_sort_tracklist), 0));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracklist, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity.changeNavigationView(-1);

        Bundle arguments = getArguments();
        if (arguments != null) {
            if (arguments.containsKey(PLAYLIST_ARG)) {
                long playlistId = arguments.getLong(PLAYLIST_ARG);
                this.playlist = playlistController.getPlaylistById(playlistId);

                if (this.playlist != null) {
                    Dispatchers.information(mainActivity, playlist.getName(), Dispatchers.LENGTH_LONG);
                }
            }
            if (arguments.containsKey(SONGS_ARG)) {
                long[] songIds = arguments.getLongArray(SONGS_ARG);
                List<Song> songs = new ArrayList<Song>();
                SongController songController = new SongController();
                for (int i = 0; i < songIds.length; i++) {
                    Song song = songController.getSongById(songIds[i]);
                    if (song != null) songs.add(song);
                }
                this.songs = new SongCollection(songs);
            }
            if (arguments.containsKey(TITLE_ARG)) {
                this.title = arguments.getString(TITLE_ARG);
            }
        }

        emptyTracklist = view.findViewById(R.id.empty_tracklist);

        int swipeFlags = playlist != null ? ItemTouchHelper.START | ItemTouchHelper.END : 0;
        ItemTouchHelperCallback callback = new ItemTouchHelperCallback(0, swipeFlags);
        callback.setItemViewSwipeEnabled(true);
        callback.setOnSwipedListener(new ItemTouchHelperCallback.OnSwipedListener() {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (playlist != null) {
                    Dispatchers.information(mainActivity, "Removed");

                    int position = viewHolder.getAdapterPosition();

                    adapter.removeSong(position);
                    playlistController.updatePlaylist(playlist);
                }

                updateEmptyText();
            }
        });
        itemTouchHelper = new ItemTouchHelper(callback);

        adapter = new TracklistAdapter(mainActivity, getSongs());
        adapter.setOnClickListener(new TracklistAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, Song song, int position) {
                PlaybackController.setSong(song);
                PlaybackController.play();
            }

            @Override
            public void onItemLongClick(View view, Song song, int position) {
                // Pass
            }
        });
        adapter.setOnStartDragListener(new TracklistAdapter.OnStartDragListener() {
            @Override
            public void onStartDrag(TracklistAdapter.SongViewHolder holder, View view, Song song, int position) {
                itemTouchHelper.startDrag(holder);
            }
        });
        adapter.setOnContextMenuClickListener(new TracklistAdapter.OnContextMenuClickListener() {
            @Override
            public void onItemClick(MenuItem menuItem, Song song, int position) {
                switch (menuItem.getItemId()) {
                    case R.id.action_remove:
                        if (playlist != null) {
                            Dispatchers.information(mainActivity, "Removed");

                            adapter.removeSong(position);
                            playlistController.updatePlaylist(playlist);
                        }

                        updateEmptyText();
                        break;
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
                return menuItem.getItemId() != R.id.action_remove || playlist != null;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), LinearLayout.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter((RecyclerView.Adapter) adapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        initToolbar();
    }

    private void initToolbar() {
        toolbar = getView().findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle(title != null ? title :
                (playlist != null ? playlist.getName() :
                        ""));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                mainActivity.onBackPressed();
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
                            getSongs().setComparator(Song::compareToById);
                        }
                        if (itemId == R.id.action_sort_name) {
                            songSortOrder = SongFilters.SortOrder.NAME;
                            getSongs().setComparator(Song::compareToByName);
                        }
                        if (itemId == R.id.action_sort_artist) {
                            songSortOrder = SongFilters.SortOrder.ARTIST;
                            getSongs().setComparator(Song::compareToByArtist);
                        }
                        if (itemId == R.id.action_sort_album) {
                            songSortOrder = SongFilters.SortOrder.ALBUM;
                            getSongs().setComparator(Song::compareToByAlbum);
                        }
                        if (itemId == R.id.action_sort_genre) {
                            songSortOrder = SongFilters.SortOrder.GENRE;
                            getSongs().setComparator(Song::compareToByGenre);
                        }

                        sharedPreferences.edit().putInt(getString(R.string.shared_pref_sort_tracklist), songSortOrder.ordinal()).apply();

                        if (!item.isChecked() && item.isCheckable()) {
                            item.setChecked(true);
                            adapter.notifyDataSetChanged();
                        }
                        return true;
                    case R.id.menu_tracklist:
                        Dispatchers.information(mainActivity, "Add Songs", Dispatchers.LENGTH_LONG);

                        AddSongsFragment.openDialog(mainActivity, playlist);
                        return true;
                    case R.id.action_edit:
                        if (playlist != null)
                            showEditPlaylistPopup(playlist);
                        return true;
                    default:
                        return false;
                }
            }
        });

        toolbar.inflateMenu(R.menu.menu_sort);
        toolbar.inflateMenu(R.menu.menu_tracklist);

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

        if (songSortOrder.equals(SongFilters.SortOrder.DEFAULT))
            getSongs().setComparator(Song::compareToById);
        if (songSortOrder.equals(SongFilters.SortOrder.NAME))
            getSongs().setComparator(Song::compareToByName);
        if (songSortOrder.equals(SongFilters.SortOrder.ARTIST))
            getSongs().setComparator(Song::compareToByArtist);
        if (songSortOrder.equals(SongFilters.SortOrder.ALBUM))
            getSongs().setComparator(Song::compareToByAlbum);
        if (songSortOrder.equals(SongFilters.SortOrder.GENRE))
            getSongs().setComparator(Song::compareToByGenre);

        if (this.playlist == null) {
            toolbar.getMenu().findItem(R.id.action_edit).setVisible(false);
            toolbar.getMenu().findItem(R.id.action_edit).setEnabled(false);
            toolbar.getMenu().findItem(R.id.menu_tracklist).setVisible(false);
            toolbar.getMenu().findItem(R.id.menu_tracklist).setVisible(false);
        }
    }

    private SongCollection getSongs() {
        SongCollection collection;
        if (this.playlist != null) {
            collection = playlist;
        } else if (this.songs != null) {
            collection = this.songs;
        } else {
            this.songs = new SongCollection();
            collection = this.songs;
        }
        return collection;
    }

    public void updateSongs() {
        adapter.notifyDataSetChanged();

        updateEmptyText();
    }

    private void updateEmptyText() {
        boolean isEmpty = adapter.getSongs().songsSize() <= 0;
        if (isEmpty && emptyTracklist.getVisibility() != View.VISIBLE)
            emptyTracklist.setVisibility(View.VISIBLE);
        else if (!isEmpty && emptyTracklist.getVisibility() != View.GONE)
            emptyTracklist.setVisibility(View.GONE);
    }

    private void showEditPlaylistPopup(Playlist playlist) {
        dialog = new AlertDialog.Builder(new ContextThemeWrapper(mainActivity, R.style.AppTheme_AlertDialog)).create();
        LayoutInflater inflater = dialog.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_playlist, null);

        final EditText editText = dialogView.findViewById(R.id.edit_comment);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonSubmit = dialogView.findViewById(R.id.buttonSubmit);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() { //So it will popup the keyboard on edit
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus)
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Dispatchers.information(getContext(), "Cancelled");
            }
        });
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlist.setName(editText.getText().toString());
                playlistController.updatePlaylist(playlist);

                toolbar.setTitle(playlist.getName());

                dialog.dismiss();
                Dispatchers.information(getContext(), "Playist edited: " + playlist.getName() + "!");
            }
        });

        dialog.setView(dialogView);
        dialog.show();

        editText.setText(playlist.getName());
        editText.requestFocus();
    }

    @Override
    public void onResume() {
        super.onResume();

        updateSongs();
    }

}
