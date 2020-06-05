package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.Services;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SongControllerIT {
    private File tempDB;
    private SongController controller;
    List<Song> songs;

    @Before
    public void setUp() throws IOException {
        System.out.println("Starting integration tests for SongController.");
        tempDB = TestUtils.copyDB();
        controller = new SongController();
        assertNotNull(controller);

        songs = new ArrayList<Song>();

        songs.add(new Song.Builder().setSongId(1).setSongName("Test1").setFilepath("/Test1").build());
        songs.add(new Song.Builder().setSongId(2).setSongName("Test2").setFilepath("/Test2").build());
        songs.add(new Song.Builder().setSongId(3).setSongName("Test3").setFilepath("/Test3").build());
        songs.add(new Song.Builder().setSongId(3).setSongName("Test3").setFilepath("/Test3").build()); //same ids as 3rd one, should fail insert
    }

    @Test
    public void testInsert() {
        assert (controller.insertSong(songs.get(0)));
        assert (controller.insertSong(songs.get(1)));
        assert (controller.insertSong(songs.get(2)));
        assert (!controller.insertSong(songs.get(3)));

        assertEquals(4, controller.getNextId());
        assertEquals(3, controller.getAllSongs().size());

        assert (controller.deleteSong(songs.get(0)));
        assert (controller.deleteSong(songs.get(1)));
        assert (controller.deleteSong(songs.get(2)));
    }

    @Test
    public void testInsertMultiple() {
        controller.insertSongs(Arrays.asList(
                songs.get(0),
                songs.get(1),
                songs.get(2),
                songs.get(0)) // Duplicate test
        );

        assertEquals(4, controller.getNextId());
        assertEquals(3, controller.getAllSongs().size());

        assert (controller.deleteSong(songs.get(0)));
        assert (controller.deleteSong(songs.get(1)));
        assert (controller.deleteSong(songs.get(2)));
    }

    @Test
    public void testGetAll() {
        assert (controller.insertSong(songs.get(0)));
        assert (controller.insertSong(songs.get(1)));
        assert (controller.insertSong(songs.get(2)));
        assert (!controller.insertSong(songs.get(3)));

        List<Song> receivedSongs = controller.getAllSongs();
        assertEquals(3, receivedSongs.size());

        assertEquals(1, receivedSongs.get(0).getSongId());
        assertEquals(2, receivedSongs.get(1).getSongId());
        assertEquals(3, receivedSongs.get(2).getSongId());

        assert (controller.deleteSong(songs.get(0)));
        assert (controller.deleteSong(songs.get(1)));
        assert (controller.deleteSong(songs.get(2)));
    }

    @Test
    public void testDelete() {
        assert (controller.insertSong(songs.get(0)));
        assert (controller.insertSong(songs.get(1)));
        assert (controller.insertSong(songs.get(2)));

        assert (controller.deleteSong(songs.get(2)));
        assert (!controller.deleteSong(songs.get(3)));

        assertEquals(2, controller.getAllSongs().size());

        assert (controller.deleteSong(songs.get(0)));
        assert (controller.deleteSong(songs.get(1)));
    }

    @Test
    public void testUpdate() {
        assert (controller.insertSong(songs.get(0)));
        assert (controller.insertSong(songs.get(1)));
        assert (controller.insertSong(songs.get(2)));

        songs.get(0).setSongName("Different name");
        assert(controller.updateSong(songs.get(0)));

        assertEquals(songs.get(0).getSongName(), controller.getAllSongs().get(0).getSongName());

        songs.get(0).setSongName("Test1");

        assert (controller.deleteSong(songs.get(0)));
        assert (controller.deleteSong(songs.get(1)));
        assert (controller.deleteSong(songs.get(2)));
    }

    @After
    public void tearDown() {
        tempDB.delete();
        Services.clean();
        System.out.println("Finished integration tests for SongController.");
    }

}
