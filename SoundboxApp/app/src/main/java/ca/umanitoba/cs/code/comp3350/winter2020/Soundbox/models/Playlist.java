package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models;

import java.util.List;
import java.util.Objects;

public class Playlist extends SongCollection {
    private long playlistId;
    private int thumbnail;

    public Playlist(long playlistId, String name, int thumbnail, List<Song> songs) {
        super(name, name, songs);

        this.playlistId = playlistId;
        this.thumbnail = thumbnail;
    }

    public Playlist(long playlistId, String name, int thumbnail) {
        super(name, name);

        this.playlistId = playlistId;
        this.thumbnail = thumbnail;
    }

    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }

    public long getPlaylistId() {
        return playlistId;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Playlist)
            return Objects.equals(playlistId, ((Playlist) other).playlistId);
        return false;
    }

    public int compareToById(Playlist other) {
        return Long.signum(playlistId - other.playlistId);
    }

}
