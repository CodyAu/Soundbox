package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongCollection;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.adapters.TracklistAdapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class TracklistAdapterTests {

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for SongAdapter.");
    }

    @Test
    public void testTracklistCreate() throws IOException {
        ArrayList<Song> songList = new ArrayList<>();
        Context context = mock(Context.class);
        TracklistAdapter adapter = new TracklistAdapter(context, new SongCollection(songList));

        assertNotNull(adapter.getSongs());
        assertEquals(0, adapter.getSongs().songsSize());

        for (Song play : songList) {
            assertNotNull(play);
            assertNotNull(play.getArtist());
            assertNotNull(play.getSongId());
            assertNotNull(play.getSongName());
            assertNotNull(play.getLength());
        }
    }

    @After
    public void tearDown() {
        System.out.println("Finished unit tests for SongAdapter.");
    }

}
