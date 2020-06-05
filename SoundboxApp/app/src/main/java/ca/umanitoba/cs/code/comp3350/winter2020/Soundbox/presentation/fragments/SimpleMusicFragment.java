package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.Observables;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.SoundboxObserver;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.Playback;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Dispatchers;

public abstract class SimpleMusicFragment extends Fragment implements Playback {
    private SoundboxObserver<PlaybackController.PlaybackState> playbackStateObserver;
    private SoundboxObserver<PlaybackController.PlaybackMode> playbackModeObserver;
    private SoundboxObserver<Boolean> shuffleObserver;
    private SoundboxObserver<Song> songObserver;
    private SoundboxObserver<Song> addSongObserver;
    private SoundboxObserver<Song> deleteSongObserver;
    private SoundboxObserver<int[]> swapSongObserver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playbackStateObserver = (observer, arg) -> updateState(observer.getValue());
        playbackModeObserver = (observer, arg) -> updateMode(observer.getValue());
        shuffleObserver = (observer, arg) -> updateShuffle(observer.getValue());
        songObserver = (observer, arg) -> updateSong(observer.getValue());
        addSongObserver = (observer, arg) -> updateAddSong(observer.getValue());
        deleteSongObserver = (observer, arg) -> updateDeleteSong(observer.getValue());
        swapSongObserver = (observer, arg) -> updateSwapSong(observer.getValue());
    }

    protected void registerObservers() {
        Observables.getPlaybackStateObservable().addObserver(playbackStateObserver);
        Observables.getPlaybackModeObservable().addObserver(playbackModeObserver);
        Observables.getPlaybackShuffleObservable().addObserver(shuffleObserver);
        Observables.getPlaybackSongObservable().addObserver(songObserver);
        Observables.getAddSongPlaybackQueueObservable().addObserver(addSongObserver);
        Observables.getDeleteSongPlaybackQueueObservable().addObserver(deleteSongObserver);
        Observables.getSwapSongPlaybackQueueObservable().addObserver(swapSongObserver);
    }

    protected void unregisterObservers() {
        Observables.getPlaybackStateObservable().deleteObserver(playbackStateObserver);
        Observables.getPlaybackModeObservable().deleteObserver(playbackModeObserver);
        Observables.getPlaybackShuffleObservable().deleteObserver(shuffleObserver);
        Observables.getPlaybackSongObservable().deleteObserver(songObserver);
        Observables.getAddSongPlaybackQueueObservable().deleteObserver(addSongObserver);
        Observables.getDeleteSongPlaybackQueueObservable().deleteObserver(deleteSongObserver);
        Observables.getSwapSongPlaybackQueueObservable().deleteObserver(swapSongObserver);
    }

    public void play() {
        PlaybackController.play();
    }

    public void pause() {
        PlaybackController.pause();
    }

    public void toggle() {
        PlaybackController.toggle(); //Toggle play state when button hit
    }

    public void playNext() {
        PlaybackController.playNext();
    }

    public void playPrevious() {
        PlaybackController.playPrevious();
    }

    public void shuffle() {
        if (PlaybackController.getPlaybackQueue().isEmpty()) {
            Dispatchers.information(getActivity(), getContext().getString(R.string.shuffle_empty));
        } else {
            PlaybackController.shuffle();
            Dispatchers.information(getActivity(), getContext().getString(R.string.shuffle_success));
        }
    }

    public void loop() {
        PlaybackController.toggleLoop();
    }

    public void updateState(PlaybackController.PlaybackState state) {
        // stub
    }

    public void updateMode(PlaybackController.PlaybackMode mode) {
        // stub
    }

    public void updateShuffle(boolean shuffle){
        // stub
    }

    public void updateSong(Song song) {
        // stub
    }

    public void updateAddSong(Song song) {
        // stub
    }

    public void updateDeleteSong(Song song) {
        // stub
    }

    public void updateSwapSong(int[] positions) {
        // stub
    }

    @Override
    public void onResume() {
        super.onResume();

        registerObservers();
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterObservers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterObservers();
    }
}
