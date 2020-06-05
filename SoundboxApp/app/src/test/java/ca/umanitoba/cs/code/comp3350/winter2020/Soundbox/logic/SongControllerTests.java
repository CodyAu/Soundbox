package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.stubs.SongPersistenceStub;

import static org.junit.Assert.assertEquals;

public class SongControllerTests {

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for SongController.");
    }

    @Test
    public void testSongController() {
        long songId = 1;
        Song song = new Song.Builder()
                .setSongId(1)
                .setSongName("Test")
                .setArtist("none")
                .setLength(4)
                .setFilepath("/music.mp3")
                .build();

        SongController controller = new SongController(new SongPersistenceStub());
        controller.insertSong(song);
        assertEquals(song, controller.getSongById(songId));
        assertEquals(2, controller.getNextId());
        assert (!controller.getAllSongs().isEmpty());

        song.setSongName("Changed name");
        controller.updateSong(song);
        assertEquals(song.getSongName(), controller.getSongById(song.getSongId()).getSongName());

        boolean deleted = controller.deleteSong(song);
        assert (deleted);
        assertEquals(controller.getSongById(song.getSongId()), null);
    }

    @Test
    public void testSongControllerFetches() {
        SongController controller = new SongController(new SongPersistenceStub());

        Song song1 = new Song(1, "Test", "none", null, null, "EDM", 4, 999, "/Music/Test.mp3", "audio/mp3", 256000, 1024, false);
        Song song2 = new Song(2, "Test", "none", null, null, "RAP", 4, 999, "/Music/Test.ogg", "audio/mp3", 256000, 1024, false);
        Song song3 = new Song(3, "Test", "Fails", null, null, "HIP HOP", 4, 999, "/Music/Test.wav", "audio/mp3", 256000, 1024, false);
        Song song4 = new Song(4, "Hot", "Fails", null, null, "POP", 4, 999, "/Music/Hot.mp3", "audio/mp3", 256000, 1024, false);
        Song song5 = new Song(5, "Pot", "Muffins", null, null, "FUNK", 4, 999, "/Music/Pot.mp3", "audio/mp3", 256000, 1024, false);

        controller.insertSong(song1);
        controller.insertSong(song2);
        controller.insertSong(song3);
        controller.insertSong(song4);
        controller.insertSong(song5);

        List<Song> songs = controller.getSongsByName("Test");
        assert (songs.contains(song1));
        assert (songs.contains(song2));
        assert (songs.contains(song3));
        assert (!songs.contains(song4));
        assert (!songs.contains(song5));

        songs = controller.getSongsByArtist("Fails");
        assert (!songs.contains(song1));
        assert (!songs.contains(song2));
        assert (songs.contains(song3));
        assert (songs.contains(song4));
        assert (!songs.contains(song5));

        songs = controller.getSongsByGenre("HIP HOP");
        assert (!songs.contains(song1));
        assert (!songs.contains(song2));
        assert (songs.contains(song3));
        assert (!songs.contains(song4));
        assert (!songs.contains(song5));

        songs = controller.getSongsByAlbum("<unknown>");
        assert (songs.containsAll(controller.getAllSongs()));

        List<Song> sortedSongs = controller.getSongsByNameLike("Tests");
        assert (sortedSongs.indexOf(song1) <= 2); // levenshtein 1
        assert (sortedSongs.indexOf(song2) <= 2); // levenshtein 1
        assert (sortedSongs.indexOf(song3) <= 2); // levenshtein 1
        assert (sortedSongs.indexOf(song4) > 2); // levenshtein 4
        assert (sortedSongs.indexOf(song5) > 2); // levenshtein 4

        sortedSongs = controller.getSongsByArtistLike("Fail");
        assert (sortedSongs.indexOf(song1) > 2); // levenshtein 4
        assert (sortedSongs.indexOf(song2) > 2); // levenshtein 4
        assert (sortedSongs.indexOf(song3) < 2); // substring levenshtein 0
        assert (sortedSongs.indexOf(song4) < 2); // substring levenshtein 0
        assertEquals (2, sortedSongs.indexOf(song5)); // levenshtein 3

        sortedSongs = controller.getSongsByAlbumLike("unknowns");
        assert (sortedSongs.indexOf(song1) <= 4); // levenshtein 1
        assert (sortedSongs.indexOf(song2) <= 4); // levenshtein 1
        assert (sortedSongs.indexOf(song3) <= 4); // levenshtein 1
        assert (sortedSongs.indexOf(song4) <= 4); // levenshtein 1
        assert (sortedSongs.indexOf(song5) <= 4); // levenshtein 1

        sortedSongs = controller.getSongsByGenreLike("HOP");
        assert (sortedSongs.indexOf(song1) == 3); // levenshtein 3
        assert (sortedSongs.indexOf(song2) == 2); // levenshtein 2
        assert (sortedSongs.indexOf(song3) == 0); // levenshtein 0
        assert (sortedSongs.indexOf(song4) == 1); // levenshtein 1
        assert (sortedSongs.indexOf(song5) == 4); // levenshtein 4

        sortedSongs = controller.getSongsLike("HOP");
        assert (sortedSongs.indexOf(song1) >= 2 && sortedSongs.indexOf(song1) < 5); // levenshtein 2
        assert (sortedSongs.indexOf(song2) >= 2 && sortedSongs.indexOf(song2) < 5); // levenshtein 2
        assert (sortedSongs.indexOf(song3) == 0); // levenshtein 0
        assert (sortedSongs.indexOf(song4) == 1); // levenshtein 1
        assert (sortedSongs.indexOf(song5) >= 2 && sortedSongs.indexOf(song5) < 5); // levenshtein 2
    }

    @After
    public void tearDown() {
        System.out.println("Finished unit tests for SongController.");
    }

}
