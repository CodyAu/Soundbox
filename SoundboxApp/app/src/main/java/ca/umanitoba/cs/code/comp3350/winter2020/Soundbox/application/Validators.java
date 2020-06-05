package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.PlaylistValidator;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.SongValidator;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.stubs.PlaylistValidatorStub;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.stubs.SongValidatorStub;

public class Validators {
    private static SongValidator songValidator = null;
    private static PlaylistValidator playlistValidator = null;

    public static synchronized SongValidator getSongValidator() {
        if (songValidator == null) songValidator = new SongValidatorStub();
        return songValidator;
    }

    public static synchronized PlaylistValidator getPlaylistValidator() {
        if (playlistValidator == null) playlistValidator = new PlaylistValidatorStub();
        return playlistValidator;
    }

    public static synchronized void clean() {
        songValidator = null;
        playlistValidator = null;
    }

}
