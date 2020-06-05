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
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.Matchers.firstDisplayed;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.ViewActions.clickChildWithId;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SongRatingsTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(MainActivity.class, true);

    @Before
    public void setUp() {
        System.out.println("Starting system tests for SongRatingsTest.");
    }

    @Test
    public void testRating1Favorite() {
        String searchQuery = "Illuminati";

        onView(withId(R.id.navigation_library)).perform(click());

        for (int i = 0; i < 5; i++) {
            onView(withId(R.id.library_tab_view_pager))
                    .perform(swipeLeft());
        }
        onView(allOf(withText("SONGS"), isCompletelyDisplayed()))
                .perform(click()); // Click on SONGS tab

        // Favorite the song
        onView(withId(R.id.tracklist_recycle_view))
                .check(matches(hasDescendant(withText(searchQuery))))
                .perform(actionOnItem(hasDescendant(withText(searchQuery)), clickChildWithId(R.id.tracklist_row_favorite)));

        // Swipe to favorites
        onView(withId(R.id.library_tab_view_pager))
                .perform(swipeRight());

        // Click on Likes collection
        onView(firstDisplayed(withId(R.id.recycler_view)))
                .check(matches(hasDescendant(withText(R.string.likes))))
                .perform(actionOnItem(hasDescendant(withText(R.string.likes)), clickChildWithId(R.id.thumbnail)));
        onView(withId(R.id.tracklist_recycle_view))
                .check(matches(hasDescendant(withText(searchQuery))))
                .perform(actionOnItem(hasDescendant(withText(searchQuery)), clickChildWithId(R.id.tracklist_row_favorite)));

        pressBack();

        // Check to make sure the song is now gone
        onView(firstDisplayed(withId(R.id.recycler_view)))
                .check(matches(hasDescendant(withText(R.string.likes))))
                .perform(actionOnItem(hasDescendant(withText(R.string.likes)), clickChildWithId(R.id.thumbnail)));
        onView(withId(R.id.tracklist_recycle_view))
                .check(matches(not(hasDescendant(withText(searchQuery)))));
    }

    @After
    public void tearDown() {
        System.out.println("Finished system tests for SongRatingsTest.");
    }

}
