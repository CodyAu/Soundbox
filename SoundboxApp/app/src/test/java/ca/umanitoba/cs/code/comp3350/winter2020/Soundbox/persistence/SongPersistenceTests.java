package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.stubs.SongPersistenceStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SongPersistenceTests {
    List<Song> songs;

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for SongPersistence.");

        songs = new ArrayList<Song>();

        songs.add(new Song.Builder().setSongId(1).setSongName("Test1").setFilepath("/Test1").build());
        songs.add(new Song.Builder().setSongId(2).setSongName("Test2").setFilepath("/Test2").build());
        songs.add(new Song.Builder().setSongId(3).setSongName("Test3").setFilepath("/Test3").build());
        songs.add(new Song.Builder().setSongId(3).setSongName("Test3").setFilepath("/Test3").build()); //same ids as 3rd one, should fail insert
    }

    @Test
    public void testInsert() {
        SongPersistence persistence = new SongPersistenceStub();
        assertNotNull(persistence);

        assert (persistence.insertSong(songs.get(0)));
        assert (persistence.insertSong(songs.get(1)));
        assert (persistence.insertSong(songs.get(2)));
        assert (!persistence.insertSong(songs.get(3)));

        assertEquals(4, persistence.getNextId());
        assertEquals(3, persistence.getAllSongs().size());
    }

    @Test
    public void testGetAll() {
        SongPersistence persistence = new SongPersistenceStub();
        assertNotNull(persistence);

        assert (persistence.insertSong(songs.get(0)));
        assert (persistence.insertSong(songs.get(1)));
        assert (persistence.insertSong(songs.get(2)));

        List<Song> songs = persistence.getAllSongs();
        assertEquals(3, songs.size());

        assertEquals(1, songs.get(0).getSongId());
        assertEquals(2, songs.get(1).getSongId());
        assertEquals(3, songs.get(2).getSongId());
    }

    @Test
    public void testDelete() {
        SongPersistence persistence = new SongPersistenceStub();
        assertNotNull(persistence);

        assert (persistence.insertSong(songs.get(0)));
        assert (persistence.insertSong(songs.get(1)));
        assert (persistence.insertSong(songs.get(2)));

        assert (persistence.deleteSong(songs.get(2)));
        assert (!persistence.deleteSong(songs.get(3)));

        assertEquals(2, persistence.getAllSongs().size());
    }

    @Test
    public void testUpdate() {
        SongPersistence persistence = new SongPersistenceStub();
        assertNotNull(persistence);

        assert (persistence.insertSong(songs.get(0)));
        assert (persistence.insertSong(songs.get(1)));
        assert (persistence.insertSong(songs.get(2)));

        songs.get(0).setSongName("Different name");
        assert(persistence.updateSong(songs.get(0)));

        assertEquals(songs.get(0).getSongName(), persistence.getAllSongs().get(0).getSongName());

        songs.get(0).setSongName("Test1");
    }


    @After
    public void tearDown() {
        System.out.println("Finished unit tests for SongPersistence.");
    }

}
