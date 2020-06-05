package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models;

import java.util.Objects;

public class SongStatistic {
    public enum Statistic {
        PLAYS,
        LISTEN_TIME,
        LIKES,
        DISLIKES;

        /**
         *
         * @param ord
         * @return returns the Statistic with id of ord
         */
        public static Statistic fromOrdinal(int ord){
            if(ord >= values().length || ord < 0)
                return null;
            return values()[ord];
        }
    }

    private long songId;
    private Statistic statistic;
    private int value;

    private Song song;

    public SongStatistic(long songId, Statistic statistic, int value) {
        this.songId = songId;
        this.statistic = statistic;
        this.value = value;

        this.song = null;
    }

    public SongStatistic(long songId, Statistic statistic) {
        this.songId = songId;
        this.statistic = statistic;
        this.value = 0;

        this.song = null;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public void setStatistic(Statistic statistic) {
        this.statistic = statistic;
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public Song getSong() {
        return song;
    }

    public void incrementValue() {
        this.value++;
    }

    public int getValue() {
        return value;
    }

    public boolean equals(Object other){
        if (other instanceof SongStatistic)
            return Objects.equals(this.songId, ((SongStatistic) other).songId) &&
                    Objects.equals(this.statistic, ((SongStatistic) other).statistic);
        return false;
    }

}
