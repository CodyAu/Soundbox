package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.basic;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.SongValidator;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;

public class SongValidatorBasic implements SongValidator {

    public boolean isSongValid(Song song) {
        return song.getSongId() > 0 &&
                !song.getFilepath().isEmpty();
    }

}
