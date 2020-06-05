package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.stubs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.SongPersistence;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;

public class SongPersistenceStub implements SongPersistence {
    private List<Song> songs;

    public SongPersistenceStub() {
        songs = new ArrayList<Song>();
    }

    @Override
    public List<Song> getAllSongs() {
        return Collections.unmodifiableList(songs);
    }

    @Override
    public boolean insertSong(Song song) {
        if (!songs.contains(song))
            return songs.add(song);
        return false;
    }

    @Override
    public void insertSongs(List<Song> addSongs) {
        for (Song song : addSongs) {
            if (!songs.contains(song))
                songs.add(song);
        }
    }

    @Override
    public boolean deleteSong(Song song) {
        return songs.remove(song);
    }

    @Override
    public boolean updateSong(Song song) {
        int index = songs.indexOf(song);
        if (index >= 0) {
            songs.set(index, song);
            return true;
        }
        return false;
    }

    @Override
    public long getNextId() {
        return songs.size() + 1;
    }
}
