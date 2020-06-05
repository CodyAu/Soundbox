package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.acceptance;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.SongStatisticController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils.SongStatisticFilters;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongStatistic;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.Matchers.firstDisplayed;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.Utils.sleepFor;
import static org.hamcrest.core.AllOf.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TrackSongStatisticsTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(MainActivity.class, true);

    @Before
    public void setUp() {
        System.out.println("Starting system tests for TrackSongStatisticsTest.");
    }

    @Test
    public void testSongStatistics1ListenTime() {
        int listenTimeBefore = SongStatisticFilters.sumStatisticsValues((new SongStatisticController()).getStatisticsByType(SongStatistic.Statistic.LISTEN_TIME));

        // Play
        onView(firstDisplayed(allOf(withId(R.id.play_bg), isDisplayed()))).perform(click());

        // Let the song play for a few seconds
        sleepFor(3000);

        // Pause
        onView(firstDisplayed(allOf(withId(R.id.play_bg), isDisplayed()))).perform(click());

        int listenTimeAfter = SongStatisticFilters.sumStatisticsValues((new SongStatisticController()).getStatisticsByType(SongStatistic.Statistic.LISTEN_TIME));

        assert(listenTimeAfter - listenTimeBefore >= 2500);
    }

    @After
    public void tearDown() {
        System.out.println("Finished system tests for TrackSongStatisticsTest.");
    }

}
