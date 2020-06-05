package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.Observables;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.SongController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.SoundboxObserver;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils.SongCollectionFilters;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils.SongFilters;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongCollection;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.adapters.TabLibraryPagerAdapter;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.SearchSuggestions;

/**
 * Class: LibraryFragment.java
 * SearchSuggestions activity provides searching song feature for the users
 * it is connected with fragment_library.xml, which is its content view
 */

public class LibraryFragment extends BottomNavigationTab {
    private MainActivity mainActivity;

    private SoundboxObserver<Song> addSongObserver;
    private SoundboxObserver<Song> favoriteSongObserver;

    private SharedPreferences sharedPreferences;

    private Toolbar toolbar;
    private MenuItem searchItem;

    private SearchView searchBar;

    private SongController songController;

    private List<OnDuplicateTapListener> onDuplicateTapListeners;
    private List<OnTabsChangedListener> onTabsChangedListeners;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TabLibraryPagerAdapter tabLibraryPagerAdapter;

    private List<List<SongCollection>> songCollections;
    private List<SongCollection> artists;
    private List<SongCollection> albums;
    private List<SongCollection> genres;
    private List<SongCollection> folders;
    private List<SongCollection> favorites;

    private SongCollection songs;

    private SimpleCursorAdapter simpleCursorAdapter;

    private List<String> songCollectionSortPreferences;
    private List<SongCollectionFilters.SortOrder> songCollectionSortOrders;
    private SongFilters.SortOrder songSortOrder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        addSongObserver = (observer, arg) -> updateAddSong(observer.getValue());
        favoriteSongObserver = (observer, arg) -> updateFavoriteSong(observer.getValue());

        sharedPreferences = mainActivity.getSharedPreferences(mainActivity.getString(R.string.shared_pref), Context.MODE_PRIVATE);

        songController = new SongController();

        onDuplicateTapListeners = new ArrayList<OnDuplicateTapListener>();
        onTabsChangedListeners = new ArrayList<OnTabsChangedListener>();

        songCollections = new ArrayList<List<SongCollection>>();
        artists = new ArrayList<SongCollection>();
        albums = new ArrayList<SongCollection>();
        genres = new ArrayList<SongCollection>();
        folders = new ArrayList<SongCollection>();
        favorites = new ArrayList<SongCollection>();

        songCollections.addAll(Arrays.asList(artists, albums, genres, folders, favorites));

        songs = new SongCollection();

        simpleCursorAdapter = SearchSuggestions.createSimpleCursorAdapter(mainActivity);

        songCollectionSortPreferences = Arrays.asList(
                getString(R.string.shared_pref_sort_artists_tab),
                getString(R.string.shared_pref_sort_albums_tab),
                getString(R.string.shared_pref_sort_genres_tab),
                getString(R.string.shared_pref_sort_folders_tab),
                getString(R.string.shared_pref_sort_favorites_tab)
        );
        songCollectionSortOrders = new ArrayList<SongCollectionFilters.SortOrder>();
        for (String preference : songCollectionSortPreferences) {
            songCollectionSortOrders.add(SongCollectionFilters.SortOrder.fromOrdinal(sharedPreferences.getInt(preference, 0)));
        }

        songSortOrder = SongFilters.SortOrder.fromOrdinal(sharedPreferences.getInt(getString(R.string.shared_pref_sort_tracklist), 0));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainActivity.changeNavigationView(2);

        viewPager = (ViewPager2) view.findViewById(R.id.library_tab_view_pager);
        tabLayout = (TabLayout) view.findViewById(R.id.tablayout);

        initToolbar();
        initSearchBar();
        initTabLayout();
    }

    private void initToolbar() {
        toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_library));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                switch (itemId) {
                    case R.id.action_search_library:
                        return true;
                    case R.id.action_sort_default:
                    case R.id.action_sort_name:
                    case R.id.action_sort_artist:
                    case R.id.action_sort_album:
                    case R.id.action_sort_genre:
                        int currentTab = viewPager.getCurrentItem();

                        if (currentTab < songCollections.size()) {
                            if (itemId == R.id.action_sort_default)
                                songCollectionSortOrders.set(currentTab, SongCollectionFilters.SortOrder.DEFAULT);
                            if (itemId == R.id.action_sort_name)
                                songCollectionSortOrders.set(currentTab, SongCollectionFilters.SortOrder.NAME);

                            sharedPreferences.edit().putInt(songCollectionSortPreferences.get(currentTab), songCollectionSortOrders.get(currentTab).ordinal()).apply();
                        } else {
                            if (itemId == R.id.action_sort_default)
                                songSortOrder = SongFilters.SortOrder.DEFAULT;
                            if (itemId == R.id.action_sort_name)
                                songSortOrder = SongFilters.SortOrder.NAME;
                            if (itemId == R.id.action_sort_artist)
                                songSortOrder = SongFilters.SortOrder.ARTIST;
                            if (itemId == R.id.action_sort_album)
                                songSortOrder = SongFilters.SortOrder.ALBUM;
                            if (itemId == R.id.action_sort_genre)
                                songSortOrder = SongFilters.SortOrder.GENRE;

                            sharedPreferences.edit().putInt(getString(R.string.shared_pref_sort_tracklist), songSortOrder.ordinal()).apply();
                        }

                        if (!item.isChecked() && item.isCheckable()) {
                            item.setChecked(true);
                            populateSongCollections();
                            notifyTabChanged(currentTab);
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });

        toolbar.inflateMenu(R.menu.menu_sort);
        toolbar.inflateMenu(R.menu.menu_library);

        searchItem = toolbar.getMenu().findItem(R.id.action_search_library);
    }

    private void initTabLayout() {
        /**
         * // From the docs https://developer.android.com/guide/navigation/navigation-swipe-view-2
         *
         * Settings up the tablayout with a viewpager2 object
         */

        tabLibraryPagerAdapter = new TabLibraryPagerAdapter(this);
        tabLibraryPagerAdapter.setData(songCollections, songs);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            int lastPosition = -1;

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (lastPosition != position) {
                    if (lastPosition >= 0 && searchItem.isActionViewExpanded())
                        searchItem.collapseActionView();

                    if (position < songCollections.size()) {
                        showSortOptions(R.id.action_sort_default, R.id.action_sort_name);

                        switch (songCollectionSortOrders.get(position)) {
                            case DEFAULT:
                                toolbar.getMenu().findItem(R.id.action_sort_default).setChecked(true);
                                break;
                            case NAME:
                                toolbar.getMenu().findItem(R.id.action_sort_name).setChecked(true);
                                break;
                        }
                    } else {
                        showSortOptions();

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
                }

                lastPosition = position;
            }
        });
        viewPager.setAdapter(tabLibraryPagerAdapter);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.clearOnTabSelectedListeners();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Pass
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Pass
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText(R.string.tab_artists);
                        break;
                    case 1:
                        tab.setText(R.string.tab_albums);
                        break;
                    case 2:
                        tab.setText(R.string.tab_genres);
                        break;
                    case 3:
                        tab.setText(R.string.tab_folders);
                        break;
                    case 4:
                        tab.setText(R.string.tab_favorites);
                        break;
                    case 5:
                        tab.setText(R.string.tab_songs);
                        break;
                    default:
                        tab.setText("Extras");
                        break;
                }
            }
        }).attach();
    }

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
                int currentTab = viewPager.getCurrentItem();

                if (currentTab < songCollections.size())
                    SearchSuggestions.populateAdapterCollections(simpleCursorAdapter, newText, songCollections.get(currentTab));
                else
                    SearchSuggestions.populateAdapterSongs(simpleCursorAdapter, newText, songs);

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                int currentTab = viewPager.getCurrentItem();

                if (currentTab < songCollections.size()) {
                    List<SongCollection> collection = songCollections.get(currentTab);
                    List<SongCollection> filteredCollection = SongCollectionFilters.getSongCollectionsByNameLike(query, collection);

                    collection.clear();
                    collection.addAll(filteredCollection);

                    notifyTabChanged(currentTab);
                } else {
                    List<Song> filteredSongs = SongFilters.getSongsLike(query, songs.getSongs());

                    songs.clearSongs();
                    songs.setComparator(null);
                    songs.insertSongs(filteredSongs.subList(0, Math.min(filteredSongs.size(), SearchSuggestions.SEARCH_LIST_LIMIT)));

                    notifyTabChanged(currentTab);
                }

                searchBar.clearFocus();

                return false;
            }
        });

        searchBar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                updateList();

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
                updateList();

                searchBar.clearFocus();

                return true;
            }
        });
    }

    public void addOnDuplicateTapListener(OnDuplicateTapListener onDuplicateTapListener) {
        if (onDuplicateTapListener != null)
            this.onDuplicateTapListeners.add(onDuplicateTapListener);
    }

    public void addOnTabsChangedListener(OnTabsChangedListener onTabsChangedListener) {
        if (onTabsChangedListener != null)
            this.onTabsChangedListeners.add(onTabsChangedListener);
    }

    public void removeOnDuplicateTapListener(OnDuplicateTapListener onDuplicateTapListener) {
        if (onDuplicateTapListener != null)
            this.onDuplicateTapListeners.remove(onDuplicateTapListener);
    }

    public void removeOnTabsChangedListener(OnTabsChangedListener onTabsChangedListener) {
        if (onTabsChangedListener != null)
            this.onTabsChangedListeners.remove(onTabsChangedListener);
    }

    private void hideSortOptions() {
        SubMenu subMenu = toolbar.getMenu().findItem(R.id.action_sort).getSubMenu();
        for (int i = 0; i < subMenu.size(); i++) {
            MenuItem item = subMenu.getItem(i);
            if (item.isVisible()) item.setVisible(false);
        }
    }

    private void showSortOptions() {
        SubMenu subMenu = toolbar.getMenu().findItem(R.id.action_sort).getSubMenu();
        for (int i = 0; i < subMenu.size(); i++) {
            MenuItem item = subMenu.getItem(i);
            if (!item.isVisible()) item.setVisible(true);
        }
    }

    private void showSortOptions(int... resIds) {
        hideSortOptions();

        SubMenu subMenu = toolbar.getMenu().findItem(R.id.action_sort).getSubMenu();
        for (int i = 0; i < resIds.length; i++) {
            MenuItem item = subMenu.findItem(resIds[i]);
            if (item != null && !item.isVisible()) {
                item.setVisible(true);
            }
        }
    }

    private void populateSongCollections() {
        List<Song> allSongs = songController.getAllSongs();

        artists.clear();
        albums.clear();
        genres.clear();
        folders.clear();

        songs.clearSongs();

        artists.addAll(SongFilters.groupSongsByArtist(allSongs));
        albums.addAll(SongFilters.groupSongsByAlbum(allSongs));
        genres.addAll(SongFilters.groupSongsByGenre(allSongs));
        folders.addAll(SongFilters.groupSongsByFolder(allSongs));
        populateFavorites(allSongs);

        songs.insertSongs(allSongs);

        for (int i = 0; i < songCollections.size(); i++) {
            if (songCollectionSortOrders.get(i).equals(SongCollectionFilters.SortOrder.NAME))
                Collections.sort(songCollections.get(i), SongCollection::compareToByName);
        }

        if (songSortOrder.equals(SongFilters.SortOrder.DEFAULT))
            songs.setComparator(Song::compareToById);
        if (songSortOrder.equals(SongFilters.SortOrder.NAME))
            songs.setComparator(Song::compareToByName);
        if (songSortOrder.equals(SongFilters.SortOrder.ARTIST))
            songs.setComparator(Song::compareToByArtist);
        if (songSortOrder.equals(SongFilters.SortOrder.ALBUM))
            songs.setComparator(Song::compareToByAlbum);
        if (songSortOrder.equals(SongFilters.SortOrder.GENRE))
            songs.setComparator(Song::compareToByGenre);
    }

    private void populateFavorites(List<Song> allSongs) {
        favorites.clear();

        favorites.addAll(SongFilters.groupSongsByFavorites(mainActivity, allSongs));
    }

    private void registerObservers() {
        Observables.getAddSongPlaybackQueueObservable().addObserver(addSongObserver);
        Observables.getFavoriteSongObservable().addObserver(favoriteSongObserver);
    }

    private void unregisterObservers() {
        Observables.getAddSongPlaybackQueueObservable().deleteObserver(addSongObserver);
        Observables.getFavoriteSongObservable().deleteObserver(favoriteSongObserver);
    }

    public void updateAddSong(Song song) {
        updateList();
    }

    public void updateFavoriteSong(Song song) {
        populateFavorites(songs.getSongs());
        notifyTabChanged(songCollections.indexOf(favorites));
    }

    public void updateList() {
        populateSongCollections();
        notifyTabChanged(-1);
    }

    private void notifyTabChanged(int tabChanged) {
        for (OnTabsChangedListener onTabsChangedListener : onTabsChangedListeners) {
            if (onTabsChangedListener != null)
                onTabsChangedListener.onTabsChanged(tabChanged);
        }
    }

    @Override
    public void onDuplicateTap() {
        for (OnDuplicateTapListener onDuplicateTapListener : onDuplicateTapListeners) {
            if (onDuplicateTapListener != null)
                onDuplicateTapListener.onDuplicateTap();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        searchBar.clearFocus();
        if (!searchItem.isActionViewExpanded()) // If the user has searched then do not change the results
            updateList();
        registerObservers();
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterObservers();
    }

    @Override
    public void onDestroy() {
        unregisterObservers();

        super.onDestroy();
    }

    public interface OnDuplicateTapListener {
        void onDuplicateTap();
    }

    public interface OnTabsChangedListener {
        void onTabsChanged(int tabChanged); //pass your object types.
    }

}
