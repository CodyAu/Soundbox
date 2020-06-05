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
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.Matchers.firstDisplayed;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.Matchers.withBackgroundDrawable;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.Matchers.withProgress;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.Utils.sleepFor;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.ViewActions.clickChildWithId;
import static org.hamcrest.core.AllOf.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PlaybackControlsTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(MainActivity.class, true);

    @Before
    public void setUp() {
        System.out.println("Starting system tests for PlaybackControlsTest.");
    }

    @Test
    public void testPlayback1PlayPause() {
        onView(withId(R.id.controls_placeholder)).perform(clickChildWithId(R.id.playing_bottom_sheet));
        sleepFor(3000);

        onView(withId(R.id.seekBar)).check(matches(withProgress(0)));

        onView(firstDisplayed(allOf(withId(R.id.play_bg), isDisplayed()))).check(matches(withBackgroundDrawable(R.drawable.pause_background)));
        // Play
        onView(firstDisplayed(allOf(withId(R.id.play_bg), isDisplayed()))).perform(click());

        // Let the song play for a few seconds
        sleepFor(3000);
        onView(firstDisplayed(allOf(withId(R.id.play_bg), isDisplayed()))).check(matches(withBackgroundDrawable(R.drawable.play_background)));

        // Pause
        onView(firstDisplayed(allOf(withId(R.id.play_bg), isDisplayed()))).perform(click());
        onView(withId(R.id.seekBar)).check(matches(withProgress(3000, 500)));

        sleepFor(3000);

        // Pause was successful and it doesn't continue to play
        onView(withId(R.id.seekBar)).check(matches(withProgress(3000, 500)));
    }

    @After
    public void tearDown() {
        System.out.println("Finished system tests for PlaybackControlsTest.");
    }

}
