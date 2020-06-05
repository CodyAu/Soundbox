package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.stubs.PlaylistPersistenceStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PlaylistPersistenceTests {
    private List<Playlist> playlists;

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for PlaylistPersistence.");

        playlists = new ArrayList<Playlist>();

        playlists.add(new Playlist(11, "Playlist1", 0));
        playlists.add(new Playlist(12, "Playlist2", 0));
        playlists.add(new Playlist(13, "Playlist3", 0));
        playlists.add(new Playlist(13, "Playlist4", 0)); //same ids as 3rd
    }

    @Test
    public void testInsert() {
        PlaylistPersistence persistence = new PlaylistPersistenceStub();
        assertNotNull(persistence);

        assert (persistence.insertPlaylist(playlists.get(0)));
        assert (persistence.insertPlaylist(playlists.get(1)));
        assert (persistence.insertPlaylist(playlists.get(2)));
        assert (!persistence.insertPlaylist(playlists.get(3)));

        assertEquals(14, persistence.getNextId());
        assertEquals(13, persistence.getAllPlaylists().size());
    }

    @Test
    public void testGetAll() {
        PlaylistPersistence persistence = new PlaylistPersistenceStub();
        assertNotNull(persistence);

        assert (persistence.insertPlaylist(playlists.get(0)));
        assert (persistence.insertPlaylist(playlists.get(1)));
        assert (persistence.insertPlaylist(playlists.get(2)));
        assert (!persistence.insertPlaylist(playlists.get(3)));

        List<Playlist> playlists = persistence.getAllPlaylists();
        assertEquals(13, playlists.size());

        assertEquals(11, playlists.get(10).getPlaylistId());
        assertEquals(12, playlists.get(11).getPlaylistId());
        assertEquals(13, playlists.get(12).getPlaylistId());
    }

    @Test
    public void testDelete() {
        PlaylistPersistence persistence = new PlaylistPersistenceStub();
        assertNotNull(persistence);

        assert (persistence.insertPlaylist(playlists.get(0)));
        assert (persistence.insertPlaylist(playlists.get(1)));
        assert (persistence.insertPlaylist(playlists.get(2)));
        assert (!persistence.insertPlaylist(playlists.get(3)));

        assert (persistence.deletePlaylist(playlists.get(2)));
        assert (!persistence.deletePlaylist(playlists.get(3)));

        assertEquals(12, persistence.getAllPlaylists().size());
    }

    @Test
    public void testUpdate() {
        PlaylistPersistence persistence = new PlaylistPersistenceStub();
        assertNotNull(persistence);

        assert (persistence.insertPlaylist(playlists.get(0)));
        assert (persistence.insertPlaylist(playlists.get(1)));
        assert (persistence.insertPlaylist(playlists.get(2)));
        assert (!persistence.insertPlaylist(playlists.get(3)));

        playlists.get(0).setName("Different name");
        persistence.updatePlaylist(playlists.get(0));

        assertEquals(playlists.get(0).getName(), persistence.getAllPlaylists().get(10).getName());

        playlists.get(0).setName("Playlist1");
    }

    @After
    public void tearDown() {
        System.out.println("Finished unit tests for PlaylistPersistence.");
    }

}
