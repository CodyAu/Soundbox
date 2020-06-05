package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.acceptance;

import android.view.KeyEvent;
import android.widget.EditText;

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
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.Matchers.atPosition;
import static org.hamcrest.core.AllOf.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SongOrganizationTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(MainActivity.class, true);

    @Before
    public void setUp() {
        System.out.println("Starting system tests for SongOrganizationTest.");
    }

    @Test
    public void testOrganization1SearchSongs() {
        String searchQuery = "Illuminati";

        // Click to Library tab
        onView(withId(R.id.navigation_library))
                .perform(click());

        for (int i = 0; i < 5; i++) {
            onView(withId(R.id.library_tab_view_pager))
                    .perform(swipeLeft());
        }
        onView(allOf(withText("SONGS"), isCompletelyDisplayed()))
                .perform(click()); // Click on SONGS tab
        onView(withId(R.id.action_search_library))
                .perform(click()); // Start search

        // Softkeyboard should be open after click is performed
        onView(isAssignableFrom(EditText.class))
                .perform(typeText(searchQuery), pressKey(KeyEvent.KEYCODE_ENTER));

        // Verify searchQuery is the top result
        onView(withId(R.id.tracklist_recycle_view))
                .check(matches(atPosition(0, hasDescendant(withText(searchQuery)))));
    }

    @After
    public void tearDown() {
        System.out.println("Finished system tests for SongOrganizationTest.");
    }

}
