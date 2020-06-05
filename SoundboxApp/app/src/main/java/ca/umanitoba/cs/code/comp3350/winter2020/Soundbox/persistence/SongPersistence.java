package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence;

import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;

public interface SongPersistence {

    List<Song> getAllSongs();

    boolean insertSong(Song song);

    void insertSongs(List<Song> songs);

    boolean deleteSong(Song song);

    boolean updateSong(Song song);

    long getNextId();

}
