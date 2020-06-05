package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import java.util.List;
import java.util.Objects;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.Observables;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongStatistic;

/**
 * Deals with the playback state and logic with the currently playing song. Interacts with PlayingFragment to
 * display the state to the user.
 */
public class PlaybackController {
    private static final int LISTEN_TIME_INTERVAL = 1000;
    private static final double PLAY_TIME_THRESHOLD = 0.9;

    public enum PlaybackState {
        PLAYING,
        PAUSED
    }

    public enum PlaybackMode {
        ONCE_PLAYLIST,
        LOOP_CURRENT,
        LOOP_PLAYLIST
    }

    private static Context context;
    private static SongController songController;

    private static PlaybackState state;
    private static PlaybackMode mode;
    private static float volume;
    private static int audioSessionId;
    private static PlaybackQueue queue;
    private static MediaPlayer media;
    private static boolean isPrepared;
    private static boolean isPreparing;

    private static Handler handler;
    private static Runnable runnable;
    private static int lastPosition = 0;
    private static int listenTime = 0;

    public static void init(final Context musicContext, final MediaPlayer mediaPlayer, PlaybackQueue playbackQueue, SongController controller) {
        cleanMediaPlayer();

        context = musicContext;
        setSongController(controller);

        media = mediaPlayer != null ? mediaPlayer : new MediaPlayer();
        queue = playbackQueue != null ? playbackQueue : new PlaybackQueue();

        state = PlaybackState.PAUSED;
        mode = PlaybackMode.ONCE_PLAYLIST;
        setVolume(1.0f);
        audioSessionId = media.getAudioSessionId();

        Observables.getPlaybackStateObservable().setValue(state);
        Observables.getPlaybackModeObservable().setValue(mode);

        // MediaPlayer setup
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            media.setAudioAttributes(
                    new AudioAttributes
                            .Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                            .build()
            );
        } else {
            media.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }

        //listeners
        media.setOnCompletionListener(__ -> {
            if (state.equals(PlaybackState.PLAYING)) { //figure out the next song.
                //update song stats
                SongStatistic temp = queue.getCurrentSong().getStatisticByType(SongStatistic.Statistic.PLAYS);
                if (temp != null && listenTime * 1000 > PLAY_TIME_THRESHOLD * getDuration()) {
                    temp.incrementValue();
                }

                // play next song
                if (mode.equals(PlaybackMode.LOOP_CURRENT)) replay();
                else playNext();
            }
        });
        media.setOnPreparedListener(__ -> onSetupSongPrepared());

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (queue.getCurrentSong() != null) {
                    int currPosition = getPosition();
                    int diffPosition = currPosition - lastPosition;

                    if (diffPosition > 0) {
                        if (diffPosition >= 1000) {
                            int listenTimeIncrement = diffPosition / 1000;

                            SongStatistic listenStat = queue.getCurrentSong().getStatisticByType(SongStatistic.Statistic.LISTEN_TIME);
                            listenStat.setValue(listenStat.getValue() + listenTimeIncrement);
                            listenTime += listenTimeIncrement;

                            lastPosition = currPosition - (diffPosition % 1000);
                        }
                    } else {
                        lastPosition = currPosition;
                    }
                }
                handler.postDelayed(this, LISTEN_TIME_INTERVAL);
            }
        };
        handler.postDelayed(runnable, LISTEN_TIME_INTERVAL);
    }

    public static void init(final Context musicContext, final MediaPlayer mediaPlayer, SongController controller) {
        init(musicContext, mediaPlayer, null, controller);
    }

    //Plays the song. Returns true if the song is played successfully.
    public static boolean play() {
        PlaybackState previousState = state;
        if (isPreparing || (!isPrepared && replay())) {
            state = PlaybackState.PLAYING;
        } else if (!media.isPlaying() && isPrepared) {
            media.start();
            state = media.isPlaying() ? PlaybackState.PLAYING : PlaybackState.PAUSED;
        } else return false;

        if (state != previousState) Observables.getPlaybackStateObservable().setValue(state);
        return state.equals(PlaybackState.PLAYING);
    }

    //Pauses the song. Returns true if the song is pauses successfully.
    public static boolean pause() {
        PlaybackState previousState = state;
        if (isPreparing || (!isPrepared && replay())) {
            state = PlaybackState.PAUSED;
        } else if (media.isPlaying() && isPrepared) {
            media.pause();
            state = media.isPlaying() ? PlaybackState.PLAYING : PlaybackState.PAUSED;
        } else return false;

        if (state != previousState) Observables.getPlaybackStateObservable().setValue(state);
        return state.equals(PlaybackState.PAUSED);
    }

    //Toggles the playing state. Returns true if the song is toggled correctly. Use getPlaybackState to find which state it is in.
    public static boolean toggle() {
        if (!media.isPlaying())
            return play();
        else if (media.isPlaying())
            return pause();
        return false;
    }

    public static void seekTo(int progress) {
        int newProgresss = Math.max(progress, 0);
        media.seekTo(newProgresss);

        lastPosition = newProgresss;
    }

    /**
     * Sets the volume of the mediaPlayer
     *
     * @param scaledVolume Scaled volume value between 0.0f and 1.0f
     */
    public static void setVolume(final float scaledVolume) {
        float clampScaledVolume = Math.min(Math.max(scaledVolume, 0.0f), 1.0f); //clamp value between 0.0f - 1.0f
        float rawVolume = (float) Math.log10(1.0f + 9.0f * clampScaledVolume);
        volume = clampScaledVolume;

        if (media != null) media.setVolume(rawVolume, rawVolume);
    }

    //restarts current song
    public static boolean replay() {
        updateSongOnSwitch();

        return setupMediaPlayer(queue.getCurrentSong());
    }

    public static boolean playNext() {
        if (!queue.isEmpty()) {
            updateSongOnSwitch();

            queue.jumpNext();
            return setupMediaPlayer(queue.getCurrentSong());
        }
        return false;
    }

    public static boolean playPrevious() {
        if (!queue.isEmpty()) {
            updateSongOnSwitch();

            queue.jumpPrevious();
            return setupMediaPlayer(queue.getCurrentSong());
        }
        return false;
    }

    public static void shuffle() {
        queue.shuffleQueue();
        Observables.getPlaybackShuffleObservable().setValue(true);
    }

    public static PlaybackMode toggleLoop() {
        if (!mode.equals(PlaybackMode.LOOP_CURRENT)) mode = PlaybackMode.LOOP_CURRENT;
        else mode = PlaybackMode.ONCE_PLAYLIST;

        Observables.getPlaybackModeObservable().setValue(mode);

        return mode;
    }

    private static boolean setupMediaPlayer(Song song) {
        if (song != null) {
            try { //Try to set datasource and initialize media
                media.stop();
                media.reset(); //Reset the mediaplayer for the next song.

                media.setDataSource(context, Uri.parse(song.getFilepath()));
                media.prepareAsync();
                isPrepared = false;
                isPreparing = true;
                return true;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            } catch (java.lang.NullPointerException e) { // Can happen if external directory file does not exist
                e.printStackTrace();
            }
        }
        onSetupSongFailed();
        return false;
    }

    private static void onSetupSongPrepared() {
        boolean isPaused = state.equals(PlaybackState.PAUSED);
        isPrepared = true;
        isPreparing = false;

        if (isPaused) pause();
        else play();
        Observables.getPlaybackSongObservable().setValue(queue.getCurrentSong());
    }

    private static void onSetupSongFailed() {
        PlaybackState previousState = state;

        isPrepared = false;
        isPreparing = false;
        state = PlaybackState.PAUSED;

        media.stop();
        media.reset();

        if (state != previousState) Observables.getPlaybackStateObservable().setValue(state);
        Observables.getPlaybackSongObservable().setValue(queue.getCurrentSong());
    }

    private static void updateSongOnSwitch() {
        //update current song info before switch
        songController.updateSong(queue.getCurrentSong());
        lastPosition = 0;
        listenTime = 0;
    }

    /**
     * @param index of the song to setup based on the playback queue
     * @return if the song was successfully setup
     */
    public static boolean setSongByIndex(int index) {
        Song song = queue.peekIndex(index);
        boolean sameSong = index == queue.getIndex(); // Even if it is the same song, it could be in different adapter positions so switch anyways.

        if (!sameSong) {
            if (!queue.getQueue().contains(song)) addPlaybackQueue(song);

            updateSongOnSwitch();

            queue.jumpIndex(index);
        }

        //Try to initialize media
        return sameSong || setupMediaPlayer(song);
    }

    /**
     * Set song based on ID (Look it up in database).
     *
     * @param id songId in the database
     * @return if the song was successfully setup
     */
    public static boolean setSong(long id) {
        Song song = songController.getSongById(id);

        return setSong(song);
    }

    //Set up a new song
    public static boolean setSong(Song song) {
        boolean sameSong = Objects.equals(queue.getCurrentSong(), song);

        if (!sameSong) {
            if (!queue.getQueue().contains(song)) addPlaybackQueue(song);

            updateSongOnSwitch();

            queue.jumpSong(song);
        }

        //Try to initialize media
        return sameSong || setupMediaPlayer(song);
    }

    public static void addPlaybackQueue(PlaybackQueue newQueue) {
        Song song = queue.getCurrentSong();
        queue.addSongs(newQueue);
        Observables.getAddSongPlaybackQueueObservable().setValue(null);
        if (!Objects.equals(song, queue.getCurrentSong()) && queue.getCurrentSong() != null)
            setupMediaPlayer(queue.getCurrentSong());
    }

    public static void addPlaybackQueue(Playlist playlist) {
        Song song = queue.getCurrentSong();
        queue.addSongs(playlist);
        Observables.getAddSongPlaybackQueueObservable().setValue(null);
        if (!Objects.equals(song, queue.getCurrentSong()) && queue.getCurrentSong() != null)
            setupMediaPlayer(queue.getCurrentSong());
    }

    public static void addPlaybackQueue(List<Song> songs) {
        Song song = queue.getCurrentSong();
        queue.addSongs(songs);
        Observables.getAddSongPlaybackQueueObservable().setValue(null);
        if (!Objects.equals(song, queue.getCurrentSong()) && queue.getCurrentSong() != null)
            setupMediaPlayer(queue.getCurrentSong());
    }

    public static void setPlaybackQueue(PlaybackQueue newQueue) {
        Song song = queue.getCurrentSong();
        queue.setSongs(newQueue);
        Observables.getAddSongPlaybackQueueObservable().setValue(null);
        if (!Objects.equals(song, queue.getCurrentSong()) && queue.getCurrentSong() != null)
            setupMediaPlayer(queue.getCurrentSong());
    }

    public static void setPlaybackQueue(Playlist playlist) {
        Song song = queue.getCurrentSong();
        queue.setSongs(playlist);
        Observables.getAddSongPlaybackQueueObservable().setValue(null);
        if (!Objects.equals(song, queue.getCurrentSong()) && queue.getCurrentSong() != null)
            setupMediaPlayer(queue.getCurrentSong());
    }

    public static void setPlaybackQueue(List<Song> songs) {
        Song song = queue.getCurrentSong();
        queue.setSongs(songs);
        Observables.getAddSongPlaybackQueueObservable().setValue(null);
        if (!Objects.equals(song, queue.getCurrentSong()) && queue.getCurrentSong() != null)
            setupMediaPlayer(queue.getCurrentSong());
    }

    public static boolean addPlaybackQueue(Song song) {
        Song oldSong = queue.getCurrentSong();
        boolean added = queue.addSong(song);
        if (added) {
            Observables.getAddSongPlaybackQueueObservable().setValue(song);
            if (!Objects.equals(oldSong, queue.getCurrentSong()) && queue.getCurrentSong() != null)
                setupMediaPlayer(queue.getCurrentSong());
        }
        return added;
    }

    public static void playNext(List<Song> songs) {
        Song oldSong = queue.getCurrentSong();
        queue.playNext(songs);
        Observables.getAddSongPlaybackQueueObservable().setValue(null);
        if (!Objects.equals(oldSong, queue.getCurrentSong()) && queue.getCurrentSong() != null)
            setupMediaPlayer(queue.getCurrentSong());
    }

    public static boolean playNext(Song song) {
        Song oldSong = queue.getCurrentSong();
        boolean playNext = queue.playNext(song);
        if (playNext) {
            Observables.getAddSongPlaybackQueueObservable().setValue(null);
            if (!Objects.equals(oldSong, queue.getCurrentSong()) && queue.getCurrentSong() != null)
                setupMediaPlayer(queue.getCurrentSong());
        }
        return playNext;
    }

    public static boolean playNext(int idx) {
        Song oldSong = queue.getCurrentSong();
        boolean playNext = queue.playNext(idx);
        if (playNext) {
            Observables.getAddSongPlaybackQueueObservable().setValue(null);
            if (!Objects.equals(oldSong, queue.getCurrentSong()) && queue.getCurrentSong() != null)
                setupMediaPlayer(queue.getCurrentSong());
        }
        return playNext;
    }

    public static boolean deletePlaybackQueue(Song song) {
        Song oldSong = queue.getCurrentSong();
        boolean deleted = queue.deleteSong(song);
        if (deleted) {
            Observables.getDeleteSongPlaybackQueueObservable().setValue(null);
            if (!Objects.equals(oldSong, queue.getCurrentSong())) {
                setupMediaPlayer(queue.getCurrentSong());
            }
        }
        return deleted;
    }

    public static boolean deletePlaybackQueue(int idx) {
        Song oldSong = queue.getCurrentSong();
        boolean deleted = queue.deleteSong(idx);
        if (deleted) {
            Observables.getDeleteSongPlaybackQueueObservable().setValue(null);
            if (!Objects.equals(oldSong, queue.getCurrentSong())) {
                setupMediaPlayer(queue.getCurrentSong());
            }
        }
        return deleted;
    }

    public static void swapSongPlaybackQueue(int fromPosition, int toPosition) {
        if (queue.swapSong(fromPosition, toPosition))
            Observables.getSwapSongPlaybackQueueObservable().setValue(new int[]{fromPosition, toPosition});
    }

    public static void clearPlaybackQueue() {
        if (!queue.isEmpty()) {
            queue.clear();
            Observables.getDeleteSongPlaybackQueueObservable().setValue(null);
            onSetupSongFailed();
        }
    }

    public static void setSongController(SongController controller) {
        songController = controller;
    }

    // Get playing state of the song
    public static PlaybackState getPlaybackState() {
        return state;
    }

    // Get playing state of the song
    public static PlaybackMode getPlaybackMode() {
        return mode;
    }

    // Get current position in song
    public static int getPosition() {
        return isPrepared ? media.getCurrentPosition() : 0;
    }

    // Get duration of song
    public static int getDuration() {
        return isPrepared ? media.getDuration() : 0;
    }

    // Gets the scaled volume of the mediaPlayer
    public static float getVolume() {
        return volume;
    }

    //Retrieve the song playing
    public static Song getSong() {
        return queue.getCurrentSong();
    }

    // Retrieve the playlist queue
    public static PlaybackQueue getPlaybackQueue() {
        return queue;
    }

    public static int getMediaPlayerSessionId() {
        return audioSessionId;
    }

    public static void cleanMediaPlayer() {
        if (media != null) {
            media.stop();
            media.reset();
            media.release();
            media = null;
            isPrepared = false;
            isPreparing = false;
            audioSessionId = 0;
        }
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }

}
