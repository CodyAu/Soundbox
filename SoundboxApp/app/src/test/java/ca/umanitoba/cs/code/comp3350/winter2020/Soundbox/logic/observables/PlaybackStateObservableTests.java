package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackController;

import static org.junit.Assert.assertEquals;

public class PlaybackStateObservableTests {

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for PlaybackStateObservable.");
    }

    @Test
    public void testObservable(){
        PlaybackStateObservable observable = new PlaybackStateObservable();

        class TestObserver implements SoundboxObserver<PlaybackController.PlaybackState> {
            private PlaybackController.PlaybackState state;

            @Override
            public void update(SoundboxObservable<? extends PlaybackController.PlaybackState> observable, PlaybackController.PlaybackState o) {
                state = observable.getValue();
            }

            public PlaybackController.PlaybackState getRecent(){
                return state;
            }
        }

        PlaybackController.PlaybackState state = PlaybackController.PlaybackState.PLAYING;

        TestObserver observer = new TestObserver();
        observable.addObserver(observer);

        observable.setValue(state);
        assertEquals(observer.getRecent(), state);

        assertEquals(1, observable.countObservers());

        observable.deleteObserver(observer);

        assertEquals(0, observable.countObservers());
    }

    @After
    public void tearDown() {
        System.out.println("Finished unit tests for PlaybackStateObservable.");
    }

}
