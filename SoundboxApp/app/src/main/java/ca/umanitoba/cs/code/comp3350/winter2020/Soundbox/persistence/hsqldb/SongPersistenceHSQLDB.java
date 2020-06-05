package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.hsqldb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.PersistenceSession;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongStatistic;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.SongPersistence;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.SongStatisticPersistence;

public class SongPersistenceHSQLDB implements SongPersistence {
    private SongStatisticPersistence songStatisticPersistence;
    private List<Song> songs;
    private long nextId;

    public SongPersistenceHSQLDB(SongStatisticPersistence persistence) {
        this.songStatisticPersistence = persistence;
        songs = new ArrayList<Song>();

        loadSavedSongs();
    }

    private void loadSavedSongs() {
        try (Connection connection = PersistenceSession.getConnection()) {
            final Statement statement = connection.createStatement();
            final ResultSet results = statement.executeQuery("SELECT * FROM Songs");

            long maxId = 0;
            while (results.next()) {
                long songId = results.getLong("song_id");

                String songName = results.getString("song_name");
                String songArtist = results.getString("song_artist");
                String songAlbumArtist = results.getString("song_album_artist");
                String songAlbum = results.getString("song_album");
                String songGenre = results.getString("song_genre");
                int songScore = results.getInt("song_score");

                String songFilepath = results.getString("song_filepath");
                String songMimeType = results.getString("song_mime_type");
                int songLength = results.getInt("song_length");
                int songBitrate = results.getInt("song_bitrate");
                long songSize = results.getLong("song_size");
                boolean songHasThumbnail = results.getBoolean("song_has_thumbnail");

                Song song = new Song(songId, songName, songArtist, songAlbumArtist, songAlbum, songGenre, songLength, songScore, songFilepath, songMimeType, songBitrate, songSize, songHasThumbnail);
                linkStatistics(song, songStatisticPersistence);
                songs.add(song);

                if (songId > maxId) maxId = songId;
            }

            nextId = maxId + 1;

            statement.close();
            results.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void linkStatistics(Song song, SongStatisticPersistence persistence) {
        long songId = song.getSongId();

        for (SongStatistic statistic : persistence.getAllSongStatistics()) {
            if (Objects.equals(songId, statistic.getSongId())) {
                song.insertStatistic(statistic);
                statistic.setSong(song);
            }
        }
    }

    @Override
    public List<Song> getAllSongs() {
        return Collections.unmodifiableList(songs);
    }

    @Override
    public boolean insertSong(Song song) {
        if (!songs.contains(song)) {
            try (Connection connection = PersistenceSession.getConnection()) {
                final PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO Songs " +
                                "(song_id, song_name, song_artist, song_album_artist, song_album, song_genre, song_score, song_filepath, song_mime_type, song_length, song_bitrate, song_size, song_has_thumbnail) " +
                                "VALUES " +
                                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                );

                statement.setLong(1, song.getSongId());

                statement.setString(2, song.getSongName());
                statement.setString(3, song.getArtist());
                statement.setString(4, song.getAlbumArtist());
                statement.setString(5, song.getAlbum());
                statement.setString(6, song.getGenre());
                statement.setInt(7, song.getScore());

                statement.setString(8, song.getFilepath());
                statement.setString(9, song.getMimeType());
                statement.setInt(10, song.getLength());
                statement.setInt(11, song.getBitrate());
                statement.setLong(12, song.getSize());
                statement.setBoolean(13, song.hasThumbnail());

                statement.executeUpdate();
                statement.close();

                addStatisticRelations(connection, song.getStatisitics(), song.getSongId());

                songs.add(song);
                nextId++;
                return true;
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void insertSongs(List<Song> addSongs) {
        List<Song> uniqueSongs = new ArrayList<Song>();
        for (Song song : addSongs) {
            if (!songs.contains(song) && !uniqueSongs.contains(song))
                uniqueSongs.add(song);
        }

        try (Connection connection = PersistenceSession.getConnection()) {
            long maxId = nextId - 1;
            for (Song song : uniqueSongs) {
                final PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO Songs " +
                                "(song_id, song_name, song_artist, song_album_artist, song_album, song_genre, song_score, song_filepath, song_mime_type, song_length, song_bitrate, song_size, song_has_thumbnail) " +
                                "VALUES " +
                                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                );

                statement.setLong(1, song.getSongId());

                statement.setString(2, song.getSongName());
                statement.setString(3, song.getArtist());
                statement.setString(4, song.getAlbumArtist());
                statement.setString(5, song.getAlbum());
                statement.setString(6, song.getGenre());
                statement.setInt(7, song.getScore());

                statement.setString(8, song.getFilepath());
                statement.setString(9, song.getMimeType());
                statement.setInt(10, song.getLength());
                statement.setInt(11, song.getBitrate());
                statement.setLong(12, song.getSize());
                statement.setBoolean(13, song.hasThumbnail());

                statement.executeUpdate();
                statement.close();

                addStatisticRelations(connection, song.getStatisitics(), song.getSongId());

                songs.add(song);
                if (song.getSongId() > maxId) maxId = song.getSongId();
            }
            nextId = maxId + 1;
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void addStatisticRelations(Connection connection, List<SongStatistic> statistics, long songId) throws SQLException {
        for (SongStatistic statistic : statistics) {
            final PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO SongStatistics " +
                            "(song_id, stats_id, stats_value) " +
                            "VALUES " +
                            "(?, ?, ?)"
            );

            statement.setLong(1, songId);
            statement.setInt(2, statistic.getStatistic().ordinal());
            statement.setInt(3, statistic.getValue());

            statement.executeUpdate();
            statement.close();
        }
    }

    private void deleteStatisticRelations(Connection connection, long songId) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM SongStatistics " +
                        "WHERE song_id = ?"
        );

        statement.setLong(1, songId);

        statement.executeUpdate();
        statement.close();
    }

    @Override
    public boolean deleteSong(Song song) {
        try (Connection connection = PersistenceSession.getConnection()) {
            if (songs.contains(song)) {
                final PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM Songs " +
                                "WHERE song_id = ?"
                );

                statement.setLong(1, song.getSongId());

                statement.executeUpdate();
                statement.close();

                deleteStatisticRelations(connection, song.getSongId());

                return songs.remove(song);
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateSong(Song song) {
        try (Connection connection = PersistenceSession.getConnection()) {
            if (songs.contains(song)) {
                final PreparedStatement statement = connection.prepareStatement(
                        "UPDATE Songs " +
                                "SET " +
                                "song_name = ?, " +
                                "song_artist = ?, " +
                                "song_album_artist = ?, " +
                                "song_album = ?, " +
                                "song_genre = ?, " +
                                "song_score = ?, " +
                                "song_filepath = ?, " +
                                "song_mime_type = ?, " +
                                "song_length = ?, " +
                                "song_bitrate = ?, " +
                                "song_size = ?, " +
                                "song_has_thumbnail = ? " +
                                "WHERE song_id = ?"
                );

                statement.setString(1, song.getSongName());
                statement.setString(2, song.getArtist());
                statement.setString(3, song.getAlbumArtist());
                statement.setString(4, song.getAlbum());
                statement.setString(5, song.getGenre());
                statement.setInt(6, song.getScore());

                statement.setString(7, song.getFilepath());
                statement.setString(8, song.getMimeType());
                statement.setInt(9, song.getLength());
                statement.setInt(10, song.getBitrate());
                statement.setLong(11, song.getSize());
                statement.setBoolean(12, song.hasThumbnail());

                statement.setLong(13, song.getSongId());

                statement.executeUpdate();
                statement.close();

                //Do not know which statistics may have been updated in the song so reconnect all
                //Can make this a lot more efficient, since this is brute forcing all relations
                deleteStatisticRelations(connection, song.getSongId());
                addStatisticRelations(connection, song.getStatisitics(), song.getSongId());

                return true;
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long getNextId() {
        return nextId;
    }
}
