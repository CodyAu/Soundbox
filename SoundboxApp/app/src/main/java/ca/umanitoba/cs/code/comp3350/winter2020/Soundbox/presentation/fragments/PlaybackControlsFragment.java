package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.NotificationCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.AudioLoader;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.SongController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.ViewPagerBottomSheetBehavior;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.SettingsActivity;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.adapters.ScreenSlidePagerAdapter;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.transformers.ZoomOutPageTransformer;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Calculate;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Dispatchers;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.NotificationActionService;

/**
 * Class: PlaybackControlsFragment.java
 * PlaybackControlsFragment provides currently playing song with visualization with the song info for the users
 */
public class PlaybackControlsFragment extends SimpleMusicFragment implements View.OnClickListener {
    private static final int SEEK_BAR_UPDATE_RATE = 50; // Rate in milliseconds the seek bar is updated

    private static final int SELECT_AUDIO_REQUEST = 1; // Request code for selecting files.
    private static final int SONG_TRACK_REQUEST = 2; // Request code for playing tracks.

    private static final String PLAY_ACTION = "PLAY_SONG";
    private static final String PLAY_NEXT_ACTION = "PLAY_NEXT_ACTION";
    private static final String PLAY_PREVIOUS_ACTION = "PLAY_PREVIOUS_ACTION";

    private static final int TOGGLE_LOOP_TINT = 0xFA8A8A8A;

    private MainActivity mainActivity;

    private BroadcastReceiver broadcastReceiver;

    private ViewSwitcher viewSwitcher;
    private View collapsedView, expandedView;

    private LinearLayout collapsedViewContent, expandedViewContent;

    private ViewPager2 viewPager;
    private ScreenSlidePagerAdapter screenSlidePagerAdapter;

    private ViewPagerBottomSheetBehavior bottomSheetBehavior;
    private FrameLayout bottomSheet;

    private Button collapse, settings, playlist;
    //control bar
    private Button shuffle, playprev, play, playnext, loop;

    private SeekBar seekBar;

    private TextView title, artist;
    private ImageView thumbnail;

    private TextView playbackPosition, playbackDuration, textview;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_playing_controls, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher);
        collapsedView = view.findViewById(R.id.view_collapsed);
        expandedView = view.findViewById(R.id.view_expanded);

        collapsedViewContent = (LinearLayout) view.findViewById(R.id.view_collapsed_content);
        expandedViewContent = (LinearLayout) view.findViewById(R.id.view_expanded_content);

        viewPager = (ViewPager2) view.findViewById(R.id.view_pager);

        bottomSheet = (FrameLayout) view.findViewById(R.id.playing_bottom_sheet);

        seekBar = (SeekBar) view.findViewById(R.id.seekBar);

        collapse = (Button) view.findViewById(R.id.collapse);
        settings = (Button) view.findViewById(R.id.settings);
        playlist = (Button) view.findViewById(R.id.playlist);

        playbackPosition = (TextView) view.findViewById((R.id.playback_position));
        playbackDuration = (TextView) view.findViewById((R.id.playback_duration));
        textview = (TextView) view.findViewById((R.id.textView));

        shuffle = (Button) view.findViewById(R.id.shuffle);
        loop = (Button) view.findViewById(R.id.loop);

        //setOnClickListener
        bottomSheet.setOnClickListener(this);

        collapse.setOnClickListener(this);
        settings.setOnClickListener(this);
        playlist.setOnClickListener(this);

        shuffle.setOnClickListener(this);
        loop.setOnClickListener(this);

        initViewPager();
        initBottomSheet();
        initAlternateViews(viewSwitcher.getCurrentView());
        initSeekBar();
        initBroadcastReceiver();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        updateInfo();
    }

    private void initAlternateViews(View view) {
        playprev = (Button) view.findViewById(R.id.play_prev);
        play = (Button) view.findViewById(R.id.play_bg);
        playnext = (Button) view.findViewById(R.id.play_next);

        if (viewSwitcher.getCurrentView() == collapsedView) {
            title = (TextView) view.findViewById((R.id.title));
            artist = (TextView) view.findViewById((R.id.artist));
            thumbnail = (ImageView) view.findViewById((R.id.thumbnail));

            title.setSelected(true);
            artist.setSelected(true);
        }

        //setOnClickListener
        playprev.setOnClickListener(this);
        play.setOnClickListener(this);
        playnext.setOnClickListener(this);
    }

    private void initBottomSheet() {
        viewSwitcher.setClickable(false);

        // Set the expanded view margin the statusbar height
        expandedView.setPadding(
                expandedView.getPaddingLeft(),
                Calculate.statusBarHeightResource(mainActivity),
                expandedView.getPaddingRight(),
                expandedView.getPaddingBottom()
        );

        bottomSheetBehavior = (ViewPagerBottomSheetBehavior) ViewPagerBottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setViewPager(viewPager);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        collapsedViewContent.animate().alpha(0.5f);
                        expandedViewContent.animate().alpha(0.5f);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        switchView();
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // On bottom sheet sliding
            }
        });
    }

    private void initViewPager() {
        screenSlidePagerAdapter = new ScreenSlidePagerAdapter(mainActivity, PlaybackController.getPlaybackQueue());
        viewPager.setAdapter(screenSlidePagerAdapter);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());

        // By default viewPager is not circular, that is it will not go from position 1 to the last position and visa versa. We will make it that way.
        viewPager.setCurrentItem(1, false);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position <= 0) {
                    viewPager.setCurrentItem(screenSlidePagerAdapter.getItemCount() - 2, true);
                    return;
                } else if (position >= screenSlidePagerAdapter.getItemCount() - 1) {
                    viewPager.setCurrentItem(1, true);
                    return;
                } else position -= 1;

                PlaybackController.setSongByIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
                // Pass
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Pass
            }
        });
    }

    private void initSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isUser) {
                if (isUser)
                    PlaybackController.seekTo(progress);

                playbackPosition.setText(Calculate.formatTime(progress));
                playbackDuration.setText(Calculate.formatTime(seekBar.getMax()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //update seek bar on the timer
        final Handler handler = new Handler();
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (PlaybackController.getPlaybackState().equals(PlaybackController.PlaybackState.PLAYING)) {
                    updateSeekBar();
                }

                handler.postDelayed(this, SEEK_BAR_UPDATE_RATE);
            }
        });
    }

    private void initBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getExtras().getString("actionName");

                switch (action) {
                    case PLAY_PREVIOUS_ACTION:
                        playPrevious();
                        break;
                    case PLAY_ACTION:
                        toggle();
                        break;
                    case PLAY_NEXT_ACTION:
                        playNext();
                        break;
                }
            }
        };
        mainActivity.registerReceiver(broadcastReceiver, new IntentFilter("TRACK_CONTROL"));
    }

    private void switchView() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED && viewSwitcher.getCurrentView() != collapsedView)
            viewSwitcher.showPrevious();
        else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED && viewSwitcher.getCurrentView() != expandedView)
            viewSwitcher.showNext();

        collapsedViewContent.animate().alpha(1.0f);
        expandedViewContent.animate().alpha(1.0f);

        initAlternateViews(viewSwitcher.getCurrentView());
        updateInfo();
    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mainActivity, view);

        // Attempt to set optional icons to true by casting Menu -> MenuBuilder
        try {
            MenuBuilder menuBuilder = (MenuBuilder) popup.getMenu();
            menuBuilder.setOptionalIconsVisible(true);
        } catch (ClassCastException e) {
            Log.e(PlaybackControlsFragment.class.getName(), PlaybackControlsFragment.class.getName() + ": Could not set optional icons visible.");
        }

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_settings, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_add_song:
                    AudioLoader.chooseDocumentFile(this, SELECT_AUDIO_REQUEST);
                    return true;
                case R.id.action_details:
                    DetailsFragment.openDialog(mainActivity, PlaybackController.getSong());
                    return true;
                case R.id.action_settings:
                    Intent intent = new Intent(mainActivity, SettingsActivity.class);
                    mainActivity.startActivity(intent);
                    return true;
                default:
                    return false;
            }
        });

        popup.show();
    }

    public void toggleBottomSheet() {
        if (bottomSheetBehavior != null) {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }

    public void expandBottomSheet() {
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void collapseBottomSheet() {
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    /**
     * This should only be called if the permission was not obtained previously.
     */
    public void startVisualizer() {
        if (screenSlidePagerAdapter != null)
            screenSlidePagerAdapter.startVisualizer();
    }

    public void updateSeekBar() {
        seekBar.setMax(PlaybackController.getDuration());
        seekBar.setProgress(PlaybackController.getPosition());
    }

    public boolean allowBackPress() {
        if (bottomSheetBehavior != null && bottomSheetBehavior.getState() == bottomSheetBehavior.STATE_EXPANDED) {
            collapseBottomSheet();
            return false;
        }
        return true;
    }

    //update displayed track info like title, artist etc. and update state (play button)
    public void updateInfo() {
        boolean isEmpty = PlaybackController.getPlaybackQueue().isEmpty();

        String songTitle = isEmpty ? "" : PlaybackController.getSong().getSongName();
        String songArtist = isEmpty ? "" : PlaybackController.getSong().getArtist();
        Bitmap largeIcon = isEmpty || !PlaybackController.getSong().hasThumbnail() ? null : AudioLoader.loadImageFromUri(mainActivity, Uri.parse(PlaybackController.getSong().getFilepath()));
        boolean isPlaying = PlaybackController.getPlaybackState().equals(PlaybackController.PlaybackState.PLAYING);

        int bgId = isPlaying ? R.drawable.play_background : R.drawable.pause_background;
        play.setBackgroundResource(bgId); //Switch the icon to play/pause

        title.setText(songTitle);
        artist.setText(songArtist);
        thumbnail.setImageBitmap(largeIcon);

        updateSeekBar();

        Dispatchers.mediaNotification(
                mainActivity,
                songTitle,
                songArtist,
                largeIcon,
                R.drawable.ic_music_note_black_24dp,
                isPlaying,
                new NotificationCompat.Action[]{
                        new NotificationCompat.Action.Builder(
                                R.drawable.play_prev_bg,
                                getString(R.string.play_pause),
                                PendingIntent.getBroadcast(
                                        mainActivity,
                                        SONG_TRACK_REQUEST,
                                        new Intent(mainActivity, NotificationActionService.class).setAction(PLAY_PREVIOUS_ACTION),
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                )
                        )
                                .build(),
                        new NotificationCompat.Action.Builder(
                                bgId,
                                getString(R.string.play_pause),
                                PendingIntent.getBroadcast(
                                        mainActivity,
                                        SONG_TRACK_REQUEST,
                                        new Intent(mainActivity, NotificationActionService.class).setAction(PLAY_ACTION),
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                )
                        )
                                .build(),
                        new NotificationCompat.Action.Builder(
                                R.drawable.play_next_bg,
                                getString(R.string.play_pause),
                                PendingIntent.getBroadcast(
                                        mainActivity,
                                        SONG_TRACK_REQUEST,
                                        new Intent(mainActivity, NotificationActionService.class).setAction(PLAY_NEXT_ACTION),
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                )
                        )
                                .build()
                }
        );
    }

    @Override
    public void updateState(PlaybackController.PlaybackState state) {
        updateInfo();
    }

    @Override
    public void updateSong(Song song) {
        updateInfo();

        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED && viewSwitcher.getCurrentView() != collapsedView)
            viewPager.setCurrentItem(PlaybackController.getPlaybackQueue().getIndex() + 1, true);
        else
            viewPager.setCurrentItem(PlaybackController.getPlaybackQueue().getIndex() + 1, false);
    }

    @Override
    public void updateAddSong(Song song) {
        screenSlidePagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateDeleteSong(Song song) {
        screenSlidePagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void shuffle() {
        super.shuffle();

        screenSlidePagerAdapter.notifyDataSetChanged();
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED && viewSwitcher.getCurrentView() != collapsedView)
            viewPager.setCurrentItem(PlaybackController.getPlaybackQueue().getIndex() + 1);
        else
            viewPager.setCurrentItem(PlaybackController.getPlaybackQueue().getIndex() + 1, false);
    }

    @Override
    public void loop() {
        boolean looping = PlaybackController.toggleLoop().equals(PlaybackController.PlaybackMode.LOOP_CURRENT);

        // Set onclick button effect
        if (looping) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                loop.getBackground().setColorFilter(new BlendModeColorFilter(TOGGLE_LOOP_TINT, BlendMode.SRC_ATOP));
            else
                loop.getBackground().setColorFilter(TOGGLE_LOOP_TINT, PorterDuff.Mode.SRC_ATOP);
            loop.invalidate();
        } else {
            loop.getBackground().clearColorFilter();
            loop.invalidate();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.playing_bottom_sheet:
                expandBottomSheet();
                break;
            case R.id.collapse:
                collapseBottomSheet();
                break;
            case R.id.settings:
                showPopupMenu(settings);
                break;
            case R.id.playlist:
                //intent
                break;
            case R.id.shuffle:
                shuffle();
                break;
            case R.id.play_prev:
                playPrevious();
                break;
            case R.id.play_bg:
                toggle();
                break;
            case R.id.play_next:
                playNext();
                break;
            case R.id.loop:
                loop();
                break;
            default:
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECT_AUDIO_REQUEST:
                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                    Uri uri = data.getData();

                    Song newSong = AudioLoader.loadSongFromUri(mainActivity, uri);

                    if (newSong != null) {
                        SongController controller = new SongController();
                        newSong.setSongId(controller.getNextId());
                        controller.insertSong(newSong);

                        Dispatchers.information(mainActivity, "File Selected: " + newSong.getFilepath(), Dispatchers.LENGTH_LONG);
                        PlaybackController.setSong(newSong);
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        screenSlidePagerAdapter.setWaveRenderer(null);
        mainActivity.registerReceiver(broadcastReceiver, new IntentFilter("TRACK_CONTROL"));
        updateSong(null);
    }

    @Override
    public void onPause() {
        super.onPause();

        mainActivity.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            mainActivity.unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) { //dirty catch since broadcast api doesn't allow checking for registered receievers.
            Log.e(getClass().getName(), "Receiver not registered!");
        }
    }

}
