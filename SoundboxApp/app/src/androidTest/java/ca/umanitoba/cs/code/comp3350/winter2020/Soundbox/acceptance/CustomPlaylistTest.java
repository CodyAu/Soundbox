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

import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaylistController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.Matchers.atPosition;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.Utils.waitForWithId;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.Utils.waitForWithText;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.ViewActions.clickChildWithId;
import static ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.ViewAssertions.recyclerViewItemCount;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;

@LargeTest
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomPlaylistTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(MainActivity.class, true);

    // Playlist can be sorted by name so add spaces so it will be ahead of all other even if sorted by name (For testing only so we don't have to change sort order)
    private String playlistName = "           A Test";

    @Before
    public void setUp() {
        System.out.println("Starting system tests for CustomPlaylistTest.");
    }

    @Test
    public void testPlaylist1Add() {
        onView(withId(R.id.navigation_playlists))
                .perform(click()); // Click to Playlist tab

        onView(withId(R.id.buttonAddPlaylist))
                .perform(click()); // Click add new playlist

        // Dialog takes a second to popup
        waitForWithText(R.string.playlist_add_title, 10000)
                .check(matches(isCompletelyDisplayed())); // Click add new playlist

        onView(withId(R.id.edit_comment))
                .perform(typeText(playlistName), closeSoftKeyboard());
        onView(withId(R.id.buttonSubmit))
                .perform(click());

        // Playlist should be added
        onView(withId(R.id.playlist_recycler_view))
                .check(matches(atPosition(0, hasDescendant(withText(playlistName)))));

        List<Playlist> playlists = (new PlaylistController()).getPlaylistsByName(playlistName);
        assert(playlists.size() > 0);
        assertEquals(playlistName, playlists.get(0).getName());
    }

    @Test
    public void testPlaylist2AddSongs() {
        onView(withId(R.id.navigation_playlists)).perform(click()); // Click to Playlist tab

        onView(withId(R.id.playlist_recycler_view))
                .check(matches(atPosition(0, hasDescendant(withText(playlistName)))))
                .perform(actionOnItemAtPosition(0, click()));
        waitForWithId(R.id.tracklist_recycle_view, 10000).check(recyclerViewItemCount(0));

        onView(withId(R.id.menu_tracklist)).perform(click());

        waitForWithId(R.id.tracklist_recycle_view, 10000);
        onView(withId(R.id.tracklist_recycle_view))
                .perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.tracklist_recycle_view))
                .perform(actionOnItemAtPosition(1, click()));
        onView(withId(R.id.tracklist_recycle_view))
                .perform(actionOnItemAtPosition(2, click()));
        onView(withId(R.id.tracklist_recycle_view))
                .perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.action_add)).perform(click());
        pressBack();

        waitForWithId(R.id.tracklist_recycle_view, 10000)
                .check(recyclerViewItemCount(2));

        pressBack();
        waitForWithId(R.id.playlist_recycler_view, 10000)
                .check(matches(atPosition(0, allOf(hasDescendant(withText(playlistName)), hasDescendant(withText("2 songs"))))));
    }

    @Test
    public void testPlaylist3Delete() {
        onView(withId(R.id.navigation_playlists))
                .perform(click()); // Click to Playlist tab

        onView(withId(R.id.playlist_recycler_view))
                .check(matches(atPosition(0, hasDescendant(withText(playlistName)))))
                .perform(actionOnItemAtPosition(0, clickChildWithId(R.id.overflow)));
        waitForWithText(R.string.action_remove, 10000)
                .inRoot(isPlatformPopup())
                .perform(click());

        // Playlist should be deleted
        List<Playlist> playlists = (new PlaylistController()).getPlaylistsByName(playlistName);
        assert(playlists.size() == 0);
    }

    @After
    public void tearDown() {
        System.out.println("Finished system tests for CustomPlaylistTest.");
    }

}
