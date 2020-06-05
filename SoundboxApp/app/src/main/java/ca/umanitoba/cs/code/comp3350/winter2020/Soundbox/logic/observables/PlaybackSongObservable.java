package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;

public class PlaybackSongObservable extends SoundboxObservable<Song> {
    private Song song;

    @Override
    public void setValue(Song song) {
        this.song = song;
        setChanged();
        notifyObservers();
    }

    @Override
    public Song getValue() {
        return song;
    }

}
