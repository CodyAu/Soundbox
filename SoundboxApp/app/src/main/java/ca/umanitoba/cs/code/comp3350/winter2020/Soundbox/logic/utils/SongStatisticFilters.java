package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils;

import java.util.ArrayList;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongStatistic;

public final class SongStatisticFilters {

    public static List<SongStatistic> getStatisticsByType(SongStatistic.Statistic type, List<SongStatistic> statistics) {
        List<SongStatistic> filterStatistics = new ArrayList<SongStatistic>();
        for (SongStatistic statistic : statistics) {
            if (statistic.getStatistic().equals(type))
                filterStatistics.add(statistic);
        }
        return filterStatistics;
    }

    public static int sumStatisticsValues(List<SongStatistic> statistics) {
        int sum = 0;
        for (SongStatistic statistic : statistics) {
            sum += statistic.getValue();
        }
        return sum;
    }

}
