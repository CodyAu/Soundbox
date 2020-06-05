package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.SongStatisticController;

public class Song {
    private long songId;

    private String songName;
    private String artist;
    private String albumArtist;
    private String album;
    private String genre;
    private int score;

    private String filepath;
    private String mimeType;
    private int length;
    private int bitrate;
    private long size;
    private boolean hasThumbnail;

    private List<SongStatistic> statistics;

    public Song(long song_id, String songName, String artist, String albumArtist, String album, String genre, int length, int score, String filepath, String mimeType, int bitrate, long size, boolean hasThumbnail) {
        this.songId = song_id;
        this.songName = songName;
        setArtist(artist);
        setAlbumArtist(albumArtist);
        setAlbum(album);
        setGenre(genre);
        this.length = length;
        this.score = score;
        setFilepath(filepath);
        setMimeType(mimeType);
        this.bitrate = bitrate;
        this.size = size;
        this.hasThumbnail = hasThumbnail;
        this.statistics = new ArrayList<SongStatistic>();
    }

    public Song(long song_id, String songName, String artist, String albumArtist, String album, String genre, String length, int score, String filepath, String mimeType, String bitrate, long size, boolean hasThumbnail) {
        this.songId = song_id;
        this.songName = songName;
        setArtist(artist);
        setAlbumArtist(albumArtist);
        setAlbum(album);
        setGenre(genre);
        setLength(length);
        this.score = score;
        setFilepath(filepath);
        setMimeType(mimeType);
        setBitrate(bitrate);
        this.size = size;
        this.hasThumbnail = hasThumbnail;
        this.statistics = new ArrayList<SongStatistic>();
    }

    public void setSongId(long id) {
        songId = id;
    }

    public long getSongId() {
        return this.songId;
    }

    public void setSongName(String name) {
        songName = name;
    }

    public String getSongName() {
        return this.songName;
    }

    public void setArtist(String artist) {
        this.artist = (artist == null || artist.isEmpty()) ? "<unknown>" : artist;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = (albumArtist == null || albumArtist.isEmpty()) ? "<unknown>" : albumArtist;
    }

    public String getAlbumArtist() {
        return this.albumArtist;
    }

    public void setAlbum(String album) {
        this.album = (album == null || album.isEmpty()) ? "<unknown>" : album;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setGenre(String genre) {
        this.genre = (genre == null || genre.isEmpty()) ? "<unknown>" : genre;
    }

    public String getGenre() {
        return this.genre;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setLength(String length) {
        try {
            this.length = length != null ? Integer.parseInt(length) : -1;
        } catch (NumberFormatException e) {
            this.length = -1;
            e.printStackTrace();
        }
    }

    public int getLength() {
        return this.length;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath != null ? filepath : "";
    }

    public String getFilepath() {
        return filepath;
    }

    public String getDirectory() {
        if (filepath != null && !filepath.isEmpty()) {
            int idx = filepath.lastIndexOf("/");
            return idx >= 0 ? filepath.substring(0, idx + 1) : "/";
        }
        return null;
    }

    public String getDirectoryName() {
        String directory = getDirectory();
        if (directory != null && !directory.isEmpty()) {
            int idx = directory.lastIndexOf('/');
            if (directory.length() - 1 == idx) directory = directory.substring(0, idx);
            idx = directory.lastIndexOf('/');
            return idx >= 0 ? directory.substring(idx + 1) : directory;
        }
        return directory;
    }

    public String getFilename(boolean extension) {
        if (filepath != null && !filepath.isEmpty()) {
            int idx = filepath.lastIndexOf("/");
            String filename = idx >= 0 ? filepath.substring(idx + 1) : filepath;
            if (!extension) {
                idx = filename.indexOf('.');
                filename = idx >= 0 ? filename.substring(0, idx) : filename;
            }
            return filename;
        }
        return null;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = (mimeType == null || mimeType.isEmpty()) ? "<unknown>" : mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public void setBitrate(String bitrate) {
        try {
            this.bitrate = bitrate != null ? Integer.parseInt(bitrate) : -1;
        } catch (NumberFormatException e) {
            this.bitrate = -1;
            e.printStackTrace();
        }
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return this.size;
    }

    public void setHasThumbnail(boolean hasThumbnail) {
        this.hasThumbnail = hasThumbnail;
    }

    public boolean hasThumbnail() {
        return this.hasThumbnail;
    }

    public List<SongStatistic> getStatisitics() {
        return Collections.unmodifiableList(statistics);
    }

    public SongStatistic getStatisticByType(SongStatistic.Statistic stat) {
        for (int i = 0; i < statistics.size(); i++) {
            if (statistics.get(i).getStatistic().equals(stat)) {
                return statistics.get(i);
            }
        }
        SongStatistic newStatistic = new SongStatistic(songId, stat);
        (new SongStatisticController()).insertStatistic(newStatistic);
        statistics.add(newStatistic);

        return newStatistic;
    }

    public boolean insertStatistic(SongStatistic stat) {
        for (int i = 0; i < statistics.size(); i++) {
            if (statistics.get(i).getStatistic().equals(stat.getStatistic())) {
                return false;
            }
        }
        return statistics.add(stat);
    }

    public boolean deleteStatistic(SongStatistic statistic) {
        return statistics.remove(statistic);
    }

    public void clearStatistics() {
        statistics.clear();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Song)
            return Objects.equals(this.songId, ((Song) other).songId) ||
                    Objects.equals(this.filepath, ((Song) other).filepath);
        return false;
    }

    public int compareToById(Song other) {
        return Long.signum(songId - other.songId);
    }

    public int compareToByName(Song other) {
        if (songName == null ^ other.songName == null)
            return songName == null ? -1 : 1;
        else if (songName == null) return 0;

        return songName.compareToIgnoreCase(other.songName);
    }

    public int compareToByArtist(Song other) {
        if (artist == null ^ other.artist == null)
            return artist == null ? -1 : 1;
        else if (artist == null) return 0;

        return artist.compareToIgnoreCase(other.artist);
    }

    public int compareToByAlbum(Song other) {
        if (album == null ^ other.album == null)
            return album == null ? -1 : 1;
        else if (album == null) return 0;

        return album.compareToIgnoreCase(other.album);
    }

    public int compareToByGenre(Song other) {
        if (genre == null ^ other.genre == null)
            return genre == null ? -1 : 1;
        else if (genre == null) return 0;

        return genre.compareToIgnoreCase(other.genre);
    }

    @Override
    public String toString() {
        return "id: " + songId + "\n" +

                "name: " + this.songName + "\n" +
                "artist: " + this.artist + "\n" +
                "albumArtist: " + this.albumArtist + "\n" +
                "album: " + this.album + "\n" +
                "genre: " + this.genre + "\n" +
                "score: " + this.score + "\n" +

                "filepath: " + this.filepath + "\n" +
                "mimeType: " + this.mimeType + "\n" +
                "length: " + this.length + "\n" +
                "bitrate: " + this.bitrate + "\n" +
                "size: " + this.size + "\n" +
                "hasThumbnail: " + this.hasThumbnail;
    }

    public static final class Builder {
        private long songId;

        private String songName;
        private String artist;
        private String albumArtist;
        private String album;
        private String genre;
        private int score;

        private String filepath;
        private String mimeType;
        private int length;
        private int bitrate;
        private long size;
        private boolean hasThumbnail;

        public Builder() {

        }

        public Builder setSongId(long songId) {
            this.songId = songId;
            return this;
        }

        public Builder setSongName(String songName) {
            this.songName = songName;
            return this;
        }

        public Builder setArtist(String artist) {
            this.artist = (artist == null || artist.isEmpty()) ? "<unknown>" : artist;
            return this;
        }

        public void setAlbumArtist(String albumArtist) {
            this.albumArtist = (albumArtist == null || albumArtist.isEmpty()) ? "<unknown>" : albumArtist;
        }

        public Builder setAlbum(String album) {
            this.album = (album == null || album.isEmpty()) ? "<unknown>" : album;
            return this;
        }

        public Builder setGenre(String genre) {
            this.genre = (genre == null || genre.isEmpty()) ? "<unknown>" : genre;
            return this;
        }

        public Builder setScore(int score) {
            this.score = score;
            return this;
        }

        public Builder setFilepath(String filepath) {
            this.filepath = filepath;
            return this;
        }

        public Builder setLength(int length) {
            this.length = length;
            return this;
        }

        public Builder setLength(String length) {
            try {
                this.length = length != null ? Integer.parseInt(length) : -1;
            } catch (NumberFormatException e) {
                this.length = -1;
                e.printStackTrace();
            }
            return this;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = (mimeType == null || mimeType.isEmpty()) ? "<unknown>" : mimeType;
        }

        public Builder setBitrate(int bitrate) {
            this.bitrate = bitrate;
            return this;
        }

        public Builder setBitrate(String bitrate) {
            try {
                this.bitrate = bitrate != null ? Integer.parseInt(bitrate) : -1;
            } catch (NumberFormatException e) {
                this.bitrate = -1;
                e.printStackTrace();
            }
            return this;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public void setHasThumbnail(boolean hasThumbnail) {
            this.hasThumbnail = hasThumbnail;
        }

        public Song build() {
            return new Song(songId, songName, artist, albumArtist, album, genre, length, score, filepath, mimeType, bitrate, size, hasThumbnail);
        }

    }

}
