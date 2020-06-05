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
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.PlaylistPersistence;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.SongPersistence;

public class PlaylistPersistenceHSQLDB implements PlaylistPersistence {
    private SongPersistence songPersistence;
    private List<Playlist> playlists;
    private long nextId;

    public PlaylistPersistenceHSQLDB(SongPersistence persistence) {
        this.songPersistence = persistence;
        playlists = new ArrayList<Playlist>();

        loadSavedPlaylists();
    }

    private void loadSavedPlaylists() {
        try (Connection connection = PersistenceSession.getConnection()) {
            final Statement statement = connection.createStatement();
            final ResultSet results = statement.executeQuery("SELECT * FROM Playlists");

            long maxId = 0;
            while (results.next()) {
                long playlistId = results.getLong("playlist_id");

                String playlistName = results.getString("playlist_name");
                int playlistThumbnail = results.getInt("playlist_thumbnail");

                Playlist playlist = new Playlist(playlistId, playlistName, playlistThumbnail);
                linkSongs(playlist, songPersistence);
                playlists.add(playlist);

                if (playlistId > maxId) maxId = playlistId;
            }

            nextId = maxId + 1;

            statement.close();
            results.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void linkSongs(Playlist playlist, SongPersistence persistence) {
        long playlistId = playlist.getPlaylistId();
        try (Connection connection = PersistenceSession.getConnection()) {
            final PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM PlaylistSongs " +
                            "WHERE playlist_id = ?"
            );

            statement.setLong(1, playlistId);

            final ResultSet results = statement.executeQuery();
            while (results.next()) {
                long songId = results.getLong("song_id");

                for (Song song : persistence.getAllSongs()) {
                    if (song.getSongId() == songId) {
                        playlist.insertSong(song);
                        break;
                    }
                }
            }

            statement.close();
            results.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Playlist> getAllPlaylists() {
        return Collections.unmodifiableList(playlists);
    }

    @Override
    public boolean insertPlaylist(Playlist playlist) {
        if (!playlists.contains(playlist)) {
            try (Connection connection = PersistenceSession.getConnection()) {
                final PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO Playlists " +
                                "(playlist_id, playlist_name, playlist_thumbnail) " +
                                "VALUES " +
                                "(?, ?, ?)"
                );

                statement.setLong(1, playlist.getPlaylistId());
                statement.setString(2, playlist.getName());
                statement.setInt(3, playlist.getThumbnail());

                statement.executeUpdate();
                statement.close();

                addSongRelations(connection, playlist.getSongs(), playlist.getPlaylistId());

                playlists.add(playlist);
                nextId++;
                return true;
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void addSongRelations(Connection connection, List<Song> songs, long playlistId) throws SQLException {
        for (Song song : songs) {
            final PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO PlaylistSongs " +
                            "(playlist_id, song_id) " +
                            "VALUES " +
                            "(?, ?)"
            );

            statement.setLong(1, playlistId);
            statement.setLong(2, song.getSongId());

            statement.executeUpdate();
            statement.close();
        }
    }

    private void deleteSongRelations(Connection connection, long playlistId) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM PlaylistSongs " +
                        "WHERE playlist_id = ?"
        );

        statement.setLong(1, playlistId);

        statement.executeUpdate();
        statement.close();
    }

    @Override
    public boolean deletePlaylist(Playlist playlist) {
        try (Connection connection = PersistenceSession.getConnection()) {
            if (playlists.contains(playlist)) {
                final PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM Playlists " +
                                "WHERE playlist_id = ?"
                );

                statement.setLong(1, playlist.getPlaylistId());

                statement.executeUpdate();
                statement.close();

                deleteSongRelations(connection, playlist.getPlaylistId());

                return playlists.remove(playlist);
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updatePlaylist(Playlist playlist) {
        try (Connection connection = PersistenceSession.getConnection()) {
            if (playlists.contains(playlist)) {
                final PreparedStatement statement = connection.prepareStatement(
                        "UPDATE Playlists " +
                                "SET " +
                                "playlist_name = ?, " +
                                "playlist_thumbnail = ? " +
                                "WHERE playlist_id = ?"
                );

                statement.setString(1, playlist.getName());
                statement.setInt(2, playlist.getThumbnail());

                statement.setLong(3, playlist.getPlaylistId());

                statement.executeUpdate();
                statement.close();

                //Do not know which songs may have been updated in the playlist so reconnect all
                //Can make this a lot more efficient, since this is brute forcing all relations
                deleteSongRelations(connection, playlist.getPlaylistId());
                addSongRelations(connection, playlist.getSongs(), playlist.getPlaylistId());

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
