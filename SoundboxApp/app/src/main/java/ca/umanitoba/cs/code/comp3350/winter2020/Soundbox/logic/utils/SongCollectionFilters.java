package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongCollection;

public final class SongCollectionFilters {
    public enum SortOrder {
        DEFAULT,
        NAME;

        /**
         * @param ord
         * @return returns the Statistic with id of ord
         */
        public static SortOrder fromOrdinal(int ord) {
            if (ord >= values().length || ord < 0)
                return null;
            return values()[ord];
        }
    }

    public static Playlist getPlaylistById(final long playlistId, final List<Playlist> playlists) {
        for (Playlist playlist : playlists)
            if (playlist.getPlaylistId() == playlistId)
                return playlist;

        return null;
    }

    public static <T extends SongCollection> List<T> getSongCollectionsByName(final String name, final List<T> songCollections) {
        List<T> filteredSongCollections = new ArrayList<T>();
        for (T songCollection : songCollections)
            if (Objects.equals(songCollection.getName(), name))
                filteredSongCollections.add(songCollection);

        return filteredSongCollections;
    }

    public static <T extends SongCollection> List<T> getSongCollectionsByKey(final String key, final List<T> songCollections) {
        List<T> filteredSongCollections = new ArrayList<T>();
        for (T songCollection : songCollections)
            if (Objects.equals(songCollection.getKey(), key))
                filteredSongCollections.add(songCollection);

        return filteredSongCollections;
    }

    /**
     * @param name
     * @return Sorted list of names
     */
    public static <T extends SongCollection> List<T> getSongCollectionsByNameLike(final String name, final List<T> songCollections) {
        final List<String> names = new ArrayList<String>(songCollections.size());
        for (int i = 0; i < songCollections.size(); i++) {
            names.add(i, songCollections.get(i).getName());
        }

        final int[] levs = Calculate.levenshteins(name, names, true);

        List<T> sortedList = new ArrayList<T>(songCollections);
        Collections.sort(sortedList, new Comparator<T>() {
            @Override
            public int compare(SongCollection left, SongCollection right) {
                return levs[songCollections.indexOf(left)] - levs[songCollections.indexOf(right)];
            }
        });

        return sortedList;
    }

    /**
     * @param key
     * @return Sorted list of keys
     */
    public static <T extends SongCollection> List<T> getSongCollectionsByKeyLike(final String key, final List<T> songCollections) {
        final List<String> keys = new ArrayList<String>(songCollections.size());
        for (int i = 0; i < songCollections.size(); i++) {
            keys.add(i, songCollections.get(i).getKey());
        }

        final int[] levs = Calculate.levenshteins(key, keys, true);

        List<T> sortedList = new ArrayList<T>(songCollections);
        Collections.sort(sortedList, new Comparator<T>() {
            @Override
            public int compare(T left, T right) {
                return levs[songCollections.indexOf(left)] - levs[songCollections.indexOf(right)];
            }
        });

        return sortedList;
    }

}
