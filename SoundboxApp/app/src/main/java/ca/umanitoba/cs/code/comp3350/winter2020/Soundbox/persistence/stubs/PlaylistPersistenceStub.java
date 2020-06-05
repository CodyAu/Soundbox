package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.stubs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.PlaylistPersistence;

public class PlaylistPersistenceStub implements PlaylistPersistence {
    private List<Playlist> playlists;

    public PlaylistPersistenceStub() {
        playlists = new ArrayList<Playlist>();
        fakeBasePlaylists();
    }


    public void fakeBasePlaylists() {
        //fake data
        List<Song> songlist1 = new ArrayList<Song>();

        //thumbnail
        int[] covers = new int[]{
                R.drawable.playlist10,
                R.drawable.playlist9,
                R.drawable.playlist8,
                R.drawable.playlist7,
                R.drawable.playlist6,
                R.drawable.playlist5,
                R.drawable.playlist4,
                R.drawable.playlist3,
                R.drawable.playlist2,
                R.drawable.playlist1
        };

        //building songlist
        for (int i = 0; i < 10; i++) {
            Song testSong = new Song.Builder()
                    .setSongId(i)
                    .setSongName("This is True Romance playlist")
                    .setArtist("COMP3350")
                    .setLength(4)
                    .build();
            songlist1.add(testSong);
        }

        //building base playlists
        playlists.add(new Playlist(1, "True Romance", covers[0], songlist1));
        playlists.add(new Playlist(2, "Xscpae", covers[1], songlist1));
        playlists.add(new Playlist(3, "Maroon 5", covers[2], songlist1));
        playlists.add(new Playlist(4, "Born to Die", covers[3], songlist1));
        playlists.add(new Playlist(5, "Honeymoon", covers[4], songlist1));
        playlists.add(new Playlist(6, "I Need a Doctor", covers[5], songlist1));
        playlists.add(new Playlist(7, "Loud", covers[6], songlist1));
        playlists.add(new Playlist(8, "Legend", covers[7], songlist1));
        playlists.add(new Playlist(9, "Hello", covers[8], songlist1));
        playlists.add(new Playlist(10, "Greatest Hits", covers[9], songlist1));
    }

    @Override
    public List<Playlist> getAllPlaylists() {
        return Collections.unmodifiableList(playlists);
    }

    @Override
    public boolean insertPlaylist(Playlist playlist) {
        if(!playlists.contains(playlist))
            return playlists.add(playlist);
        return false;
    }

    @Override
    public boolean deletePlaylist(Playlist playlist) {
        return playlists.remove(playlist);
    }

    @Override
    public boolean updatePlaylist(Playlist playlist) {
        int index = playlists.indexOf(playlist);
        if (index >= 0) {
            playlists.set(index, playlist);
            return true;
        }
        return false;
    }

    @Override
    public long getNextId() {
        return playlists.size() + 1;
    }

}
