package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence;

import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;

public interface PlaylistPersistence {

    List<Playlist> getAllPlaylists();

    boolean insertPlaylist(Playlist playlist);

    boolean deletePlaylist(Playlist playlist);

    boolean updatePlaylist(Playlist playlist);

    long getNextId();

}
