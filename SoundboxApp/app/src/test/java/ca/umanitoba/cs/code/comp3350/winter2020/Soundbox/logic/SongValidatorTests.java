package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.SongValidator;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.basic.SongValidatorBasic;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;

public class SongValidatorTests {

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for SongValidator.");
    }

    @Test
    public void testSongValidator() {
        SongValidator validator = new SongValidatorBasic();

        Song song1 = new Song(1, "noonoo", null, null, null, null, 3, 999, "song.mp3", "audio/mp3", 255000, 1024, false);
        Song song2 = new Song.Builder().build();

        assert (validator.isSongValid(song1));
        assert (!validator.isSongValid(song2));

        song1.setSongId(-1);

        assert (!validator.isSongValid(song1));
    }

    @After
    public void tearDown() {
        System.out.println("Finished unit tests for SongValidator.");
    }

}
