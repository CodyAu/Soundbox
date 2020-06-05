package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.PlaylistPersistence;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.SongPersistence;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.SongStatisticPersistence;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.hsqldb.PlaylistPersistenceHSQLDB;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.hsqldb.SongPersistenceHSQLDB;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.hsqldb.SongStatisticPersistenceHSQLDB;

public class Services {
    private static SongPersistence songPersistence = null;
    private static PlaylistPersistence playlistPersistence = null;
    private static SongStatisticPersistence songStatisticPersistence = null;

    public static synchronized SongPersistence getSongPersistence() {
        getSongStatisticPersistence();
        if (songPersistence == null) songPersistence = new SongPersistenceHSQLDB(getSongStatisticPersistence());
        return songPersistence;
    }

    public static synchronized PlaylistPersistence getPlaylistPersistence() {
        if (playlistPersistence == null) playlistPersistence = new PlaylistPersistenceHSQLDB(getSongPersistence());
        return playlistPersistence;
    }

    public static synchronized SongStatisticPersistence getSongStatisticPersistence(){
        if(songStatisticPersistence == null) {
            songStatisticPersistence = new SongStatisticPersistenceHSQLDB();
            getSongPersistence();
        }
        return songStatisticPersistence;
    }

    public static synchronized void clean() {
        songPersistence = null;
        playlistPersistence = null;
        songStatisticPersistence = null;
    }

}
