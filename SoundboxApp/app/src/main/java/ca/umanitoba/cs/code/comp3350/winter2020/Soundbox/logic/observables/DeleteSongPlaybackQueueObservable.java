package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;

public class DeleteSongPlaybackQueueObservable extends SoundboxObservable<Song> {
    private Song song;

    @Override
    public void setValue(Song song) {
        this.song = song;
        setChanged();
        notifyObservers();
    }

    /**
     *
     * @return Returns the song that was deleted. If more than one song was added returns null
     */
    @Override
    public Song getValue() {
        return song;
    }

}
