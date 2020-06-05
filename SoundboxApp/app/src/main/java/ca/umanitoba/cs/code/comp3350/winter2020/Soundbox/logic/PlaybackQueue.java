package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;

public class PlaybackQueue {
    private Playlist playlist;
    private List<Song> queue;
    private Song currentSong;
    private boolean allowDuplicates;
    private int index;

    public PlaybackQueue(List<Song> songs) {
        currentSong = null;
        playlist = null;
        allowDuplicates = true;
        queue = new ArrayList<Song>();
        addSongs(songs);
    }

    public PlaybackQueue(Playlist playlist) {
        currentSong = null;
        allowDuplicates = true;
        queue = new ArrayList<Song>();
        addSongs(playlist);
    }

    public PlaybackQueue() {
        currentSong = null;
        playlist = null;
        allowDuplicates = true;
        queue = new ArrayList<Song>();
        index = -1;
    }

    public void addSongs(PlaybackQueue queue) {
        boolean isEmpty = isEmpty();
        if (queue != null) {
            for (Song song : queue.getQueue())
                if (allowDuplicates || !this.queue.contains(song)) this.queue.add(song);
        }
        if (isEmpty) jumpFirst();
    }

    public void addSongs(Playlist playlist) {
        boolean isEmpty = isEmpty();
        if (playlist != null) {
            this.playlist = playlist;
            for (Song song : playlist.getSongs())
                if (allowDuplicates || !this.queue.contains(song)) this.queue.add(song);
        }
        if (isEmpty) jumpFirst();
    }

    public void addSongs(List<Song> songs) {
        boolean isEmpty = isEmpty();
        if (songs != null) {
            for (Song song : songs)
                if (allowDuplicates || !this.queue.contains(song)) this.queue.add(song);
        }
        if (isEmpty) jumpFirst();
    }

    public void setSongs(PlaybackQueue queue) {
        if (queue != null) {
            clear();
            this.queue.addAll(queue.getQueue());
        }
        jumpFirst();
    }

    public void setSongs(Playlist playlist) {
        if (playlist != null) {
            clear();
            this.playlist = playlist;
            this.queue.addAll(playlist.getSongs());
        }
        jumpFirst();
    }

    public void setSongs(List<Song> songs) {
        if (songs != null) {
            clear();
            queue.addAll(songs);
        }
        jumpFirst();
    }

    public boolean addSong(Song song) {
        if (allowDuplicates || !queue.contains(song)) {
            if (isEmpty()) {
                index = 0;
                currentSong = song;
            }

            return queue.add(song);
        }

        return false;
    }

    public boolean addSong(int idx, Song song) {
        if (allowDuplicates || !queue.contains(song)) {
            if (isEmpty()) {
                index = idx = 0;
                currentSong = song;
            }

            try {
                idx = Math.max(Math.min(idx, queueSize()), 0);
                if (idx <= index) index++; // Current song should not be changed

                queue.add(idx, song);
                return true;
            } catch (Exception e) {
                // Failed to insert
            }
        }

        return false;
    }

    public boolean playNext(Song song) {
        if (Objects.equals(currentSong, song)) return true;

        deleteSong(song);

        return addSong(index + 1, song);
    }

    public boolean playNext(int idx) {
        if (idx >= 0 && idx < queueSize()) {
            Song song = queue.get(idx);

            if (Objects.equals(currentSong, song)) return true;

            deleteSong(song);

            return addSong(index + 1, song);
        }
        return false;
    }

    public void playNext(List<Song> songs) {
        int endIdx = 0;
        if (isEmpty() && songs.size() > 0) { // If queue is empty add one song that all other songs will be played after
            addSong(songs.get(0));
            endIdx = 1;
        }
        for (int i = songs.size() - 1; i >= endIdx; i--) {
            playNext(songs.get(i));
        }
    }

    /**
     * Note this deletes all instances of a song in the queue
     *
     * @param song
     * @return If all instances of the song were deleted
     */
    public boolean deleteSong(Song song) {
        boolean removed = false;
        int idx;
        while ((idx = queue.indexOf(song)) >= 0) { // if duplicates are allowed, there could be multiple instances of a song, remove them all
            if (idx < index) {
                removed = queue.remove(idx) != null;
                if (removed) index--;
            } else removed = queue.remove(idx) != null;
        }

        if (isEmpty()) clear();
        else currentSong = queue.get(index);

        return removed;
    }

    public boolean deleteSong(int idx) {
        boolean removed = false;
        if (idx >= 0 && idx < queueSize()) {
            if (idx < index) {
                removed = queue.remove(idx) != null;
                if (removed) index--;
            } else removed = queue.remove(idx) != null;

            if (isEmpty()) clear();
            else currentSong = queue.get(index);
        }

        return removed;
    }

    public boolean swapSong(int fromPosition, int toPosition) {
        boolean selected = index == fromPosition;
        boolean swapped = false;
        if (fromPosition >= 0 && fromPosition < queueSize() && toPosition >= 0 && toPosition < queueSize()) {
            Song prev = queue.remove(fromPosition);
            if (prev != null) {
                queue.add(toPosition, prev);
                if (selected) {
                    index = toPosition;
                } else if (index > fromPosition && index <= toPosition) {
                    index--;
                } else if (index < fromPosition && index >= toPosition) {
                    index++;
                }
                swapped = true;
            }
        }

        return swapped;
    }

    public void setAllowDuplicates(boolean allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
    }

    public void shuffleQueue() {
        Collections.shuffle(queue);
        jumpSong(currentSong);
    }

    public Song jumpFirst() {
        return jumpIndex(0);
    }

    public Song jumpLast() {
        return jumpIndex(queue.size() - 1);
    }

    public Song jumpPrevious() {
        if (isEmpty()) {
            return null;
        } else {
            index = (index + queue.size() - 1) % queue.size();
            currentSong = queue.get(index);
            return currentSong;
        }
    }

    public Song jumpNext() {
        if (isEmpty()) {
            return null;
        } else {
            index = (index + 1) % queue.size();
            currentSong = queue.get(index);
            return currentSong;
        }
    }

    public Song jumpIndex(int idx) {
        if (idx < 0 || idx >= queue.size()) {
            return null;
        } else {
            index = idx;
            currentSong = queue.get(index);
            return currentSong;
        }
    }

    public Song jumpSong(Song song) {
        int idx = queue.indexOf(song);

        if (idx >= 0)
            jumpIndex(idx);
        else
            return null;

        return currentSong;
    }

    public Song peekFirst() {
        return peekIndex(0);
    }

    public Song peekLast() {
        return peekIndex(queue.size() - 1);
    }

    public Song peekPrevious() {
        if (isEmpty()) {
            return null;
        } else {
            int idx = (index + queue.size() - 1) % queue.size();
            Song song = queue.get(idx);
            return song;
        }
    }

    public Song peekNext() {
        if (isEmpty()) {
            return null;
        } else {
            int idx = (index + 1) % queue.size();
            Song song = queue.get(idx);
            return song;
        }
    }

    public Song peekIndex(int idx) {
        if (idx < 0 || idx >= queue.size()) {
            return null;
        } else {
            Song song = queue.get(idx);
            return song;
        }
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public List<Song> getQueue() {
        return Collections.unmodifiableList(queue);
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public boolean getAllowDuplicates() {
        return allowDuplicates;
    }

    public int getIndex() {
        return index;
    }

    public int queueSize() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void clear() {
        playlist = null;
        queue.clear();
        currentSong = null;
        index = -1;
    }

}
