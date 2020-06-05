package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SwapSongPlaybackQueueObservableTests {

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for SwapSongPlaybackQueueObservable.");
    }

    @Test
    public void testObservable() {
        SwapSongPlaybackQueueObservable observable = new SwapSongPlaybackQueueObservable();

        class TestObserver implements SoundboxObserver<int[]> {
            private int[] positions;

            @Override
            public void update(SoundboxObservable<? extends int[]> observable, int[] o) {
                positions = observable.getValue();
            }

            public int[] getRecent() {
                return positions;
            }

        }

        int[] positions = new int[]{0, 5};

        TestObserver observer = new TestObserver();
        observable.addObserver(observer);

        observable.setValue(new int[]{0, 5});
        assertEquals(observer.getRecent()[0], positions[0]);
        assertEquals(observer.getRecent()[1], positions[1]);

        assertEquals(1, observable.countObservers());

        observable.deleteObserver(observer);

        assertEquals(0, observable.countObservers());
    }

    @After
    public void tearDown() {
        System.out.println("Finished unit tests for SwapSongPlaybackQueueObservable.");
    }

}
