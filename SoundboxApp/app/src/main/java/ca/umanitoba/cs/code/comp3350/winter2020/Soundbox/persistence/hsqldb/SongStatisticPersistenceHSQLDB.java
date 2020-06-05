package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.hsqldb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.PersistenceSession;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongStatistic;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.SongStatisticPersistence;

public class SongStatisticPersistenceHSQLDB implements SongStatisticPersistence {
    private List<SongStatistic> statistics;

    public SongStatisticPersistenceHSQLDB() {
        statistics = new ArrayList<SongStatistic>();

        loadSavedStatistics();
    }

    private void loadSavedStatistics() {
        try (Connection connection = PersistenceSession.getConnection()) {
            final Statement statement = connection.createStatement();
            final ResultSet results = statement.executeQuery("SELECT * FROM SongStatistics");
            while (results.next()) {
                long songId = results.getLong("song_id");
                int statisticValue = results.getInt("stats_value");

                int statisticId = results.getInt("stats_id");
                SongStatistic.Statistic statistic = SongStatistic.Statistic.fromOrdinal(statisticId);

                SongStatistic songStatistic = new SongStatistic(songId, statistic, statisticValue);
                statistics.add(songStatistic);
            }

            statement.close();
            results.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<SongStatistic> getAllSongStatistics() {
        return Collections.unmodifiableList(statistics);
    }

    @Override
    public boolean insertSongStatistic(SongStatistic statistic) {
        if (!statistics.contains(statistic)) {
            try (Connection connection = PersistenceSession.getConnection()) {
                final PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO SongStatistics " +
                                "(song_id, stats_id, stats_value) " +
                                "VALUES " +
                                "(?, ?, ?)"
                );

                statement.setLong(1, statistic.getSongId());
                statement.setInt(2, statistic.getStatistic().ordinal());
                statement.setInt(3, statistic.getValue());

                statement.executeUpdate();
                statement.close();

                statistics.add(statistic);
                return true;
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean deleteSongStatistic(SongStatistic statistic) {
        try (Connection connection = PersistenceSession.getConnection()) {
            if (statistics.contains(statistic)) {
                final PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM SongStatistics " +
                                "WHERE song_id = ?" +
                                "AND stats_id = ?"
                );

                statement.setLong(1, statistic.getSongId());
                statement.setInt(2, statistic.getStatistic().ordinal());

                statement.executeUpdate();
                statement.close();

                return statistics.remove(statistic);
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateSongStatistic(SongStatistic statistic) {
        try (Connection connection = PersistenceSession.getConnection()) {
            if (statistics.contains(statistic)) {
                final PreparedStatement statement = connection.prepareStatement(
                        "UPDATE SongStatistics " +
                                "SET " +
                                "stats_value = ? " +
                                "WHERE song_id = ? " +
                                "AND stats_id = ?"
                );

                statement.setInt(1, statistic.getValue());

                statement.setLong(2, statistic.getSongId());
                statement.setInt(3, statistic.getStatistic().ordinal());

                statement.executeUpdate();
                statement.close();

                return true;
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
