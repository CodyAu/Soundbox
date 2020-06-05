package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic;

import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.Services;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils.SongStatisticFilters;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongStatistic;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.SongStatisticPersistence;

public class SongStatisticController {
    private SongStatisticPersistence songStatisticPersistence;

    public SongStatisticController(SongStatisticPersistence persistence) {
        songStatisticPersistence = persistence;
    }

    public SongStatisticController() {
        songStatisticPersistence = Services.getSongStatisticPersistence();
    }

    public List<SongStatistic> getAllStatistics() {
        return songStatisticPersistence.getAllSongStatistics();
    }

    public List<SongStatistic> getStatisticsByType(SongStatistic.Statistic type) {
        return SongStatisticFilters.getStatisticsByType(type, songStatisticPersistence.getAllSongStatistics());
    }

    public boolean insertStatistic(SongStatistic statistic) {
        return songStatisticPersistence.insertSongStatistic(statistic);
    }

    public boolean updateStatistic(SongStatistic statistic) {
        return songStatisticPersistence.updateSongStatistic(statistic);
    }

    public boolean deleteStatistic(SongStatistic statistic) {
        return songStatisticPersistence.deleteSongStatistic(statistic);
    }

}
