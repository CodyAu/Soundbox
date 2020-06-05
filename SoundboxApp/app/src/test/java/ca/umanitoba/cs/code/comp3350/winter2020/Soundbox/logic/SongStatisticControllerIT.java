package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.Services;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongStatistic;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SongStatisticControllerIT {
    private File tempDB;
    private SongStatisticController controller;
    List<SongStatistic> statistics;

    @Before
    public void setUp() throws IOException {
        System.out.println("Starting integration tests for SongStatisticController.");
        tempDB = TestUtils.copyDB();
        controller = new SongStatisticController();
        assertNotNull(controller);

        statistics = new ArrayList<SongStatistic>();

        //need to make foreign key condition
        (new SongController()).insertSong(new Song.Builder()
                .setSongId(1)
                .setSongName("Test")
                .setGenre("no")
                .setArtist("no")
                .setAlbum("no")
                .setFilepath("/Tests1").build());

        statistics.add(new SongStatistic(1, SongStatistic.Statistic.PLAYS));
        statistics.add(new SongStatistic(1, SongStatistic.Statistic.LISTEN_TIME));
        statistics.add(new SongStatistic(1, SongStatistic.Statistic.LIKES));
        statistics.add(new SongStatistic(1, SongStatistic.Statistic.DISLIKES));
        statistics.add(new SongStatistic(1, SongStatistic.Statistic.DISLIKES)); //same ids as 4th one, should fail insert
    }

    @Test
    public void testInsert() {
        assert (controller.insertStatistic(statistics.get(0)));
        assert (controller.insertStatistic(statistics.get(1)));
        assert (controller.insertStatistic(statistics.get(2)));
        assert (controller.insertStatistic(statistics.get(3)));
        assert (!controller.insertStatistic(statistics.get(4)));

        assertEquals(4, controller.getAllStatistics().size());

        assert (controller.deleteStatistic(statistics.get(0)));
        assert (controller.deleteStatistic(statistics.get(1)));
        assert (controller.deleteStatistic(statistics.get(2)));
        assert (controller.deleteStatistic(statistics.get(3)));
    }

    @Test
    public void testGetAll() {
        assert (controller.insertStatistic(statistics.get(0)));
        assert (controller.insertStatistic(statistics.get(1)));
        assert (controller.insertStatistic(statistics.get(2)));
        assert (controller.insertStatistic(statistics.get(3)));

        List<SongStatistic> receivedStatistics = controller.getAllStatistics();
        assertEquals(4, receivedStatistics.size());

        assert(receivedStatistics.get(0).getStatistic().equals(SongStatistic.Statistic.PLAYS));
        assert(receivedStatistics.get(1).getStatistic().equals(SongStatistic.Statistic.LISTEN_TIME));
        assert(receivedStatistics.get(2).getStatistic().equals(SongStatistic.Statistic.LIKES));
        assert(receivedStatistics.get(3).getStatistic().equals(SongStatistic.Statistic.DISLIKES));

        assert (controller.deleteStatistic(statistics.get(0)));
        assert (controller.deleteStatistic(statistics.get(1)));
        assert (controller.deleteStatistic(statistics.get(2)));
        assert (controller.deleteStatistic(statistics.get(3)));
    }

    @Test
    public void testDelete() {
        assert (controller.insertStatistic(statistics.get(0)));
        assert (controller.insertStatistic(statistics.get(1)));
        assert (controller.insertStatistic(statistics.get(2)));
        assert (controller.insertStatistic(statistics.get(3)));

        assert (controller.deleteStatistic(statistics.get(3)));
        assert (!controller.deleteStatistic(statistics.get(4)));

        assertEquals(3, controller.getAllStatistics().size());

        assert (controller.deleteStatistic(statistics.get(0)));
        assert (controller.deleteStatistic(statistics.get(1)));
        assert (controller.deleteStatistic(statistics.get(2)));
    }

    @Test
    public void testUpdate() {
        assert (controller.insertStatistic(statistics.get(0)));
        assert (controller.insertStatistic(statistics.get(1)));
        assert (controller.insertStatistic(statistics.get(2)));
        assert (controller.insertStatistic(statistics.get(3)));

        statistics.get(0).setSongId(24);
        assert(controller.updateStatistic(statistics.get(0)));

        assertEquals(statistics.get(0).getSongId(), controller.getAllStatistics().get(0).getSongId());

        statistics.get(0).setSongId(1);

        assert (controller.deleteStatistic(statistics.get(0)));
        assert (controller.deleteStatistic(statistics.get(1)));
        assert (controller.deleteStatistic(statistics.get(2)));
        assert (controller.deleteStatistic(statistics.get(3)));
    }

    @After
    public void tearDown() {
        tempDB.delete();
        Services.clean();
        System.out.println("Finished integration tests for SongStatisticController.");
    }

}
