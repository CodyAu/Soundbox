package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables;

public class PlaybackShuffleObservable extends SoundboxObservable<Boolean> {
    @Override
    public void setValue(Boolean shuffle) {
        setChanged();
        notifyObservers();
    }

    @Override
    public Boolean getValue() {
        return true;
    }

}
