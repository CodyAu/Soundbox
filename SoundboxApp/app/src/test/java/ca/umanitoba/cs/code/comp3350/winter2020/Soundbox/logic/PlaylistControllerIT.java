package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.Services;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PlaylistControllerIT {
    private File tempDB;
    private PlaylistController controller;
    private List<Playlist> playlists;

    @Before
    public void setUp() throws IOException {
        System.out.println("Starting integration tests for PlaylistController.");
        tempDB = TestUtils.copyDB();
        controller = new PlaylistController();
        assertNotNull(controller);

        playlists = new ArrayList<Playlist>();

        playlists.add(new Playlist(11, "Playlist1", 0));
        playlists.add(new Playlist(12, "Playlist2", 0));
        playlists.add(new Playlist(13, "Playlist3", 0));
        playlists.add(new Playlist(13, "Playlist4", 0)); //same ids as 3rd
    }

    @Test
    public void testInsert() {
        assert (controller.insertPlaylist(playlists.get(0)));
        assert (controller.insertPlaylist(playlists.get(1)));
        assert (controller.insertPlaylist(playlists.get(2)));
        assert (!controller.insertPlaylist(playlists.get(3)));

        assertEquals(4, controller.getNextId());
        assertEquals(3, controller.getAllPlaylists().size());

        assert (controller.deletePlaylist(playlists.get(0)));
        assert (controller.deletePlaylist(playlists.get(1)));
        assert (controller.deletePlaylist(playlists.get(2)));
    }

    @Test
    public void testGetAll() {
        assert (controller.insertPlaylist(playlists.get(0)));
        assert (controller.insertPlaylist(playlists.get(1)));
        assert (controller.insertPlaylist(playlists.get(2)));
        assert (!controller.insertPlaylist(playlists.get(3)));

        List<Playlist> receivedPlaylists = controller.getAllPlaylists();
        assertEquals(3, receivedPlaylists.size());

        assertEquals(11, receivedPlaylists.get(0).getPlaylistId());
        assertEquals(12, receivedPlaylists.get(1).getPlaylistId());
        assertEquals(13, receivedPlaylists.get(2).getPlaylistId());

        assert (controller.deletePlaylist(playlists.get(0)));
        assert (controller.deletePlaylist(playlists.get(1)));
        assert (controller.deletePlaylist(playlists.get(2)));
    }

    @Test
    public void testDelete() {
        assert (controller.insertPlaylist(playlists.get(0)));
        assert (controller.insertPlaylist(playlists.get(1)));
        assert (controller.insertPlaylist(playlists.get(2)));

        assert (controller.deletePlaylist(playlists.get(2)));
        assert (!controller.deletePlaylist(playlists.get(3)));

        assertEquals(2, controller.getAllPlaylists().size());

        assert (controller.deletePlaylist(playlists.get(0)));
        assert (controller.deletePlaylist(playlists.get(1)));
    }

    @Test
    public void testUpdate() {
        assert (controller.insertPlaylist(playlists.get(0)));
        assert (controller.insertPlaylist(playlists.get(1)));
        assert (controller.insertPlaylist(playlists.get(2)));

        playlists.get(0).setName("Different name");
        controller.updatePlaylist(playlists.get(0));

        assertEquals(playlists.get(0).getName(), controller.getAllPlaylists().get(0).getName());

        playlists.get(0).setName("Playlist1");

        assert (controller.deletePlaylist(playlists.get(0)));
        assert (controller.deletePlaylist(playlists.get(1)));
        assert (controller.deletePlaylist(playlists.get(2)));
    }


    @After
    public void tearDown() {
        tempDB.delete();
        Services.clean();
        System.out.println("Finished integration tests for PlaylistController.");
    }

}
