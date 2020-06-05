package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;

import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils.SongCollectionFilters;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils.SongFilters;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongCollection;

public final class SearchSuggestions {
    private static final String SEARCH_COLUMN_INDEX = "searchKey";

    public static final int SUGGESTIONS_LIMIT = 5; // Shows the top 5 suggestions from what the user typed
    public static final int SEARCH_LIST_LIMIT = 25; // Shows the top 25 closest results for what the user queried

    public static SimpleCursorAdapter createSimpleCursorAdapter(Context context) {
        final String[] from = new String[]{SEARCH_COLUMN_INDEX};
        final int[] to = new int[]{R.id.text1};
        return new SimpleCursorAdapter(context,
                R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    public static String getSuggestion(SimpleCursorAdapter simpleCursorAdapter, int position) {
        Cursor cursor = (Cursor) simpleCursorAdapter.getItem(position);
        return cursor.getString(cursor.getColumnIndex(SEARCH_COLUMN_INDEX));
    }

    public static <T extends SongCollection> void populateAdapterCollections(SimpleCursorAdapter simpleCursorAdapter, String query, List<T> collections) {
        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, SEARCH_COLUMN_INDEX});

        List<T> suggestions = SongCollectionFilters.getSongCollectionsByNameLike(query, collections);
        for (int i = 0; i < Math.min(suggestions.size(), SUGGESTIONS_LIMIT); i++) {
            c.addRow(new Object[]{i, suggestions.get(i).getName()});
        }

        simpleCursorAdapter.changeCursor(c);
    }

    public static <T extends SongCollection> void populateAdapterSongs(SimpleCursorAdapter simpleCursorAdapter, String query, T collection) {
        populateAdapterSongs(simpleCursorAdapter, query, collection.getSongs());
    }

    public static void populateAdapterSongs(SimpleCursorAdapter simpleCursorAdapter, String query, List<Song> songs) {
        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, SEARCH_COLUMN_INDEX});

        List<Song> suggestions = SongFilters.getSongsLike(query, songs);
        for (int i = 0; i < Math.min(suggestions.size(), SUGGESTIONS_LIMIT); i++) {
            c.addRow(new Object[]{i, suggestions.get(i).getSongName()});
        }
        simpleCursorAdapter.changeCursor(c);
    }

}
