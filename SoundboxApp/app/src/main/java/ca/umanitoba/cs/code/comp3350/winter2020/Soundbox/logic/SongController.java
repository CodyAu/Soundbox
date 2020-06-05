package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic;

import java.util.ArrayList;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.Services;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.Validators;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils.SongFilters;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.SongValidator;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.SongPersistence;

public class SongController {
    private SongPersistence songPersistence;
    private SongValidator songValidator;

    public SongController(SongPersistence persistence) {
        songPersistence = persistence;
        songValidator = Validators.getSongValidator();
    }

    public SongController() {
        songPersistence = Services.getSongPersistence();
        songValidator = Validators.getSongValidator();
    }

    public List<Song> getAllSongs() {
        return songPersistence.getAllSongs();
    }

    public Song getSongById(long songId) {
        return SongFilters.getSongById(songId, songPersistence.getAllSongs());
    }

    public List<Song> getSongsByName(String name) {
        return SongFilters.getSongsByName(name, songPersistence.getAllSongs());
    }

    public List<Song> getSongsByArtist(String artist) {
        return SongFilters.getSongsByArtist(artist, songPersistence.getAllSongs());
    }

    public List<Song> getSongsByAlbum(String album) {
        return SongFilters.getSongsByAlbum(album, songPersistence.getAllSongs());
    }

    public List<Song> getSongsByGenre(String genre) {
        return SongFilters.getSongsByGenre(genre, songPersistence.getAllSongs());
    }

    /**
     * @param name
     * @return Sorted list of names
     */
    public List<Song> getSongsByNameLike(String name) {
        return SongFilters.getSongsByNameLike(name, songPersistence.getAllSongs());
    }

    /**
     * @param artist
     * @return Sorted list of artists.
     */
    public List<Song> getSongsByArtistLike(String artist) {
        return SongFilters.getSongsByArtistLike(artist, songPersistence.getAllSongs());
    }

    /**
     * @param album
     * @return Sorted list of albums.
     */
    public List<Song> getSongsByAlbumLike(String album) {
        return SongFilters.getSongsByAlbumLike(album, songPersistence.getAllSongs());
    }

    /**
     * @param genre
     * @return Sorted list of genres.
     */
    public List<Song> getSongsByGenreLike(String genre) {
        return SongFilters.getSongsByGenreLike(genre, songPersistence.getAllSongs());
    }

    public List<Song> getSongsLike(String search) {
        return SongFilters.getSongsLike(search, songPersistence.getAllSongs());
    }

    public boolean insertSong(Song song) {
        return songValidator.isSongValid(song) && songPersistence.insertSong(song);
    }

    public void insertSongs(List<Song> songs) {
        List<Song> validSongs = new ArrayList<Song>();
        for (Song song : songs){
            if(songValidator.isSongValid(song))
                validSongs.add(song);
        }
        songPersistence.insertSongs(validSongs);
    }

    public boolean updateSong(Song song) {
        return songValidator.isSongValid(song) && songPersistence.updateSong(song);
    }

    public boolean deleteSong(Song song) {
        return songValidator.isSongValid(song) && songPersistence.deleteSong(song);
    }

    public long getNextId(){
        return songPersistence.getNextId();
    }

}
