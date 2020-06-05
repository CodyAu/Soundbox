package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation;

public interface Playback {
    void play();

    void pause();

    void toggle();

    void playNext();

    void playPrevious();

    void shuffle();

    void loop();

}
