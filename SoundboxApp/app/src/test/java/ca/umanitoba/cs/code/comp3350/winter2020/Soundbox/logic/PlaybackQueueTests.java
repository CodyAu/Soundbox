package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;

import static org.junit.Assert.assertEquals;

public class PlaybackQueueTests {

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for PlaybackQueue.");
    }

    @Test
    public void testInitialization() {
        List<Song> songs = new ArrayList<Song>();

        songs.add(new Song.Builder().setSongId(1).setSongName("Test1").setFilepath("/Test1").build());
        songs.add(new Song.Builder().setSongId(2).setSongName("Test2").setFilepath("/Test2").build());
        songs.add(new Song.Builder().setSongId(3).setSongName("Test3").setFilepath("/Test3").build());

        Playlist playlist = new Playlist(1, "Playlist1", -1, songs);

        PlaybackQueue queue = new PlaybackQueue();
        assertEquals(0, queue.queueSize());
        assert (queue.isEmpty());
        assertEquals(-1, queue.getIndex());

        queue.addSong(new Song.Builder().setSongName("Test4").build());
        assertEquals(1, queue.queueSize());
        assert (!queue.isEmpty());
        assertEquals("Test4", queue.getCurrentSong().getSongName());
        assertEquals(0, queue.getIndex());

        queue = new PlaybackQueue(songs);
        assertEquals(3, queue.queueSize());
        assert (!queue.isEmpty());
        assertEquals("Test1", queue.getCurrentSong().getSongName());
        assertEquals(0, queue.getIndex());

        queue = new PlaybackQueue(playlist);
        assertEquals(3, queue.queueSize());
        assert (!queue.isEmpty());
        assertEquals("Test1", queue.getCurrentSong().getSongName());
        assertEquals(0, queue.getIndex());
    }

    @Test
    public void testInsertions() {
        List<Song> songs = new ArrayList<Song>();

        songs.add(new Song.Builder().setSongId(1).setSongName("Test1").setFilepath("/Test1").build());
        songs.add(new Song.Builder().setSongId(2).setSongName("Test2").setFilepath("/Test2").build());
        songs.add(new Song.Builder().setSongId(3).setSongName("Test3").setFilepath("/Test3").build());

        Playlist playlist = new Playlist(1, "Playlist1", -1, songs);

        PlaybackQueue queue = new PlaybackQueue();
        assertEquals(0, queue.queueSize());
        assert (queue.isEmpty());
        assertEquals(-1, queue.getIndex());

        queue.addSong(new Song.Builder().setSongId(4).setSongName("Test4").build());
        assertEquals(1, queue.queueSize());
        assert (!queue.isEmpty());
        assertEquals("Test4", queue.getCurrentSong().getSongName());
        assertEquals(0, queue.getIndex());

        queue.addSongs(songs);
        assertEquals(4, queue.queueSize());
        assert (!queue.isEmpty());
        assertEquals("Test4", queue.getCurrentSong().getSongName());
        assertEquals(0, queue.getIndex());

        queue.setAllowDuplicates(false);
        assert (!queue.getAllowDuplicates());

        queue.addSongs(playlist);
        assertEquals(4, queue.queueSize());

        queue.addSongs(queue);
        assertEquals(4, queue.queueSize());

        queue.setSongs(songs);
        assertEquals(3, queue.queueSize());
        assert (!queue.isEmpty());
        assertEquals("Test1", queue.getCurrentSong().getSongName());
        assertEquals(0, queue.getIndex());

        queue.setSongs(playlist);
        assertEquals(3, queue.queueSize());
        assert (!queue.isEmpty());
        assertEquals("Test1", queue.getCurrentSong().getSongName());
        assertEquals(0, queue.getIndex());

        queue.setSongs(new PlaybackQueue(songs));
        assertEquals(3, queue.queueSize());
        assert (!queue.isEmpty());
        assertEquals("Test1", queue.getCurrentSong().getSongName());
        assertEquals(0, queue.getIndex());

        queue.addSong(0, new Song.Builder().setSongId(5).setSongName("Test5").build());
        assertEquals("Test5", queue.peekIndex(0).getSongName());
        assertEquals(1, queue.getIndex());
    }

    @Test
    public void testDeletions() {
        List<Song> songs = new ArrayList<Song>();

        songs.add(new Song.Builder().setSongId(1).setSongName("Test1").setFilepath("/Test1").build());
        songs.add(new Song.Builder().setSongId(2).setSongName("Test2").setFilepath("/Test2").build());
        songs.add(new Song.Builder().setSongId(3).setSongName("Test3").setFilepath("/Test3").build());

        PlaybackQueue queue = new PlaybackQueue();
        queue.addSongs(songs);

        assert (queue.deleteSong(songs.get(2)));
        assertEquals(2, queue.queueSize());

        assert (queue.deleteSong(1));
        assertEquals(1, queue.queueSize());

        assertEquals(songs.get(0), queue.getCurrentSong());

        assert (!queue.deleteSong(songs.get(1)));
        assert (queue.deleteSong(songs.get(0)));

        assertEquals(0, queue.queueSize());
        assertEquals(null, queue.getCurrentSong());
    }

    @Test
    public void testSwaps() {
        List<Song> songs = new ArrayList<Song>();

        songs.add(new Song.Builder().setSongId(1).setSongName("Test1").setFilepath("/Test1").build());
        songs.add(new Song.Builder().setSongId(2).setSongName("Test2").setFilepath("/Test2").build());
        songs.add(new Song.Builder().setSongId(3).setSongName("Test3").setFilepath("/Test3").build());

        PlaybackQueue queue = new PlaybackQueue();
        queue.addSongs(songs);

        assert (queue.swapSong(0, 1));
        assertEquals(songs.get(0), queue.peekIndex(1));
        assertEquals(songs.get(1), queue.peekIndex(0));
        assertEquals(songs.get(2), queue.peekIndex(2));
    }

    @Test
    public void testPlayNext() {
        List<Song> songs = new ArrayList<Song>();
        List<Song> moreSongs = new ArrayList<Song>();

        songs.add(new Song.Builder().setSongId(1).setSongName("Test1").setFilepath("/Test1").build());
        songs.add(new Song.Builder().setSongId(2).setSongName("Test2").setFilepath("/Test2").build());
        songs.add(new Song.Builder().setSongId(3).setSongName("Test3").setFilepath("/Test3").build());

        moreSongs.add(songs.get(0));
        moreSongs.add(new Song.Builder().setSongId(4).setSongName("Test4").setFilepath("/Test4").build());
        moreSongs.add(new Song.Builder().setSongId(5).setSongName("Test5").setFilepath("/Test5").build());

        PlaybackQueue queue = new PlaybackQueue();

        queue.playNext(songs);
        assertEquals(3, queue.queueSize());

        assert (queue.playNext(songs.get(2)));
        assertEquals(queue.peekIndex(1), songs.get(2));

        queue.playNext(moreSongs);
        assertEquals(5, queue.queueSize());
        assertEquals(queue.peekIndex(1), moreSongs.get(1));

        queue.playNext(4);
        assertEquals(5, queue.queueSize());
        assertEquals(queue.peekIndex(1), songs.get(1));
    }

    @Test
    public void testJumps() {
        PlaybackQueue queue = new PlaybackQueue();

        Song song1 = new Song.Builder().setSongId(1).setSongName("Test1").setFilepath("/Test1").build();
        Song song2 = new Song.Builder().setSongId(2).setSongName("Test2").setFilepath("/Test2").build();
        Song song3 = new Song.Builder().setSongId(3).setSongName("Test3").setFilepath("/Test3").build();

        queue.addSong(song1);
        queue.addSong(song2);
        queue.addSong(song3);

        assertEquals(song1, queue.getCurrentSong());
        assertEquals(0, queue.getIndex());

        assertEquals(song3, queue.peekLast());
        assertEquals(song3, queue.jumpLast());
        assertEquals(2, queue.getIndex());

        assertEquals(song1, queue.peekFirst());
        assertEquals(song1, queue.jumpFirst());
        assertEquals(0, queue.getIndex());

        assertEquals(song3, queue.peekIndex(2));
        assertEquals(song3, queue.jumpIndex(2));
        assertEquals(2, queue.getIndex());

        assertEquals(song2, queue.jumpSong(song2));
        assertEquals(1, queue.getIndex());

        assertEquals(null, queue.jumpSong(null));
        assertEquals(1, queue.getIndex()); //no change since jump invalid

        assertEquals(song3, queue.peekNext());
        assertEquals(song3, queue.jumpNext());
        assertEquals(2, queue.getIndex());

        assertEquals(song2, queue.peekPrevious());
        assertEquals(song2, queue.jumpPrevious());
        assertEquals(1, queue.getIndex());
    }

    @Test
    public void testClear() {
        PlaybackQueue queue = new PlaybackQueue();

        List<Song> songs = new ArrayList<Song>();

        Song song1 = new Song.Builder().setSongId(1).setSongName("Test1").setFilepath("/Test1").build();
        Song song2 = new Song.Builder().setSongId(2).setSongName("Test2").setFilepath("/Test2").build();
        Song song3 = new Song.Builder().setSongId(3).setSongName("Test3").setFilepath("/Test3").build();

        songs.add(song1);
        songs.add(song2);
        songs.add(song3);

        Playlist playlist = new Playlist(1, "Playlist1", -1, songs);

        queue.clear();

        assertEquals(-1, queue.getIndex());
        assertEquals(null, queue.getPlaylist());
        assertEquals(null, queue.getCurrentSong());

        queue.setSongs(songs);

        assertEquals(0, queue.getIndex());
        assertEquals(null, queue.getPlaylist());
        assertEquals(song1, queue.getCurrentSong());

        queue.clear();

        assertEquals(-1, queue.getIndex());
        assertEquals(null, queue.getPlaylist());
        assertEquals(null, queue.getCurrentSong());

        queue.setSongs(playlist);

        assertEquals(0, queue.getIndex());
        assertEquals(playlist, queue.getPlaylist());
        assertEquals(song1, queue.getCurrentSong());

        queue.clear();

        assertEquals(-1, queue.getIndex());
        assertEquals(null, queue.getPlaylist());
        assertEquals(null, queue.getCurrentSong());
    }

    @Test
    public void testShuffle() {
        PlaybackQueue queue = new PlaybackQueue();

        Song song1 = new Song.Builder().setSongId(1).setSongName("Test1").setFilepath("/Test1").build();
        Song song2 = new Song.Builder().setSongId(2).setSongName("Test2").setFilepath("/Test2").build();
        Song song3 = new Song.Builder().setSongId(3).setSongName("Test3").setFilepath("/Test3").build();

        queue.addSong(song1);
        queue.addSong(song2);
        queue.addSong(song3);

        queue.jumpNext();
        queue.shuffleQueue();

        assertEquals(song2, queue.getCurrentSong()); //shuffle queue jumps to the current playing song
    }

    @After
    public void tearDown() {
        System.out.println("Finished unit tests for PlaybackQueue.");
    }

}
