package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackController;

import static org.junit.Assert.assertEquals;

public class PlaybackModeObservableTests {

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for PlaybackModeObservable.");
    }

    @Test
    public void testObservable(){
        PlaybackModeObservable observable = new PlaybackModeObservable();

        class TestObserver implements SoundboxObserver<PlaybackController.PlaybackMode> {
            private PlaybackController.PlaybackMode mode;

            @Override
            public void update(SoundboxObservable<? extends PlaybackController.PlaybackMode> observable, PlaybackController.PlaybackMode o) {
                mode = observable.getValue();
            }

            public PlaybackController.PlaybackMode getRecent(){
                return mode;
            }
        }

        PlaybackController.PlaybackMode mode = PlaybackController.PlaybackMode.ONCE_PLAYLIST;

        TestObserver observer = new TestObserver();
        observable.addObserver(observer);

        observable.setValue(mode);
        assertEquals(observer.getRecent(), mode);

        assertEquals(1, observable.countObservers());

        observable.deleteObserver(observer);

        assertEquals(0, observable.countObservers());
    }

    @After
    public void tearDown() {
        System.out.println("Finished unit tests for PlaybackModeObservable.");
    }

}
