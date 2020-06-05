package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.basic;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.validators.PlaylistValidator;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;

public class PlaylistValidatorBasic implements PlaylistValidator {

    public boolean isPlaylistValid(Playlist playlist) {
        return playlist.getPlaylistId() > 0;
    }

}
