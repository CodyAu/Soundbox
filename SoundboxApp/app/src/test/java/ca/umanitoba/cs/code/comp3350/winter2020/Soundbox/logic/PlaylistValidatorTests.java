package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.PlaylistValidator;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.basic.PlaylistValidatorBasic;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;

public class PlaylistValidatorTests {

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for PlaylistValidator.");
    }

    @Test
    public void testPlaylistValidator() {
        PlaylistValidator validator = new PlaylistValidatorBasic();

        Playlist playlist1 = new Playlist(1, "playme", 0);
        Playlist playlist2 = new Playlist(-1, "playme", 0);

        assert (validator.isPlaylistValid(playlist1));
        assert (!validator.isPlaylistValid(playlist2));
    }

    @After
    public void tearDown() {
        System.out.println("Finished unit tests for PlaylistValidator.");
    }

}
