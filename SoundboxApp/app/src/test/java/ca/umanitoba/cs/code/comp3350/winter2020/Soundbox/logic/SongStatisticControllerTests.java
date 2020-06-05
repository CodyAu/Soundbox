package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongStatistic;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.stubs.SongStatisticPersistenceStub;

import static org.junit.Assert.assertEquals;

public class SongStatisticControllerTests {

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for SongStatisticController.");
    }

    @Test
    public void testSongStatisticController() {
        SongStatistic statistic = new SongStatistic(1, SongStatistic.Statistic.LIKES);

        SongStatisticController controller = new SongStatisticController(new SongStatisticPersistenceStub());
        controller.insertStatistic(statistic);
        assertEquals(statistic, controller.getStatisticsByType(SongStatistic.Statistic.LIKES).get(0));
        assert (!controller.getAllStatistics().isEmpty());

        statistic.incrementValue();
        controller.updateStatistic(statistic);
        assertEquals(1, controller.getStatisticsByType(SongStatistic.Statistic.LIKES).get(0).getValue());

        boolean deleted = controller.deleteStatistic(statistic);
        assert (deleted);
        assert (controller.getAllStatistics().isEmpty());
    }

    @Test
    public void testSongStatisticControllerFetches() {
        SongStatisticController controller = new SongStatisticController(new SongStatisticPersistenceStub());

        SongStatistic statistic1 = new SongStatistic(1, SongStatistic.Statistic.PLAYS);
        SongStatistic statistic2 = new SongStatistic(1, SongStatistic.Statistic.LISTEN_TIME);
        SongStatistic statistic3 = new SongStatistic(1, SongStatistic.Statistic.LIKES);
        SongStatistic statistic4 = new SongStatistic(1, SongStatistic.Statistic.DISLIKES);

        controller.insertStatistic(statistic1);
        controller.insertStatistic(statistic2);
        controller.insertStatistic(statistic3);
        controller.insertStatistic(statistic4);

        List<SongStatistic> statistics = controller.getAllStatistics();
        assert (statistics.contains(statistic1));
        assert (statistics.contains(statistic2));
        assert (statistics.contains(statistic3));
        assert (statistics.contains(statistic4));

        statistics = controller.getStatisticsByType(SongStatistic.Statistic.LIKES);
        assert (!statistics.contains(statistic1));
        assert (!statistics.contains(statistic2));
        assert (statistics.contains(statistic3));
        assert (!statistics.contains(statistic4));
    }

    @After
    public void tearDown() {
        System.out.println("Finished unit tests for SongStatisticController.");
    }

}
