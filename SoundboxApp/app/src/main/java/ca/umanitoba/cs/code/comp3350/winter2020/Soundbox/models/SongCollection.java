package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SongCollection {
    private String name;
    private String key;

    private List<Song> songs;

    private Comparator<Song> comparator;

    public SongCollection(String name, String key, List<Song> songs) {
        this.name = name;
        this.key = key;

        this.songs = songs != null ? songs : new ArrayList<Song>();
    }

    public SongCollection(String name, String key) {
        this.name = name;
        this.key = key;

        this.songs = new ArrayList<Song>();
    }

    public SongCollection(List<Song> songs) {
        this.name = "";
        this.key = "";

        this.songs = songs != null ? songs : new ArrayList<Song>();
    }

    public SongCollection() {
        this.name = "";
        this.key = "";

        this.songs = new ArrayList<Song>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public Song getFirstSongWithThumbnail() {
        for (Song song : songs) {
            if (song.hasThumbnail())
                return song;
        }
        return null;
    }

    /**
     * @return The song with the thumbnail to be displayed for this collection
     */
    public Song getCoverSong() {
        Song cover = null;
        for (Song song : songs) {
            if (song.hasThumbnail() && (cover == null || song.getSongId() < cover.getSongId()))
                cover = song;
        }
        return cover;
    }

    public List<Song> getSongs() {
        return Collections.unmodifiableList(songs); //do not allow change of the collection unless using the other methods in this class
    }

    public Song getSong(int index) {
        return songs.get(index);
    }

    public boolean insertSong(Song song) {
        if (comparator != null) {
            int index = Collections.binarySearch(songs, song, comparator);
            // Collections.binarySearch returns a negative index of the correct index.
            if (index < 0) index = ~index;  // Flip the bits to get the correct insertion index
            songs.add(index, song);
            return true;
        }
        return songs.add(song);
    }

    public boolean insertSongs(List<Song> songs) {
        boolean inserted = this.songs.addAll(songs);
        if (comparator != null) // instead of performing a binary search on each addition. Sort once after
            Collections.sort(this.songs, comparator);
        return inserted;
    }

    public boolean deleteSong(Song song) {
        return songs.remove(song);
    }

    public Song deleteSong(int index) {
        return songs.remove(index);
    }

    /**
     * Sorts songs by a given comparator
     *
     * @param comparator
     */
    public void setComparator(Comparator<Song> comparator) {
        this.comparator = comparator; // Allow null comparator for no comparison sort

        if (comparator != null)
            Collections.sort(songs, comparator);
    }

    public Comparator<Song> getComparator() {
        return comparator;
    }

    public void clearSongs() {
        songs.clear();
    }

    /**
     * @return The number of songs in this playlist
     */
    public int songsSize() {
        return songs.size();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SongCollection)
            return Objects.equals(key, ((SongCollection) other).key);
        return false;
    }

    public int compareToByName(SongCollection other) {
        if (name == null ^ other.name == null)
            return name == null ? -1 : 1;
        else if (name == null) return 0;

        return name.compareToIgnoreCase(other.name);
    }

    public int compareToByKey(SongCollection other) {
        if (key == null ^ other.key == null)
            return key == null ? -1 : 1;
        else if (key == null) return 0;

        return key.compareToIgnoreCase(other.key);
    }

}
