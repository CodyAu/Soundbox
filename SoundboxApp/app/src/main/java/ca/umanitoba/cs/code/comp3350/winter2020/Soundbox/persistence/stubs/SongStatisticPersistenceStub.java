package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.stubs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongStatistic;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.SongStatisticPersistence;

public class SongStatisticPersistenceStub implements SongStatisticPersistence {
    private List<SongStatistic> statistics;

    public SongStatisticPersistenceStub() {
        statistics = new ArrayList<SongStatistic>();
    }

    @Override
    public List<SongStatistic> getAllSongStatistics() {
        return Collections.unmodifiableList(statistics);
    }

    @Override
    public boolean insertSongStatistic(SongStatistic statistic) {
        if(!statistics.contains(statistic))
            return statistics.add(statistic);
        return false;
    }

    @Override
    public boolean deleteSongStatistic(SongStatistic statistic) {
        return statistics.remove(statistic);
    }

    @Override
    public boolean updateSongStatistic(SongStatistic statistic) {
        int index = statistics.indexOf(statistic);
        if (index >= 0) {
            statistics.set(index, statistic);
            return true;
        }
        return false;
    }
}
