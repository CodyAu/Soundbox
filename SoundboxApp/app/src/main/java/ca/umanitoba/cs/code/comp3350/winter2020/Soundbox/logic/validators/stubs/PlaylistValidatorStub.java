package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.stubs;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.PlaylistValidator;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;

public class PlaylistValidatorStub implements PlaylistValidator {

    public boolean isPlaylistValid(Playlist playlist) {
        return true;
    }

}
