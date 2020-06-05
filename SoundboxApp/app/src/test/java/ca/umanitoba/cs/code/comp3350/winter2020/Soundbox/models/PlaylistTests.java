package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PlaylistTests {

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for Playlist.");
    }

    @Test //Test getter and setter methods of Playlist class to ensure accuracy
    public void testPlaylist() {
        long playlistId = 9912;
        String playlistName = "Canadian Bar Rock";
        int thumbnail = 99;
        List<Song> songs = new ArrayList<>();
        songs.add(new Song.Builder().build());

        //Test constructor and getters
        Playlist pl = new Playlist(playlistId, playlistName, thumbnail, songs);
        assertEquals(playlistId, pl.getPlaylistId());
        assertEquals(playlistName, pl.getName());
        assertEquals(thumbnail, pl.getThumbnail());
        assertEquals(songs, pl.getSongs());

        playlistId = 90876;
        playlistName = "Löttchen's Playlist";
        thumbnail = 92;
        songs = new ArrayList<>();
        songs.add(new Song.Builder().build());

        //Test setters
        pl.clearSongs();
        pl.setPlaylistId(playlistId);
        pl.setName(playlistName);
        pl.setThumbnail(thumbnail);
        for(Song song  : songs)
            pl.insertSong(song);

        assertEquals(playlistId, pl.getPlaylistId());
        assertEquals(playlistName, pl.getName());
        assertEquals(thumbnail, pl.getThumbnail());
        assertEquals(songs.size(), pl.songsSize());
    }

    @After
    public void tearDown() {
        System.out.println("Finished unit tests for Playlist.");
    }

}
