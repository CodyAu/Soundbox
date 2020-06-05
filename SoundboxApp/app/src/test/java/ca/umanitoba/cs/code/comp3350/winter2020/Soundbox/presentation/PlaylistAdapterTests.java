package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.adapters.PlaylistAdapter;

import static org.junit.Assert.assertNotNull;

public class PlaylistAdapterTests {

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for PlaylistAdapter.");
    }

    @Test
    public void testPlaylistCreate() throws IOException {
        MainActivity context = new MainActivity();
        List<Playlist> playList = new ArrayList<>();
        PlaylistAdapter adapter = new PlaylistAdapter(context, playList);

        assertNotNull(adapter.getPlaylists());
        assertNotNull(adapter.getPlaylists().size() == 10);

        for (Playlist play : adapter.getPlaylists()) {
            assertNotNull(play);
            assertNotNull(play.getName());
            assertNotNull(play.getPlaylistId());
            assertNotNull(play.getSongs());
            assertNotNull(play.getThumbnail());
        }
    }

    @After
    public void tearDown() {
        System.out.println("Finished unit tests for PlaylistAdapter.");
    }

}
