package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;

import static org.junit.Assert.assertEquals;

public class AddSongPlaybackQueueObservableTests {

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for AddSongPlaybackQueueObservable.");
    }

    @Test
    public void testObservable() {
        AddSongPlaybackQueueObservable observable = new AddSongPlaybackQueueObservable();

        class TestObserver implements SoundboxObserver<Song> {
            private Song song;

            @Override
            public void update(SoundboxObservable<? extends Song> observable, Song o) {
                song = observable.getValue();
            }

            public Song getRecent() {
                return song;
            }

        }

        Song song = new Song.Builder()
                .setSongId(1)
                .setSongName("Test")
                .setArtist("none")
                .setLength(4)
                .setFilepath("/music.mp3")
                .build();

        TestObserver observer = new TestObserver();
        observable.addObserver(observer);

        observable.setValue(song);
        assertEquals(observer.getRecent(), song);

        assertEquals(1, observable.countObservers());

        observable.deleteObserver(observer);

        assertEquals(0, observable.countObservers());
    }

    @After
    public void tearDown() {
        System.out.println("Finished unit tests for AddSongPlaybackQueueObservable.");
    }

}
