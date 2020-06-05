package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables;

public interface SoundboxObserver<U> {
    void update(SoundboxObservable<? extends U> observer, U arg);
}
