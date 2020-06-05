package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;

public class AudioLoader {
    private static String DEFAULT_ASSET_PATH = "music";

    public static List<Song> loadSongsFromAssets(final Context context) {
        return loadSongsFromAssets(context, DEFAULT_ASSET_PATH);
    }

    //Load all default songs from assets
    public static List<Song> loadSongsFromAssets(final Context context, String path) {
        List<Song> songs = new ArrayList<Song>();
        List<String> paths = loadPathsFromAssets(context, path);
        for (String p : paths) {
            Song newSong = loadSongFromPath(p);
            if (newSong != null) songs.add(newSong);
        }
        return songs;
    }

    public static List<String> loadPathsFromAssets(final Context context) {
        return loadPathsFromAssets(context, DEFAULT_ASSET_PATH);
    }

    //Load all default songs from assets
    public static List<String> loadPathsFromAssets(final Context context, String path) {
        List<String> paths = new ArrayList<String>();
        AssetManager assets = context.getResources().getAssets();
        try {
            String[] music = assets.list(path);
            File saveDirectory = context.getExternalFilesDir(null);
            for (String p : music) {
                File outFile = new File(saveDirectory, p);
                String saveTo = outFile.getAbsolutePath();

                if (!outFile.exists()) {
                    InputStream inputStream = assets.open(path + "/" + p);
                    FileOutputStream outputStream = new FileOutputStream(saveTo);
                    byte[] buff = new byte[10 * 1024];
                    int len;
                    while ((len = inputStream.read(buff)) > 0) {
                        outputStream.write(buff, 0, len);
                    }

                    inputStream.close();
                    outputStream.close();
                }
                paths.add(saveTo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }

    public static void chooseDocumentFile(final Activity activity, final int requestId) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");

        activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.select_audio)), requestId);
    }

    public static void chooseDocumentFile(final Fragment fragment, final int requestId) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");

        fragment.startActivityForResult(Intent.createChooser(intent, fragment.getString(R.string.select_audio)), requestId);
    }

    public static List<Song> loadSongsFromMediaStore(final Context context) {
        return loadSongsFromMediaStore(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
    }

    public static List<Song> loadSongsFromMediaStore(final Context context, final Uri uri) {
        List<Song> songs = new ArrayList<Song>();

        for (String path : loadPathsFromMediaStore(context, uri)) {
            Song newSong = loadSongFromPath(path);
            if (newSong != null) songs.add(newSong);
        }

        return songs;
    }

    public static List<String> loadPathsFromMediaStore(final Context context) {
        return loadPathsFromMediaStore(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
    }

    public static List<String> loadPathsFromMediaStore(final Context context, final Uri uri) {
        List<String> paths = new ArrayList<String>();

        ContentResolver contentResolver = context.getContentResolver();

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA
        };

        Cursor cursor = null;

        try {
            cursor = contentResolver.query(
                    uri,
                    projection,
                    selection,
                    null,
                    null);

            if (cursor.moveToFirst()) {
                do {
                    String path = cursor.getString(1);

                    paths.add(path);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return paths;
    }

    public static Song loadSongFromPath(final String path) {
        File songFile = new File(path);
        if (!songFile.exists()) return null;

        MediaMetadataRetriever mediaMetadataRetriever;
        //try initialize the metadata retriever
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(path);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }

        //create song from data
        Song newSong = new Song(
                1,
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST),
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE),
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION),
                0,
                path,
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE),
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE),
                songFile.length(),
                mediaMetadataRetriever.getEmbeddedPicture() != null
        );

        //if the title meta data is not available, use the filename instead
        if (newSong.getSongName() == null || newSong.getSongName().isEmpty()) {
            String filename = newSong.getFilename(false);
            newSong.setSongName(filename != null ? filename : "");
        }

        return newSong;
    }

    public static Song loadSongFromUri(final Context context, final Uri uri) {
        return loadSongFromPath(getPathFromUri(context, uri));
    }

    public static Bitmap loadImageFromUri(final Context context, final Uri uri) {
        MediaMetadataRetriever mediaMetadataRetriever;
        //try initialize the metadata retriever
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(context, uri);

        } catch (IllegalArgumentException e) {
            Log.e(AudioLoader.class.getName(), "IllegalArgumentException: Could not find embedded image for that Uri.");
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }

        byte[] data = mediaMetadataRetriever.getEmbeddedPicture();

        // convert the byte array to a bitmap
        return (data == null) ? null : BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static Bitmap loadCircularImageFromUri(final Context context, final Uri uri) {
        Bitmap bitmap = loadImageFromUri(context, uri);
        if (bitmap == null) return null;

        int radius = Math.min(bitmap.getWidth(), bitmap.getHeight()) / 2;
        Bitmap circleBitmap = Bitmap.createBitmap(2 * radius, 2 * radius, Bitmap.Config.ARGB_8888);
        Matrix translationMatrix = new Matrix();
        translationMatrix.setTranslate(-(bitmap.getWidth() - circleBitmap.getWidth()) / 2, -(bitmap.getHeight() - circleBitmap.getHeight()) / 2);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        shader.setLocalMatrix(translationMatrix);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(shader);

        Canvas canvas = new Canvas(circleBitmap);
        canvas.drawCircle(radius, radius, radius, paint);

        return circleBitmap;
    }

    public static String getPathFromUri(final Context context, final Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        } else if (hasNoAuthority(uri)) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(final Context context, final Uri uri, final String selection, final String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    // Only this class uses these currently:

    public static boolean isExternalStorageDocument(final Uri uri) {
        return Objects.equals("com.android.externalstorage.documents", uri.getAuthority());
    }

    public static boolean isDownloadsDocument(final Uri uri) {
        return Objects.equals("com.android.providers.downloads.documents", uri.getAuthority());
    }

    public static boolean isMediaDocument(final Uri uri) {
        return Objects.equals("com.android.providers.media.documents", uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(final Uri uri) {
        return Objects.equals("com.google.android.apps.photos.content", uri.getAuthority());
    }

    public static boolean hasNoAuthority(final Uri uri) {
        return Objects.equals(uri.getAuthority(), null);
    }

}
