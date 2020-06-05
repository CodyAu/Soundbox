package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackController;

public class PlaybackStateObservable extends SoundboxObservable<PlaybackController.PlaybackState> {
    private PlaybackController.PlaybackState state;

    @Override
    public void setValue(PlaybackController.PlaybackState state) {
        this.state = state;
        setChanged();
        notifyObservers();
    }

    @Override
    public PlaybackController.PlaybackState getValue() {
        return state;
    }

}
