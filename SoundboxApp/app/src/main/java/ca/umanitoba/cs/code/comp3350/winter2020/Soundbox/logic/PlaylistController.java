package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic;

import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.Services;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.Validators;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils.SongCollectionFilters;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.PlaylistValidator;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.PlaylistPersistence;

public class PlaylistController {
    private PlaylistPersistence playlistPersistence;
    private PlaylistValidator playlistValidator;

    public PlaylistController(PlaylistPersistence persistence) {
        playlistPersistence = persistence;
        playlistValidator = Validators.getPlaylistValidator();
    }

    public PlaylistController() {
        playlistPersistence = Services.getPlaylistPersistence();
        playlistValidator = Validators.getPlaylistValidator();
    }

    public List<Playlist> getAllPlaylists() {
        return playlistPersistence.getAllPlaylists();
    }

    public Playlist getPlaylistById(long playlistId) {
        return SongCollectionFilters.getPlaylistById(playlistId, playlistPersistence.getAllPlaylists());
    }

    public List<Playlist> getPlaylistsByName(String name) {
        return SongCollectionFilters.getSongCollectionsByName(name, playlistPersistence.getAllPlaylists());
    }

    /**
     * @param name
     * @return Sorted list of names
     */
    public List<Playlist> getPlaylistsByNameLike(String name) {
        return SongCollectionFilters.getSongCollectionsByNameLike(name, playlistPersistence.getAllPlaylists());
    }

    public boolean insertPlaylist(Playlist playlist) {
        return playlistValidator.isPlaylistValid(playlist) && playlistPersistence.insertPlaylist(playlist);
    }

    public boolean updatePlaylist(Playlist playlist) {
        return playlistValidator.isPlaylistValid(playlist) && playlistPersistence.updatePlaylist(playlist);
    }

    public boolean deletePlaylist(Playlist playlist) {
        return playlistValidator.isPlaylistValid(playlist) && playlistPersistence.deletePlaylist(playlist);
    }

    public long getNextId() {
        return playlistPersistence.getNextId();
    }

}
