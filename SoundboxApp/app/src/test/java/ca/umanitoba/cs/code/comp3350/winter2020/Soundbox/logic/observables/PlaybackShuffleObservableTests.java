package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlaybackShuffleObservableTests {

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for PlaybackShuffleObservable.");
    }

    @Test
    public void testObservable(){
        PlaybackShuffleObservable observable = new PlaybackShuffleObservable();

        class TestObserver implements SoundboxObserver<Boolean> {
            private boolean shuffle;

            @Override
            public void update(SoundboxObservable<? extends Boolean> observable, Boolean o) {
                shuffle = observable.getValue();
            }

            public boolean getRecent(){
                return shuffle;
            }
        }

        TestObserver observer = new TestObserver();
        observable.addObserver(observer);

        observable.setValue(true);
        assertEquals(observer.getRecent(), true);

        assertEquals(1, observable.countObservers());

        observable.deleteObserver(observer);

        assertEquals(0, observable.countObservers());
    }

    @After
    public void tearDown() {
        System.out.println("Finished unit tests for PlaybackShuffleObservable.");
    }

}
