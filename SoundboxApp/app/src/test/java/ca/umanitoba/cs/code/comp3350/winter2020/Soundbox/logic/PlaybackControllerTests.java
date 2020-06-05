package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.stubs.SongPersistenceStub;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.mock;

public class PlaybackControllerTests {
    private SongController controller;

    @Before
    public void setUp() throws IOException {
        System.out.println("Starting unit tests for PlaybackController.");

        MediaPlayer mediaPlayer = mock(MediaPlayer.class);
        doNothing().when(mediaPlayer).pause();
        doNothing().when(mediaPlayer).start();
        doNothing().when(mediaPlayer).reset();
        doNothing().when(mediaPlayer).prepare();
        doNothing().when(mediaPlayer).prepareAsync();
        doNothing().when(mediaPlayer).setAudioStreamType(isA(Integer.class));
        doNothing().when(mediaPlayer).setDataSource(anyString());
        doNothing().when(mediaPlayer).setDataSource(isA(Context.class), isA(Uri.class));

        MainActivity activity = mock(MainActivity.class);

        controller = new SongController(new SongPersistenceStub());
        PlaybackController.init(activity, mediaPlayer, controller);
    }

    @Test
    public void testSetSongByClass() {
        Song song = new Song.Builder()
                .setSongId(1)
                .setSongName("Test")
                .setArtist("none")
                .setLength(4)
                .build();

        assertEquals(true, PlaybackController.setSong(song));
        assertEquals(song, PlaybackController.getSong());
        assertEquals(0, PlaybackController.getDuration());
        assertEquals(0, PlaybackController.getPosition());
        assert (PlaybackController.getPlaybackState().equals(PlaybackController.PlaybackState.PAUSED));
    }

    @Test
    public void testSetSongByQueue() {
        Song song = new Song.Builder()
                .setSongId(1)
                .setSongName("Test")
                .setArtist("none")
                .setLength(4)
                .build();

        List<Song> songs = new ArrayList<Song>();
        songs.add(song);

        Playlist playlist = new Playlist(1, "playlist1", -1, songs);
        PlaybackQueue queue = new PlaybackQueue(songs);

        PlaybackController.setPlaybackQueue(songs);

        assertEquals(song, PlaybackController.getSong());
        assertEquals(0, PlaybackController.getDuration());
        assertEquals(0, PlaybackController.getPosition());
        assert (PlaybackController.getPlaybackState().equals(PlaybackController.PlaybackState.PAUSED));

        PlaybackController.setPlaybackQueue(playlist);

        assertEquals(song, PlaybackController.getSong());
        assertEquals(0, PlaybackController.getDuration());
        assertEquals(0, PlaybackController.getPosition());
        assert (PlaybackController.getPlaybackState().equals(PlaybackController.PlaybackState.PAUSED));

        PlaybackController.setPlaybackQueue(queue);

        assertEquals(song, PlaybackController.getSong());
        assertEquals(0, PlaybackController.getDuration());
        assertEquals(0, PlaybackController.getPosition());
        assert (PlaybackController.getPlaybackState().equals(PlaybackController.PlaybackState.PAUSED));

        PlaybackController.addPlaybackQueue(song);
        PlaybackController.addPlaybackQueue(songs);
        PlaybackController.addPlaybackQueue(playlist);
        PlaybackController.addPlaybackQueue(queue);

        assertEquals(song, PlaybackController.getSong());
        assertEquals(0, PlaybackController.getDuration());
        assertEquals(0, PlaybackController.getPosition());
        assert (PlaybackController.getPlaybackState().equals(PlaybackController.PlaybackState.PAUSED));

        assertEquals(5, PlaybackController.getPlaybackQueue().queueSize());
    }

    @Test
    public void testSetSongById() {
        Song song = new Song.Builder()
                .setSongId(2)
                .setSongName("Test2")
                .setArtist("me")
                .setLength(2)
                .build();
        controller.insertSong(song);

        assertEquals(true, PlaybackController.setSong(song.getSongId()));
        assertEquals(song, PlaybackController.getSong());
        assertEquals(0, PlaybackController.getDuration());
        assertEquals(0, PlaybackController.getPosition());
        assert (PlaybackController.getPlaybackState().equals(PlaybackController.PlaybackState.PAUSED));

        controller.deleteSong(song);
    }

    @Test
    public void testDeleteSongByQueue() {
        List<Song> songs = new ArrayList<Song>();

        songs.add(new Song.Builder().setSongId(1).setSongName("Test1").setFilepath("/Test1").build());
        songs.add(new Song.Builder().setSongId(2).setSongName("Test2").setFilepath("/Test2").build());
        songs.add(new Song.Builder().setSongId(3).setSongName("Test3").setFilepath("/Test3").build());

        PlaybackController.setPlaybackQueue(songs);

        assert (PlaybackController.deletePlaybackQueue(songs.get(2)));
        assertEquals(2, PlaybackController.getPlaybackQueue().queueSize());

        assert (PlaybackController.deletePlaybackQueue(1));
        assertEquals(1, PlaybackController.getPlaybackQueue().queueSize());

        assertEquals(songs.get(0), PlaybackController.getSong());

        assert (!PlaybackController.deletePlaybackQueue(songs.get(1)));
        assert (PlaybackController.deletePlaybackQueue(songs.get(0)));

        assertEquals(0, PlaybackController.getPlaybackQueue().queueSize());
        assertEquals(null, PlaybackController.getSong());
    }

    @Test
    public void testSwaps() {
        List<Song> songs = new ArrayList<Song>();

        songs.add(new Song.Builder().setSongId(1).setSongName("Test1").setFilepath("/Test1").build());
        songs.add(new Song.Builder().setSongId(2).setSongName("Test2").setFilepath("/Test2").build());
        songs.add(new Song.Builder().setSongId(3).setSongName("Test3").setFilepath("/Test3").build());

        PlaybackController.setPlaybackQueue(songs);

        PlaybackController.swapSongPlaybackQueue(0, 1);
        assertEquals(songs.get(0), PlaybackController.getPlaybackQueue().peekIndex(1));
        assertEquals(songs.get(1), PlaybackController.getPlaybackQueue().peekIndex(0));
        assertEquals(songs.get(2), PlaybackController.getPlaybackQueue().peekIndex(2));
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

        PlaybackController.clearPlaybackQueue();

        PlaybackController.playNext(songs);
        assertEquals(3, PlaybackController.getPlaybackQueue().queueSize());

        assert (PlaybackController.playNext(songs.get(2)));
        assertEquals(PlaybackController.getPlaybackQueue().peekIndex(1), songs.get(2));

        PlaybackController.playNext(moreSongs);
        assertEquals(5, PlaybackController.getPlaybackQueue().queueSize());
        assertEquals(PlaybackController.getPlaybackQueue().peekIndex(1), moreSongs.get(1));

        PlaybackController.playNext(4);
        assertEquals(5, PlaybackController.getPlaybackQueue().queueSize());
        assertEquals(PlaybackController.getPlaybackQueue().peekIndex(1), songs.get(1));
    }

    @Test
    public void testToggle() {
        Song song = new Song.Builder()
                .setSongId(1)
                .setSongName("Test")
                .setArtist("none")
                .setLength(4)
                .build();

        assertEquals(true, PlaybackController.setSong(song));
        assert (PlaybackController.getPlaybackState().equals(PlaybackController.PlaybackState.PAUSED));
        assertEquals(true, PlaybackController.toggle());
        assert (PlaybackController.getPlaybackState().equals(PlaybackController.PlaybackState.PLAYING));

        PlaybackController.toggleLoop();
        assert (PlaybackController.getPlaybackState().equals(PlaybackController.PlaybackState.PLAYING));
        assert (PlaybackController.getPlaybackMode().equals(PlaybackController.PlaybackMode.LOOP_CURRENT));
    }

    @Test
    public void testSongNavigation() {
        Song song = new Song.Builder()
                .setSongId(1)
                .setSongName("Test")
                .setArtist("none")
                .setLength(4)
                .build();

        assertEquals(true, PlaybackController.setSong(song));
        assert (PlaybackController.getPlaybackState().equals(PlaybackController.PlaybackState.PAUSED));

        assert (PlaybackController.toggleLoop().equals(PlaybackController.PlaybackMode.LOOP_CURRENT));
        assertEquals(true, PlaybackController.playNext());
        assertEquals(true, PlaybackController.playPrevious());

        PlaybackController.shuffle();
        assertEquals(song, PlaybackController.getSong());
    }

    @Test
    public void testQueue() {
        Playlist playlist = new Playlist(11, "Mix", -1);

        PlaybackController.setPlaybackQueue(playlist);
        assertEquals(playlist.songsSize(), PlaybackController.getPlaybackQueue().queueSize());

        PlaybackController.setPlaybackQueue(playlist.getSongs());
        assertEquals(playlist.songsSize(), PlaybackController.getPlaybackQueue().queueSize());
    }

    @After
    public void tearDown() {
        PlaybackController.cleanMediaPlayer();
        System.out.println("Finished unit tests for PlaybackController.");
    }

}
