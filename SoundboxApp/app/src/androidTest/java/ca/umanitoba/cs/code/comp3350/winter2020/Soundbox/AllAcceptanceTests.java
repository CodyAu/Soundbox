package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.acceptance.CustomPlaylistTest;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.acceptance.PlaybackControlsTest;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.acceptance.PlaybackOptionsTest;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.acceptance.SongNavigationTest;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.acceptance.SongOrganizationTest;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.acceptance.SongRatingsTest;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.acceptance.TrackSongStatisticsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CustomPlaylistTest.class,
        PlaybackControlsTest.class,
        SongNavigationTest.class,
        PlaybackOptionsTest.class,
        SongOrganizationTest.class,
        SongRatingsTest.class,
        TrackSongStatisticsTest.class
})

public class AllAcceptanceTests {

}
