package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models;

import java.util.ArrayList;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.SongController;

public class Recommendation {

    private List<Song> songs;
    public SongStatistic songStatistic;
    public SongStatistic.Statistic statistic;
    public SongController songController;


    public Recommendation(List<Song> songs) {
        this.songs = songs;
    }

    public Recommendation() {
        this.songs = new ArrayList<Song>();
        songController = new SongController();
    }

    public List<Song> getRecs(){
        List<Song> recs = songController.getAllSongs();
        return recs;
    }

    public boolean insertSong(Song song) {
        return songs.add(song);
    }

    public boolean deleteSong(Song song) {
        return songs.remove(song);
    }

    public void clearSongs() {
        songs.clear();
    }

    public int recSize() {
        return songs.size();
    }

}
