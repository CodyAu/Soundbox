package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.media.AudioFormat;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.AudioLoader;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.SongController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Calculate;

public class DetailsFragment extends DialogFragment {
    public static final String TAG = "DIALOG_FRAGMENT";

    public static final String SONG_ARG = "song_id";

    private MainActivity mainActivity;

    private Toolbar toolbar;

    private ImageView thumbnail;
    private TextView title, artist, albumArtist, album, genre, length, score, format, bitrate, bitDepth, sampleRate, size, filepath;

    private Song song;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(requireContext(), getTheme()) {
            @Override
            public void onBackPressed() {
                dismiss();
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
        this.song = null;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.content_track_details, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            long songId = arguments.getLong(SONG_ARG);
            this.song = (new SongController()).getSongById(songId);
        }

        toolbar = view.findViewById(R.id.toolbar);

        thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

        title = (TextView) view.findViewById(R.id.title_content);
        artist = (TextView) view.findViewById(R.id.artist_content);
        albumArtist = (TextView) view.findViewById(R.id.album_artist_content);
        album = (TextView) view.findViewById(R.id.album_content);
        genre = (TextView) view.findViewById(R.id.genre_content);
        length = (TextView) view.findViewById(R.id.length_content);
        score = (TextView) view.findViewById(R.id.score_content);
        format = (TextView) view.findViewById(R.id.format_content);
        bitrate = (TextView) view.findViewById(R.id.bitrate_content);
        bitDepth = (TextView) view.findViewById(R.id.bit_depth_content);
        sampleRate = (TextView) view.findViewById(R.id.sample_rate_content);
        size = (TextView) view.findViewById(R.id.size_content);
        filepath = (TextView) view.findViewById(R.id.filepath_content);

        initToolbar();
        initDetails();
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    private void initToolbar() {
        toolbar.setTitle(R.string.action_details);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        return true;
                    default:
                        return false;
                }
            }
        });
        toolbar.inflateMenu(R.menu.dialog_details);

        toolbar.getMenu().findItem(R.id.action_edit).setVisible(false);
    }

    private void initDetails() {
        if (song != null) {
            Bitmap bitmapImage = (song.hasThumbnail()) ? AudioLoader.loadImageFromUri(mainActivity, Uri.parse(song.getFilepath())) : null;

            if (bitmapImage != null) thumbnail.setImageBitmap(bitmapImage);
            else Glide.with(mainActivity).load(R.drawable.playlist1).into(thumbnail);

            title.setText(song.getSongName());
            artist.setText(song.getArtist());
            albumArtist.setText(song.getAlbumArtist());
            album.setText(song.getAlbum());
            genre.setText(song.getGenre());
            length.setText(Calculate.formatTime(song.getLength()));
            score.setText(String.valueOf(song.getScore()));
            format.setText(song.getMimeType());
            bitrate.setText(Calculate.formatBitrate(song.getBitrate()));
            size.setText(Calculate.formatSize(song.getSize()));
            filepath.setText(song.getFilepath());

            MediaExtractor extractor = new MediaExtractor();
            try {
                extractor.setDataSource(song.getFilepath());
                if (extractor.getTrackCount() > 0) {
                    MediaFormat format = extractor.getTrackFormat(0);

                    int rawSampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE); // Required format parameter
                    String formattedBitDepth; // Optional format parameter. Key may not exist

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            int PCMEncoding = format.getInteger(MediaFormat.KEY_PCM_ENCODING);
                            switch (PCMEncoding) {
                                case AudioFormat.ENCODING_PCM_8BIT:
                                    formattedBitDepth = "8 bit";
                                    break;
                                case AudioFormat.ENCODING_PCM_FLOAT:
                                    formattedBitDepth = "32 bit float";
                                    break;
                                case AudioFormat.ENCODING_PCM_16BIT:
                                default:
                                    formattedBitDepth = "16 bit";
                                    break;
                            }
                        } else {
                            int bitWidth = format.getInteger("bit-width");
                            formattedBitDepth = bitWidth + " bit";
                        }
                    } catch (NullPointerException e) {
                        Log.e(DetailsFragment.class.getName(), "bit width not found");
                        formattedBitDepth = "16 bit";
                    }

                    bitDepth.setText(formattedBitDepth);
                    sampleRate.setText(Calculate.formatFrequency(rawSampleRate));
                }
            } catch (IOException e) {
                Log.e(DetailsFragment.class.getName(), "Could not extract song media for: " + song.getFilepath());
            }

            extractor.release();
        }
    }

    public static void openDialog(FragmentActivity activity, Song song) {
        if (song != null) {
            Bundle bundle = new Bundle();
            bundle.putLong(DetailsFragment.SONG_ARG, song.getSongId());

            DetailsFragment detailsFragment = new DetailsFragment();
            detailsFragment.setArguments(bundle);

            detailsFragment.show(activity.getSupportFragmentManager(), DetailsFragment.TAG);
        }
    }

}