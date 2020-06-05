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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.Matchers.firstDisplayed;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.Matchers.withProgress;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.Utils.sleepFor;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.ViewActions.clickChildWithId;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.ViewActions.scrubProgress;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PlaybackOptionsTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(MainActivity.class, true);

    @Before
    public void setUp() {
        System.out.println("Starting system tests for PlaybackOptionsTest.");
    }

    @Test
    public void testPlayback1Shuffle() {
        onView(withId(R.id.controls_placeholder)).perform(clickChildWithId(R.id.playing_bottom_sheet));
        sleepFor(3000);

        onView(firstDisplayed(allOf(withId(R.id.shuffle), isDisplayed()))).perform(click());

        onView(withText(R.string.shuffle_success)).inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void testPlayback2Scrub() {
        onView(withId(R.id.controls_placeholder)).perform(clickChildWithId(R.id.playing_bottom_sheet));
        sleepFor(3000);

        // Play
        onView(firstDisplayed(allOf(withId(R.id.play_bg), isDisplayed()))).perform(click());

        // Set progress so the song finishes faster (don't want to wait for a song to finish to test this)
        onView(withId(R.id.seekBar)).perform(scrubProgress(Math.max(PlaybackController.getDuration() - 5000, 0)));

        onView(withId(R.id.seekBar)).check(matches(withProgress(Math.max(PlaybackController.getDuration() - 5000, 0), 250)));
    }

    @Test
    public void testPlayback3Loop() {
        String songName = PlaybackController.getSong().getSongName();

        onView(withId(R.id.controls_placeholder)).perform(clickChildWithId(R.id.playing_bottom_sheet));
        sleepFor(3000);

        // Play
        onView(firstDisplayed(allOf(withId(R.id.play_bg), isDisplayed()))).perform(click());

        // Set progress so the song finishes faster (don't want to wait for a song to finish to test this)
        onView(withId(R.id.seekBar)).perform(scrubProgress(Math.max(PlaybackController.getDuration() - 500, 0)));

        // Let the song play for a few seconds to make sure its done
        sleepFor(3000);
        // Should have a different name since we didnt press loop button
        onView(withId(R.id.view_pager)).check(matches(not(hasDescendant(withText(songName)))));

        songName = PlaybackController.getSong().getSongName();

        // Press loop button
        onView(firstDisplayed(allOf(withId(R.id.loop), isDisplayed()))).perform(click());
        onView(withId(R.id.seekBar)).perform(scrubProgress(Math.max(PlaybackController.getDuration() - 500, 0)));

        sleepFor(3000);
        // Should have the same name now since it looped
        onView(withId(R.id.view_pager)).check(matches(hasDescendant(withText(songName))));
    }

    @After
    public void tearDown() {
        System.out.println("Finished system tests for PlaybackOptionsTest.");
    }

}
