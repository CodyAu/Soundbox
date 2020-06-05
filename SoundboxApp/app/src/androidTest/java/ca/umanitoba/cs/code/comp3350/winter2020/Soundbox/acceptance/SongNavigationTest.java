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
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.Matchers.firstDisplayed;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.Utils.sleepFor;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.ViewActions.clickChildWithId;
import static org.hamcrest.core.AllOf.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SongNavigationTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(MainActivity.class, true);

    @Before
    public void setUp() {
        System.out.println("Starting system tests for SongNavigationTest.");
    }

    @Test
    public void testSongNavigation1Button() {
        onView(withId(R.id.controls_placeholder)).perform(clickChildWithId(R.id.playing_bottom_sheet));
        sleepFor(3000);

        onView(withId(R.id.view_pager)).check(matches(hasDescendant(withText(PlaybackController.getSong().getSongName()))));
        onView(firstDisplayed(allOf(withId(R.id.play_next), isDisplayed()))).perform(click());

        sleepFor(500);

        onView(withId(R.id.view_pager)).check(matches(hasDescendant(withText(PlaybackController.getSong().getSongName()))));
        onView(firstDisplayed(allOf(withId(R.id.play_prev), isDisplayed()))).perform(click());

        sleepFor(500);

        onView(withId(R.id.view_pager)).check(matches(hasDescendant(withText(PlaybackController.getSong().getSongName()))));
    }

    @Test
    public void testSongNavigation2Swipe() {
        onView(withId(R.id.controls_placeholder)).perform(clickChildWithId(R.id.playing_bottom_sheet));
        sleepFor(3000);

        onView(withId(R.id.view_pager)).check(matches(hasDescendant(withText(PlaybackController.getSong().getSongName()))));
        onView(withId(R.id.view_pager)).perform(swipeLeft());

        sleepFor(500);

        onView(withId(R.id.view_pager)).check(matches(hasDescendant(withText(PlaybackController.getSong().getSongName()))));
        onView(withId(R.id.view_pager)).perform(swipeRight());

        sleepFor(500);

        onView(withId(R.id.view_pager)).check(matches(hasDescendant(withText(PlaybackController.getSong().getSongName()))));
    }

    @After
    public void tearDown() {
        System.out.println("Finished system tests for SongNavigationTest.");
    }

}
