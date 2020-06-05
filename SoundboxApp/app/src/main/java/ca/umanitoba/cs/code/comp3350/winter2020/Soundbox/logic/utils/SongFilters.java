package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongCollection;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongStatistic;

public final class SongFilters {
    public enum SortOrder {
        DEFAULT,
        NAME,
        ARTIST,
        ALBUM,
        GENRE;

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

    public static Song getSongById(final long songId, final List<Song> songs) {
        for (Song song : songs)
            if (Objects.equals(song.getSongId(), songId))
                return song;

        return null;
    }

    public static List<Song> getSongsByName(final String name, final List<Song> songs) {
        List<Song> filterSongs = new ArrayList<Song>();
        for (Song song : songs)
            if (Objects.equals(song.getSongName(), name))
                filterSongs.add(song);

        return filterSongs;
    }

    public static List<Song> getSongsByArtist(final String artist, final List<Song> songs) {
        List<Song> filterSongs = new ArrayList<Song>();
        for (Song song : songs)
            if (Objects.equals(song.getArtist(), artist))
                filterSongs.add(song);

        return filterSongs;
    }

    public static List<Song> getSongsByAlbum(final String album, final List<Song> songs) {
        List<Song> filterSongs = new ArrayList<Song>();
        for (Song song : songs)
            if (Objects.equals(song.getAlbum(), album))
                filterSongs.add(song);

        return filterSongs;
    }

    public static List<Song> getSongsByGenre(final String genre, final List<Song> songs) {
        List<Song> filterSongs = new ArrayList<Song>();
        for (Song song : songs)
            if (Objects.equals(song.getGenre(), genre))
                filterSongs.add(song);

        return filterSongs;
    }

    /**
     * @param name
     * @return Sorted list of names
     */
    public static List<Song> getSongsByNameLike(final String name, final List<Song> songs) {
        final List<String> names = new ArrayList<String>(songs.size());
        for (int i = 0; i < songs.size(); i++) {
            names.add(i, songs.get(i).getSongName());
        }

        final int[] levs = Calculate.levenshteins(name, names, true);

        List<Song> sortedList = new ArrayList<Song>(songs);
        Collections.sort(sortedList, new Comparator<Song>() {
            @Override
            public int compare(Song left, Song right) {
                return levs[songs.indexOf(left)] - levs[songs.indexOf(right)];
            }
        });

        return sortedList;
    }

    /**
     * @param artist
     * @return Sorted list of artists.
     */
    public static List<Song> getSongsByArtistLike(final String artist, final List<Song> songs) {
        final List<String> artists = new ArrayList<String>(songs.size());
        for (int i = 0; i < songs.size(); i++) {
            artists.add(i, songs.get(i).getArtist());
        }

        final int[] levs = Calculate.levenshteins(artist, artists, true);

        List<Song> sortedList = new ArrayList<Song>(songs);
        Collections.sort(sortedList, new Comparator<Song>() {
            @Override
            public int compare(Song left, Song right) {
                return levs[songs.indexOf(left)] - levs[songs.indexOf(right)];
            }
        });

        return sortedList;
    }

    /**
     * @param album
     * @return Sorted list of albums.
     */
    public static List<Song> getSongsByAlbumLike(final String album, final List<Song> songs) {
        final List<String> albums = new ArrayList<String>(songs.size());
        for (int i = 0; i < songs.size(); i++) {
            albums.add(i, songs.get(i).getAlbum());
        }

        final int[] levs = Calculate.levenshteins(album, albums, true);

        List<Song> sortedList = new ArrayList<Song>(songs);
        Collections.sort(sortedList, new Comparator<Song>() {
            @Override
            public int compare(Song left, Song right) {
                return levs[songs.indexOf(left)] - levs[songs.indexOf(right)];
            }
        });

        return sortedList;
    }

    /**
     * @param genre
     * @return Sorted list of genres.
     */
    public static List<Song> getSongsByGenreLike(final String genre, final List<Song> songs) {
        final List<String> genres = new ArrayList<String>(songs.size());
        for (int i = 0; i < songs.size(); i++) {
            genres.add(i, songs.get(i).getGenre());
        }

        final int[] levs = Calculate.levenshteins(genre, genres, true);

        List<Song> sortedList = new ArrayList<Song>(songs);
        Collections.sort(sortedList, new Comparator<Song>() {
            @Override
            public int compare(Song left, Song right) {
                return levs[songs.indexOf(left)] - levs[songs.indexOf(right)];
            }
        });

        return sortedList;
    }

    public static List<Song> getSongsLike(final String search, final List<Song> songs) {
        final List<String> closest = new ArrayList<String>(songs.size());

        for (int i = 0; i < songs.size(); i++) {
            String min = Calculate.nearest(search, Arrays.asList(
                    songs.get(i).getSongName(),
                    songs.get(i).getArtist(),
                    songs.get(i).getAlbum(),
                    songs.get(i).getGenre()
            ), true);
            closest.add(i, min);
        }

        final int[] levs = Calculate.levenshteins(search, closest, true);

        List<Song> sortedList = new ArrayList<Song>(songs);
        Collections.sort(sortedList, new Comparator<Song>() {
            @Override
            public int compare(Song left, Song right) {
                return levs[songs.indexOf(left)] - levs[songs.indexOf(right)];
            }
        });

        return sortedList;
    }

    public static List<SongCollection> groupSongsByArtist(List<Song> songs) {
        Map<String, SongCollection> artists = new HashMap<String, SongCollection>();

        for (Song song : songs) {
            SongCollection artistsSongs = artists.get(song.getArtist());
            if (artistsSongs == null) { // getOrDefault only in api 24 >
                artistsSongs = new SongCollection(song.getArtist(), song.getArtist());
                artists.put(song.getArtist(), artistsSongs);
            }
            artistsSongs.insertSong(song);
        }

        return new ArrayList<SongCollection>(artists.values());
    }

    public static List<SongCollection> groupSongsByAlbum(List<Song> songs) {
        Map<String, SongCollection> albums = new HashMap<String, SongCollection>();

        for (Song song : songs) {
            SongCollection albumsSongs = albums.get(song.getAlbum());
            if (albumsSongs == null) { // getOrDefault only in api 24 >
                albumsSongs = new SongCollection(song.getAlbum(), song.getAlbum());
                albums.put(song.getAlbum(), albumsSongs);
            }
            albumsSongs.insertSong(song);
        }

        return new ArrayList<SongCollection>(albums.values());
    }

    public static List<SongCollection> groupSongsByGenre(List<Song> songs) {
        Map<String, SongCollection> genres = new HashMap<String, SongCollection>();

        for (Song song : songs) {
            SongCollection genresSongs = genres.get(song.getGenre());
            if (genresSongs == null) { // getOrDefault only in api 24 >
                genresSongs = new SongCollection(song.getGenre(), song.getGenre());
                genres.put(song.getGenre(), genresSongs);
            }
            genresSongs.insertSong(song);
        }

        return new ArrayList<SongCollection>(genres.values());
    }

    public static List<SongCollection> groupSongsByFolder(List<Song> songs) {
        Map<String, SongCollection> folders = new HashMap<String, SongCollection>();

        for (Song song : songs) {
            SongCollection foldersSongs = folders.get(song.getDirectory());
            if (foldersSongs == null) { // getOrDefault only in api 24 >
                foldersSongs = new SongCollection(song.getDirectoryName(), song.getDirectory());
                folders.put(song.getDirectory(), foldersSongs);
            }
            foldersSongs.insertSong(song);
        }

        return new ArrayList<SongCollection>(folders.values());
    }

    public static List<SongCollection> groupSongsByFavorites(Context context, List<Song> songs){
        Map<String, SongCollection> favorites = new HashMap<String, SongCollection>();

        for (Song song : songs) {
            SongStatistic likes = song.getStatisticByType(SongStatistic.Statistic.LIKES);
            SongStatistic dislikes = song.getStatisticByType(SongStatistic.Statistic.DISLIKES);

            SongCollection favoritesSongsLikes = favorites.get(likes.getStatistic().toString());
            if (favoritesSongsLikes == null) { // getOrDefault only in api 24 >
                favoritesSongsLikes = new SongCollection(context.getString(R.string.likes), likes.getStatistic().toString());
                favorites.put(likes.getStatistic().toString(), favoritesSongsLikes);
            }
            if (likes.getValue() > 0) favoritesSongsLikes.insertSong(song);

            SongCollection favoritesSongsDislikes = favorites.get(dislikes.getStatistic().toString());
            if (favoritesSongsDislikes == null) { // getOrDefault only in api 24 >
                favoritesSongsDislikes = new SongCollection(context.getString(R.string.dislikes), dislikes.getStatistic().toString());
                favorites.put(dislikes.getStatistic().toString(), favoritesSongsDislikes);
            }
            if (dislikes.getValue() > 0) favoritesSongsDislikes.insertSong(song);
        }

        return new ArrayList<SongCollection>(favorites.values());
    }

}
