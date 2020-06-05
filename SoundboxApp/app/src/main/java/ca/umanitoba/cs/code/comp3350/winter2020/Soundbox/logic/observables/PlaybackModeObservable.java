package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackController;

public class PlaybackModeObservable extends SoundboxObservable<PlaybackController.PlaybackMode> {
    private PlaybackController.PlaybackMode mode;

    @Override
    public void setValue(PlaybackController.PlaybackMode mode) {
        this.mode = mode;
        setChanged();
        notifyObservers();
    }

    @Override
    public PlaybackController.PlaybackMode getValue() {
        return mode;
    }

}
