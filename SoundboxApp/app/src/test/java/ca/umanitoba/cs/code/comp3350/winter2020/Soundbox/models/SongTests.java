package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SongTests {

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for Song.");
    }

    @Test //Test getter and setter methods of Song class to ensure accuracy
    public void testSong() {
        long song_id = 99;
        String song_name = "Fashionable People";
        String artist = "Joel Plaskett Emergency";
        String album = "Ashtray Rock";
        String length = "98";
        int score = 5;
        String filepath = "/home/music_lover/joel_plaskett";
        String bitrate = "320";

        Song s = new Song.Builder().build();
        s.setSongId(99);
        s.setSongName(song_name);
        s.setArtist(artist);
        s.setAlbum(album);
        s.setLength(Integer.parseInt(length));
        s.setScore(score);
        s.setFilepath(filepath);
        s.setBitrate(Integer.parseInt(bitrate));

        assertEquals(song_id, s.getSongId());
        assertEquals(song_name, s.getSongName());
        assertEquals(artist, s.getArtist());
        assertEquals(album, s.getAlbum());
        assertEquals(Long.parseLong(length), s.getLength());
        assertEquals(score, s.getScore(), .2);
        assertEquals(filepath, s.getFilepath());
        assertEquals(Long.parseLong(bitrate), s.getBitrate());

    }

    @After
    public void tearDown() {
        System.out.println("Finished unit tests for Song.");
    }

}
