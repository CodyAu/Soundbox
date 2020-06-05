package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.stubs;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.SongValidator;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;

public class SongValidatorStub implements SongValidator {

    public boolean isSongValid(Song song) {
        return true;
    }

}
