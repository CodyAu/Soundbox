package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongStatistic;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.stubs.SongStatisticPersistenceStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SongStatisticPersistenceTests {
    List<SongStatistic> statistics;

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for SongStatisiticPersistence.");

        statistics = new ArrayList<SongStatistic>();

        statistics.add(new SongStatistic(1, SongStatistic.Statistic.PLAYS));
        statistics.add(new SongStatistic(1, SongStatistic.Statistic.LISTEN_TIME));
        statistics.add(new SongStatistic(1, SongStatistic.Statistic.LIKES));
        statistics.add(new SongStatistic(1, SongStatistic.Statistic.DISLIKES));
        statistics.add(new SongStatistic(1, SongStatistic.Statistic.DISLIKES)); //same ids as 4th one, should fail insert
    }

    @Test
    public void testInsert() {
        SongStatisticPersistence persistence = new SongStatisticPersistenceStub();
        assertNotNull(persistence);

        assert (persistence.insertSongStatistic(statistics.get(0)));
        assert (persistence.insertSongStatistic(statistics.get(1)));
        assert (persistence.insertSongStatistic(statistics.get(2)));
        assert (persistence.insertSongStatistic(statistics.get(3)));
        assert (!persistence.insertSongStatistic(statistics.get(4)));

        assertEquals(4, persistence.getAllSongStatistics().size());
    }

    @Test
    public void testGetAll() {
        SongStatisticPersistence persistence = new SongStatisticPersistenceStub();
        assertNotNull(persistence);

        assert (persistence.insertSongStatistic(statistics.get(0)));
        assert (persistence.insertSongStatistic(statistics.get(1)));
        assert (persistence.insertSongStatistic(statistics.get(2)));
        assert (persistence.insertSongStatistic(statistics.get(3)));

        List<SongStatistic> statistics = persistence.getAllSongStatistics();
        assertEquals(4, statistics.size());

        assert (statistics.get(0).getStatistic().equals(SongStatistic.Statistic.PLAYS));
        assert (statistics.get(1).getStatistic().equals(SongStatistic.Statistic.LISTEN_TIME));
        assert (statistics.get(2).getStatistic().equals(SongStatistic.Statistic.LIKES));
        assert (statistics.get(3).getStatistic().equals(SongStatistic.Statistic.DISLIKES));
    }

    @Test
    public void testDelete() {
        SongStatisticPersistence persistence = new SongStatisticPersistenceStub();
        assertNotNull(persistence);

        assert (persistence.insertSongStatistic(statistics.get(0)));
        assert (persistence.insertSongStatistic(statistics.get(1)));
        assert (persistence.insertSongStatistic(statistics.get(2)));
        assert (persistence.insertSongStatistic(statistics.get(3)));

        assert (persistence.deleteSongStatistic(statistics.get(3)));
        assert (!persistence.deleteSongStatistic(statistics.get(4)));

        assertEquals(3, persistence.getAllSongStatistics().size());
    }

    @Test
    public void testUpdate() {
        SongStatisticPersistence persistence = new SongStatisticPersistenceStub();
        assertNotNull(persistence);

        assert (persistence.insertSongStatistic(statistics.get(0)));
        assert (persistence.insertSongStatistic(statistics.get(1)));
        assert (persistence.insertSongStatistic(statistics.get(2)));

        statistics.get(0).setSongId(24);
        assert (persistence.updateSongStatistic(statistics.get(0)));

        assertEquals(statistics.get(0).getSongId(), persistence.getAllSongStatistics().get(0).getSongId());

        statistics.get(0).setSongId(1);
    }


    @After
    public void tearDown() {
        System.out.println("Finished unit tests for SongStatisiticPersistence.");
    }

}
