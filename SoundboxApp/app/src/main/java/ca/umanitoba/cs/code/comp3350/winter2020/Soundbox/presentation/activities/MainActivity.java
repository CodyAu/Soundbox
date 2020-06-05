package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.AudioLoader;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.Observables;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.PermissionManager;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackQueue;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.SongController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments.AnalyticsFragment;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments.DiscoverFragment;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments.LibraryFragment;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments.PlaybackControlsFragment;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments.PlayingFragment;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments.PlaylistFragment;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments.TracklistFragment;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Themes;


/**
 * Class: MainActivity.java
 * This MainActivity is the launch page class as Playlists page
 * it is connected with activity_main.xml which is its content view
 * Connected with PlaylistAdapter.java & Playlist.java
 */
public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    private PlaylistFragment playlistFragment;
    private PlayingFragment playingFragment;
    private LibraryFragment libraryFragment;
    private DiscoverFragment discoverFragment;
    private AnalyticsFragment analyticsFragment;
    private PlaybackControlsFragment playbackControlsFragment;

    private BottomNavigationView bottomNavigationView;
    private int index = 0;

    private boolean loadedDefaults = false;
    private boolean loadedMediaStore = false;
    private boolean isDarkTheme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref), MODE_PRIVATE);
        isDarkTheme = Themes.changeThemeModeCreate(this, sharedPreferences);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // hide top status bar
        //        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Observables.clear();

        SongController controller = new SongController();
        PlaybackController.init(this, null, new PlaybackQueue(controller.getAllSongs()), controller);

        //Ask for permissions if needed
        String[] permissions;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO};
        } else {
            permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            };
        }

        String[] alreadyObtained = PermissionManager.obtainedPermissions(this, permissions);
        for (String permission : alreadyObtained)
            permissionGranted(permission); //If we have the permission already, see if there's something we should do
        PermissionManager.getPermissions(this, permissions);

        initBottomNavigation();
        initPlaybackControls();

        playlistFragment = new PlaylistFragment();
        playingFragment = new PlayingFragment();
        libraryFragment = new LibraryFragment();
        discoverFragment = new DiscoverFragment();
        analyticsFragment = new AnalyticsFragment();

        switchToPlaylistFragment(false, false);
    }

    private void initBottomNavigation() {
        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_playlists:
                    switchToPlaylistFragment(true, false);
                    break;

                case R.id.navigation_playing:
                    switchToPlayingFragment(true, false);
                    break;

                case R.id.navigation_library:
                    switchToLibraryFragment(true, false);
                    break;

                case R.id.navigation_discover:
                    switchToDiscoverFragment(true, false);
                    break;

                case R.id.navigation_analytics:
                    switchToAnalyticsFragment(true, false);
                    break;
            }
            return false;
        });
    }

    private void initPlaybackControls() {
        playbackControlsFragment = new PlaybackControlsFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.controls_placeholder, playbackControlsFragment)
                .commit();

    }

    private void initDefaultSongsLoad() {
        if (PermissionManager.obtainedPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                ((Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) || PermissionManager.obtainedPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) &&
                !loadedDefaults) {
            // Load default songs
            List<String> songsPaths = AudioLoader.loadPathsFromAssets(this);
            insertNewSongs(songsPaths);

            loadedDefaults = true;
        } else
            Log.i("AUDIO_LOAD", "Default songs could not be loaded because of insufficient permissions.");
    }

    private void initSongsLoad() {
        if (PermissionManager.obtainedPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                !loadedMediaStore) {
            // Load songs from MediaStore
            List<String> songsPaths = AudioLoader.loadPathsFromMediaStore(this);
            insertNewSongs(songsPaths);

            loadedMediaStore = true;
        } else
            Log.i("AUDIO_LOAD", "Songs could not be loaded because of insufficient permissions.");
    }

    private void insertNewSongs(List<String> newPaths) {
        SongController controller = new SongController();
        List<Song> newSongs = new ArrayList<Song>();
        long nextId = controller.getNextId();
        for (String path : newPaths) {
            boolean exists = false;
            for (Song song : controller.getAllSongs()) { // Are only checking if filepaths are equal, not entire song + id
                if (Objects.equals(song.getFilepath(), path)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                Song newSong = AudioLoader.loadSongFromPath(path);
                if (newSong != null) {
                    newSong.setSongId(nextId++);
                    newSongs.add(newSong);
                }
            }
        }
        controller.insertSongs(newSongs);

        PlaybackController.setPlaybackQueue((new SongController()).getAllSongs());
    }

    private void permissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE: //Set up current song, since it currently needs external file access to get the test song
                initDefaultSongsLoad();
                initSongsLoad();
                break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                initDefaultSongsLoad();
                break;
            case Manifest.permission.RECORD_AUDIO:
                if (playbackControlsFragment != null)
                    playbackControlsFragment.startVisualizer();
                break;
        }
    }

    public boolean changeNavigationView(int idx) {
        if (idx == -1) { // If we are pushing a fragment not in the bottom nav
            index = 0; // resId of 0.
            return true;
        }

        Menu menu = bottomNavigationView.getMenu();
        try {
            MenuItem menuItem = menu.getItem(idx);
            menuItem.setChecked(true);
            index = menuItem.getItemId();
            return true;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void switchToPlaylistFragment(boolean pushState, boolean forceReplace) {
        playbackControlsFragment.collapseBottomSheet();

        if (forceReplace || index != R.id.navigation_playlists) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction()
                    .replace(R.id.fragment_placeholder, playlistFragment);
            if (pushState) {
                if (index == R.id.navigation_playlists) manager.popBackStackImmediate();
                transaction.addToBackStack(PlaylistFragment.class.getName());
            }
            transaction.commit();
        } else playlistFragment.onDuplicateTap();
    }

    public void switchToPlayingFragment(boolean pushState, boolean forceReplace) {
        playbackControlsFragment.collapseBottomSheet();

        if (forceReplace || index != R.id.navigation_playing) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction()
                    .replace(R.id.fragment_placeholder, playingFragment);
            if (pushState) {
                if (index == R.id.navigation_playing) manager.popBackStackImmediate();
                transaction.addToBackStack(PlayingFragment.class.getName());
            }
            transaction.commit();
        } else playingFragment.onDuplicateTap();
    }

    public void switchToLibraryFragment(boolean pushState, boolean forceReplace) {
        playbackControlsFragment.collapseBottomSheet();

        if (forceReplace || index != R.id.navigation_library) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction()
                    .replace(R.id.fragment_placeholder, libraryFragment);
            if (pushState) {
                if (index == R.id.navigation_library) manager.popBackStackImmediate();
                transaction.addToBackStack(LibraryFragment.class.getName());
            }
            transaction.commit();
        } else libraryFragment.onDuplicateTap();
    }

    public void switchToDiscoverFragment(boolean pushState, boolean forceReplace) {
        playbackControlsFragment.collapseBottomSheet();

        if (forceReplace || index != R.id.navigation_discover) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction()
                    .replace(R.id.fragment_placeholder, discoverFragment);
            if (pushState) {
                if (index == R.id.navigation_discover) manager.popBackStackImmediate();
                transaction.addToBackStack(DiscoverFragment.class.getName());
            }
            transaction.commit();
        } else discoverFragment.onDuplicateTap();
    }

    public void switchToAnalyticsFragment(boolean pushState, boolean forceReplace) {
        playbackControlsFragment.collapseBottomSheet();

        if (forceReplace || index != R.id.navigation_analytics) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction()
                    .replace(R.id.fragment_placeholder, analyticsFragment);
            if (pushState) {
                if (index == R.id.navigation_analytics) manager.popBackStackImmediate();
                transaction.addToBackStack(AnalyticsFragment.class.getName());
            }
            transaction.commit();
        } else analyticsFragment.onDuplicateTap();
    }

    public void switchToTracklistFragment(boolean pushState, Bundle bundle) {
        playbackControlsFragment.collapseBottomSheet();

        TracklistFragment tracklistFragment = new TracklistFragment();
        if (bundle != null) tracklistFragment.setArguments(bundle);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction()
                .replace(R.id.fragment_placeholder, tracklistFragment, TracklistFragment.TAG);
        if (pushState) transaction.addToBackStack(TracklistFragment.class.getName());
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (playbackControlsFragment.allowBackPress()) {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count <= 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //When we get permissions, do what we weren't able to before
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == 0)
                permissionGranted(permissions[i]);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Themes.changeThemeMode(this, sharedPreferences, isDarkTheme);
    }
}
