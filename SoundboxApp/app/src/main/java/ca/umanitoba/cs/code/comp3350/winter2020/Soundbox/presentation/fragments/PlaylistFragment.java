package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaylistController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils.SongCollectionFilters;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongCollection;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.adapters.PlaylistAdapter;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Calculate;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Dispatchers;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.SearchSuggestions;

public class PlaylistFragment extends BottomNavigationTab {
    private MainActivity mainActivity;

    private SharedPreferences sharedPreferences;

    private PlaylistController playlistController;

    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private MenuItem searchItem;

    private SearchView searchBar;

    private View emptyPlaylists;

    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;

    private AppCompatButton buttonAddPlaylist;

    private AlertDialog dialog;

    private SongCollectionFilters.SortOrder playlistsSortOrder;

    private SimpleCursorAdapter simpleCursorAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        sharedPreferences = mainActivity.getSharedPreferences(mainActivity.getString(R.string.shared_pref), Context.MODE_PRIVATE);

        playlistController = new PlaylistController();

        simpleCursorAdapter = SearchSuggestions.createSimpleCursorAdapter(mainActivity);

        playlistsSortOrder = SongCollectionFilters.SortOrder.fromOrdinal(sharedPreferences.getInt(getString(R.string.shared_pref_sort_playlist), 0));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainActivity.changeNavigationView(0);

        emptyPlaylists = view.findViewById(R.id.empty_playlists);

        // Click ADD PLAYLIST then show "add playlist" dialog
        buttonAddPlaylist = view.findViewById(R.id.buttonAddPlaylist);
        buttonAddPlaylist.setOnClickListener(v -> showAddPlaylistPopup());

        initCollapsingToolbar(view);
        initSearchBar();
        initPlaylistCards(view);
    }

    //----------------------Playlist page top collapsing toolbar----------------------------

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar(View view) {
        collapsingToolbar = view.findViewById(R.id.collapsing_toolbar);
        appBarLayout = view.findViewById(R.id.appbar);
        toolbar = view.findViewById(R.id.toolbar);

        collapsingToolbar.setTitle(" ");
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) scrollRange = appBarLayout.getTotalScrollRange();

                if (scrollRange + verticalOffset == 0) {
                    isShow = true;

                    toolbar.setVisibility(View.VISIBLE);
                    showSortOptions();
                    collapsingToolbar.setTitle(getString(R.string.title_playlists));
                } else if (isShow) {
                    isShow = false;

                    hideSortOptions();
                    collapsingToolbar.setTitle(" ");
                    toolbar.setVisibility(View.GONE);
                }
            }
        });

        // Click + then show "add playlist" dialog
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                switch (itemId) {
                    case R.id.action_sort_default:
                    case R.id.action_sort_name:
                        if (itemId == R.id.action_sort_default)
                            playlistsSortOrder = SongCollectionFilters.SortOrder.DEFAULT;
                        if (itemId == R.id.action_sort_name)
                            playlistsSortOrder = SongCollectionFilters.SortOrder.NAME;

                        sharedPreferences.edit().putInt(getString(R.string.shared_pref_sort_playlist), playlistsSortOrder.ordinal()).apply();

                        if (!item.isChecked() && item.isCheckable()) {
                            item.setChecked(true);
                            populatePlaylists();
                        }
                        return true;
                    case R.id.action_add_playlist:
                        showAddPlaylistPopup();
                        return true;
                    default:
                        return false;
                }
            }
        });

        toolbar.inflateMenu(R.menu.menu_sort);
        toolbar.inflateMenu(R.menu.menu_playlist);

        SubMenu subMenu = toolbar.getMenu().findItem(R.id.action_sort).getSubMenu();
        for (int i = 0; i < subMenu.size(); i++) {
            MenuItem item = subMenu.getItem(i);
            if (item.isVisible()) item.setVisible(false);
        }
        // Only show two relevant options for sorting playlists
        subMenu.findItem(R.id.action_sort_default).setVisible(true);
        subMenu.findItem(R.id.action_sort_name).setVisible(true);

        // Set the correct checked sort filter
        switch (playlistsSortOrder) {
            case DEFAULT:
                toolbar.getMenu().findItem(R.id.action_sort_default).setChecked(true);
                break;
            case NAME:
                toolbar.getMenu().findItem(R.id.action_sort_name).setChecked(true);
                break;
            default:
                break;
        }

        // Initially hide the options when collapsing toolbar expanded
        hideSortOptions();

        searchItem = toolbar.getMenu().findItem(R.id.action_search_playlists);
    }
    //----------------------END: Playlist page top collapsing toolbar----------------------------

    private void initSearchBar() {
        try {
            searchBar = (SearchView) searchItem.getActionView();
        } catch (Exception e) {
            e.printStackTrace();
        }

        searchBar.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchBar.setIconified(true);
        searchBar.setFocusable(true);
        searchBar.setFocusableInTouchMode(true);
        searchBar.setQueryHint(mainActivity.getString(R.string.search_playlists));
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
                SearchSuggestions.populateAdapterCollections(simpleCursorAdapter, newText, adapter.getPlaylists());

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                List<Playlist> collection = adapter.getPlaylists();
                List<Playlist> filteredCollection = SongCollectionFilters.getSongCollectionsByNameLike(query, collection);

                adapter.clearPlaylists();
                adapter.addAll(filteredCollection);

                searchBar.clearFocus();

                return false;
            }
        });

        searchBar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                populatePlaylists();

                searchBar.clearFocus();

                return true;
            }
        });
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                populatePlaylists();

                searchBar.clearFocus();

                return true;
            }
        });
    }

    //----------------------Playlist cardview----------------------------

    private void initPlaylistCards(View view) {
        recyclerView = view.findViewById(R.id.playlist_recycler_view);
        adapter = new PlaylistAdapter(mainActivity, null);
        adapter.setOnItemClickListener(new PlaylistAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, Playlist playlist, int position) {
                Bundle bundle = new Bundle();
                bundle.putLong(TracklistFragment.PLAYLIST_ARG, playlist.getPlaylistId());
                bundle.putString(TracklistFragment.TITLE_ARG, playlist.getName());

                mainActivity.switchToTracklistFragment(true, bundle);
            }
        });
        adapter.setOnContextMenuClickListener(new PlaylistAdapter.OnContextMenuClickListener() {
            @Override
            public void onItemClick(MenuItem menuItem, Playlist playlist, int position) {
                switch (menuItem.getItemId()) {
                    case R.id.action_edit:
                        showEditPlaylistPopup(playlist, position);
                        break;
                    case R.id.action_remove:
                        Dispatchers.information(mainActivity, "Removed");

                        playlistController.deletePlaylist(playlist);
                        adapter.removePlaylist(position);
                        updateEmptyText();

                        break;
                    case R.id.action_play_next:
                        Dispatchers.information(mainActivity, "Play next");

                        PlaybackController.playNext(playlist.getSongs());
                        break;
                    case R.id.action_add_queue:
                        Dispatchers.information(mainActivity, "Added to queue");

                        PlaybackController.addPlaybackQueue(playlist.getSongs());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public boolean filterItem(MenuItem menuItem, Playlist playlist, int position) {
                return true;
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mainActivity, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, Calculate.dpToPx(mainActivity, 10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        populatePlaylists();
    }

    private void hideSortOptions() {
        Menu menu = toolbar.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.isVisible()) item.setVisible(false);
        }
    }

    private void showSortOptions() {
        Menu menu = toolbar.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (!item.isVisible()) item.setVisible(true);
        }
    }

    /**
     * Create playlists in reverse order of creation date
     */
    private void populatePlaylists() {
        List<Playlist> playlists = new ArrayList<Playlist>();

        playlists.addAll(playlistController.getAllPlaylists());
        Collections.reverse(playlists);

        if (playlistsSortOrder.equals(SongCollectionFilters.SortOrder.NAME))
            Collections.sort(playlists, SongCollection::compareToByName);

        adapter.clearPlaylists();
        adapter.addAll(playlists);
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    //----------------------Playlist cardview----------------------------


    //--------------------------------add playlist---------------------------------

    /**
     * Add new playlists when user clicks the add playlist button
     */
    private void showAddPlaylistPopup() {
        dialog = new AlertDialog.Builder(new ContextThemeWrapper(mainActivity, R.style.AppTheme_AlertDialog)).create();
        LayoutInflater inflater = dialog.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_playlist, null);

        final EditText editText = dialogView.findViewById(R.id.edit_comment);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonSubmit = dialogView.findViewById(R.id.buttonSubmit);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() { //So it will popup the keyboard on add
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
                addPlaylist(editText.getText().toString(), R.drawable.playlist1);
                dialog.dismiss();
                Dispatchers.information(getContext(), "New playlist: " + editText.getText().toString() + " is created!");
            }
        });

        dialog.setView(dialogView);
        dialog.show();

        editText.requestFocus();
    }

    private void showEditPlaylistPopup(Playlist playlist, int position) {
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
                adapter.notifyItemChanged(position);

                dialog.dismiss();
                Dispatchers.information(getContext(), "Playlist edited: " + editText.getText().toString() + "!");
            }
        });

        dialog.setView(dialogView);
        dialog.show();

        editText.setText(playlist.getName());
        editText.requestFocus();
    }

    private void updateEmptyText() {
        boolean isEmpty = adapter.getPlaylists().size() <= 0;
        if (isEmpty && emptyPlaylists.getVisibility() != View.VISIBLE)
            emptyPlaylists.setVisibility(View.VISIBLE);
        else if (!isEmpty && emptyPlaylists.getVisibility() != View.GONE)
            emptyPlaylists.setVisibility(View.GONE);
    }

    /**
     * Add new playlists when user clicks the add playlist button
     */
    private void addPlaylist(String name, int thumbnail) {
        long playlistId = playlistController.getNextId();
        Playlist playlist = new Playlist(playlistId, name, thumbnail);

        int index = 0;
        if (playlistsSortOrder.equals(SongCollectionFilters.SortOrder.NAME)) {
            index = Collections.binarySearch(adapter.getPlaylists(), playlist, SongCollection::compareToByName);
            if (index < 0) index = ~index;
        }

        playlistController.insertPlaylist(playlist);
        adapter.addPlaylist(index, playlist);

        updateEmptyText();
    }
    //--------------------------------add playlist---------------------------------

    @Override
    public void onDuplicateTap() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (linearLayoutManager.isSmoothScrolling()) {
            linearLayoutManager.scrollToPositionWithOffset(0, 0);
        } else {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        updateEmptyText();
    }
}
