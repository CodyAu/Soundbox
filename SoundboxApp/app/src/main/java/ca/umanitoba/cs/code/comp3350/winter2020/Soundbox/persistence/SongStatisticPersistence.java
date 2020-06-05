package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence;

import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongStatistic;

public interface SongStatisticPersistence {
    List<SongStatistic> getAllSongStatistics();

    boolean insertSongStatistic(SongStatistic statistic);

    boolean deleteSongStatistic(SongStatistic statistic);

    boolean updateSongStatistic(SongStatistic statistic);


}
