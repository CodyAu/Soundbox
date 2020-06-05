package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.CalculateTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackControllerTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackQueueTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaylistControllerTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaylistValidatorTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.SongControllerTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.SongStatisticControllerTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.SongValidatorTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.AddSongPlaybackQueueObservableTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.DeleteSongPlaybackQueueObservableTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.FavoriteSongObservableTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.PlaybackModeObservableTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.PlaybackShuffleObservableTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.PlaybackSongObservableTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.PlaybackStateObservableTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.SwapSongPlaybackQueueObservableTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.PlaylistTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.PlaylistPersistenceTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.SongPersistenceTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.SongStatisticPersistenceTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.PlaylistAdapterTests;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.TracklistAdapterTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SongTests.class,
        PlaylistTests.class,
        PlaybackControllerTests.class,
        PlaybackQueueTests.class,
        PlaylistAdapterTests.class,
        PlaylistControllerTests.class,
        SongControllerTests.class,
        SongStatisticControllerTests.class,
        TracklistAdapterTests.class,
        SongValidatorTests.class,
        PlaylistValidatorTests.class,
        CalculateTests.class,
        SongPersistenceTests.class,
        PlaylistPersistenceTests.class,
        SongStatisticPersistenceTests.class,
        PlaybackStateObservableTests.class,
        PlaybackModeObservableTests.class,
        PlaybackShuffleObservableTests.class,
        PlaybackSongObservableTests.class,
        AddSongPlaybackQueueObservableTests.class,
        DeleteSongPlaybackQueueObservableTests.class,
        SwapSongPlaybackQueueObservableTests.class,
        FavoriteSongObservableTests.class
})

public class AllUnitTests {

}
