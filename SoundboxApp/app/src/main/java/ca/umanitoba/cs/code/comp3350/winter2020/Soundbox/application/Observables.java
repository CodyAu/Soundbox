package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.DeleteSongPlaybackQueueObservable;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.FavoriteSongObservable;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.PlaybackModeObservable;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.PlaybackShuffleObservable;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.PlaybackSongObservable;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.PlaybackStateObservable;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.AddSongPlaybackQueueObservable;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.SwapSongPlaybackQueueObservable;

public class Observables {
    private static PlaybackStateObservable playbackStateObservable = null;
    private static PlaybackSongObservable playbackSongObservable = null;
    private static PlaybackModeObservable playbackModeObservable = null;
    private static PlaybackShuffleObservable playbackShuffleObservable = null;
    private static AddSongPlaybackQueueObservable addSongPlaybackQueueObservable = null;
    private static DeleteSongPlaybackQueueObservable deleteSongPlaybackQueueObservable = null;
    private static SwapSongPlaybackQueueObservable swapSongPlaybackQueueObservable = null;
    private static FavoriteSongObservable favoriteSongObservable = null;

    public static synchronized PlaybackStateObservable getPlaybackStateObservable() {
        if (playbackStateObservable == null)
            playbackStateObservable = new PlaybackStateObservable();
        return playbackStateObservable;
    }

    public static synchronized PlaybackModeObservable getPlaybackModeObservable() {
        if (playbackModeObservable == null) playbackModeObservable = new PlaybackModeObservable();
        return playbackModeObservable;
    }

    public static synchronized PlaybackSongObservable getPlaybackSongObservable() {
        if (playbackSongObservable == null) playbackSongObservable = new PlaybackSongObservable();
        return playbackSongObservable;
    }

    public static synchronized PlaybackShuffleObservable getPlaybackShuffleObservable() {
        if (playbackShuffleObservable == null)
            playbackShuffleObservable = new PlaybackShuffleObservable();
        return playbackShuffleObservable;
    }

    public static synchronized AddSongPlaybackQueueObservable getAddSongPlaybackQueueObservable() {
        if (addSongPlaybackQueueObservable == null)
            addSongPlaybackQueueObservable = new AddSongPlaybackQueueObservable();
        return addSongPlaybackQueueObservable;
    }

    public static synchronized DeleteSongPlaybackQueueObservable getDeleteSongPlaybackQueueObservable() {
        if (deleteSongPlaybackQueueObservable == null)
            deleteSongPlaybackQueueObservable = new DeleteSongPlaybackQueueObservable();
        return deleteSongPlaybackQueueObservable;
    }

    public static synchronized SwapSongPlaybackQueueObservable getSwapSongPlaybackQueueObservable() {
        if (swapSongPlaybackQueueObservable == null)
            swapSongPlaybackQueueObservable = new SwapSongPlaybackQueueObservable();
        return swapSongPlaybackQueueObservable;
    }

    public static synchronized FavoriteSongObservable getFavoriteSongObservable() {
        if (favoriteSongObservable == null)
            favoriteSongObservable = new FavoriteSongObservable();
        return favoriteSongObservable;
    }

    public static synchronized void clear() {
        if (playbackStateObservable != null) playbackStateObservable.deleteObservers();
        if (playbackSongObservable != null) playbackSongObservable.deleteObservers();
        if (playbackModeObservable != null) playbackModeObservable.deleteObservers();
        if (playbackShuffleObservable != null) playbackShuffleObservable.deleteObservers();
        if (addSongPlaybackQueueObservable != null)
            addSongPlaybackQueueObservable.deleteObservers();
        if (deleteSongPlaybackQueueObservable != null)
            deleteSongPlaybackQueueObservable.deleteObservers();
        if (swapSongPlaybackQueueObservable != null)
            swapSongPlaybackQueueObservable.deleteObservers();
        if (favoriteSongObservable != null)
            favoriteSongObservable.deleteObservers();

        playbackStateObservable = null;
        playbackSongObservable = null;
        playbackModeObservable = null;
        playbackShuffleObservable = null;
        addSongPlaybackQueueObservable = null;
        deleteSongPlaybackQueueObservable = null;
        swapSongPlaybackQueueObservable = null;
        favoriteSongObservable = null;
    }

}
