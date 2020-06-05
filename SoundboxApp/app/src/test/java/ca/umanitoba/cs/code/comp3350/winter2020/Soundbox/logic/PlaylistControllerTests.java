package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.stubs.PlaylistPersistenceStub;

import static org.junit.Assert.assertEquals;

public class PlaylistControllerTests {

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for PlaylistController.");
    }

    @Test
    public void testPlaylistController() {
        PlaylistController controller = new PlaylistController(new PlaylistPersistenceStub());
        Playlist playlist = new Playlist(11, "fake", -1);

        controller.insertPlaylist(playlist);
        assertEquals(11, controller.getPlaylistById(11).getPlaylistId());
        assertEquals(12, controller.getNextId());
        assert (!controller.getAllPlaylists().isEmpty());

        playlist.setName("not not fake");
        controller.updatePlaylist(playlist);
        assertEquals(controller.getPlaylistById(playlist.getPlaylistId()).getName(), playlist.getName());

        boolean deleted = controller.deletePlaylist(playlist);
        assert (deleted);
        assertEquals(controller.getPlaylistById(playlist.getPlaylistId()), null);
    }

    @Test
    public void testPlaylistControllerFetches() {
        PlaylistController controller = new PlaylistController(new PlaylistPersistenceStub());

        Playlist playlist1 = new Playlist(11, "PlaylistControllerTests - Test", -1);
        Playlist playlist2 = new Playlist(12, "PlaylistControllerTests - Test", -1);
        Playlist playlist3 = new Playlist(13, "PlaylistControllerTests - Test", -1);
        Playlist playlist4 = new Playlist(14, "PlaylistControllerTests - Hot", -1);
        Playlist playlist5 = new Playlist(15, "PlaylistControllerTests - Pot", -1);

        controller.insertPlaylist(playlist1);
        controller.insertPlaylist(playlist2);
        controller.insertPlaylist(playlist3);
        controller.insertPlaylist(playlist4);
        controller.insertPlaylist(playlist5);

        List<Playlist> playlists = controller.getPlaylistsByName("PlaylistControllerTests - Test");
        assert (playlists.contains(playlist1));
        assert (playlists.contains(playlist2));
        assert (playlists.contains(playlist3));
        assert (!playlists.contains(playlist4));
        assert (!playlists.contains(playlist5));

        List<Playlist> sortedPlaylists = controller.getPlaylistsByNameLike("PlaylistControllerTests - Tests");
        assert (sortedPlaylists.indexOf(playlist1) <= 2); // levenshtein 1
        assert (sortedPlaylists.indexOf(playlist2) <= 2); // levenshtein 1
        assert (sortedPlaylists.indexOf(playlist3) <= 2); // levenshtein 1
        assert (sortedPlaylists.indexOf(playlist4) > 2); // levenshtein 4
        assert (sortedPlaylists.indexOf(playlist5) > 2); // levenshtein 4
    }

    @After
    public void tearDown() {
        System.out.println("Finished unit tests for PlaylistController.");
    }

}
