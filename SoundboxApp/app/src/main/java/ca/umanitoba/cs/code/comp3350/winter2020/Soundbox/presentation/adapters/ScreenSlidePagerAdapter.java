package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.adapters;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.AudioLoader;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.PermissionManager;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackQueue;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.layouts.WaveformVisualizer;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.visualizer.WaveformRenderer;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.visualizer.WaveformRendererFactory;

/**
 * Adapter for quick switching songs while swiping
 */
public class ScreenSlidePagerAdapter extends RecyclerView.Adapter<ScreenSlidePagerAdapter.SongViewHolder> {
    private MainActivity mainActivity;

    private PlaybackQueue queue;

    private SharedPreferences sharedPreferences;

    private Visualizer visualizer;
    private WaveformRenderer renderer;
    private List<WaveformVisualizer> registeredWaveforms;

    public ScreenSlidePagerAdapter(MainActivity mainActivity, PlaybackQueue queue) {
        this.mainActivity = mainActivity;
        this.queue = queue;

        this.sharedPreferences = mainActivity.getSharedPreferences(mainActivity.getString(R.string.shared_pref), Context.MODE_PRIVATE);

        this.registeredWaveforms = new ArrayList<WaveformVisualizer>();
        setWaveRenderer(null);
        startVisualizer();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mainActivity).inflate(R.layout.content_expanded_song_info, parent, false);
        return new SongViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        if (position == 0) position = queue.queueSize() - 1;
        else if (position == getItemCount() - 1) position = 0;
        else position -= 1;

        final Song song = queue.peekIndex(position);

        if (song != null) {
            holder.title.setText(song.getSongName());
            holder.artist.setText(song.getArtist());
            holder.thumbnail.setImageBitmap(AudioLoader.loadCircularImageFromUri(mainActivity, Uri.parse(song.getFilepath())));

            holder.title.setSelected(true);
            holder.artist.setSelected(true);

            holder.waveform.setRenderer(renderer);
            holder.waveform.setSmoothing(0.0f);

            registerWaveform(holder.waveform);
        }
    }

    @Override
    public void onViewRecycled(@NonNull SongViewHolder holder) {
        super.onViewRecycled(holder);

        unregisterWaveform(holder.waveform);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        stopVisualizer();
    }

    @Override
    public int getItemCount() {
        if (queue != null && queue.queueSize() > 0)
            return queue.queueSize() + 2;
        return 1;
    }

    public void setWaveRenderer(WaveformRenderer renderer) {
        if (renderer == null) {
            WaveformRendererFactory.RendererType type = WaveformRendererFactory.RendererType.fromOrdinal(sharedPreferences.getInt(mainActivity.getString(R.string.shared_pref_visualization_type), 0));
            renderer = WaveformRendererFactory.createRenderer(type);
        }

        this.renderer = renderer;
        for (WaveformVisualizer waveform : registeredWaveforms) {
            waveform.setRenderer(this.renderer);
        }
    }

    private boolean registerWaveform(WaveformVisualizer waveformVisualizer) {
        return registeredWaveforms.add(waveformVisualizer);
    }

    private boolean unregisterWaveform(WaveformVisualizer waveformVisualizer) {
        return registeredWaveforms.remove(waveformVisualizer);
    }

    public void startVisualizer() {
        if (visualizer != null) return;

        if (PermissionManager.obtainedPermission(mainActivity, Manifest.permission.RECORD_AUDIO)) {
            try {
                visualizer = new Visualizer(PlaybackController.getMediaPlayerSessionId());
                visualizer.setEnabled(false);
                visualizer.setCaptureSize(WaveformVisualizer.CAPTURE_SIZE);
                visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                    @Override
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveformData, int samplingRate) {
                        if (waveformData != null) {
                            for (WaveformVisualizer waveform : registeredWaveforms) {
                                waveform.setWaveformData(waveformData, samplingRate);
                            }
                        }
                    }

                    @Override
                    public void onFftDataCapture(Visualizer visualizer, byte[] fftData, int samplingRate) {
                        if (fftData != null) {
                            for (WaveformVisualizer waveform : registeredWaveforms) {
                                waveform.setFftData(fftData, samplingRate);
                            }
                        }
                    }
                }, Visualizer.getMaxCaptureRate(), true, true);
                visualizer.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopVisualizer() {
        if (visualizer != null) {
            try {
                visualizer.setEnabled(false);
                visualizer.setDataCaptureListener(null, 0, false, false);
//                visualizer.release(); // Note if you call this on certain builds and set the visualizer to null it will crash. Most likely a bug when finalize is called causing it to destroy a mutex twice
                visualizer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        public TextView title, artist;
        public ImageView thumbnail;

        private WaveformVisualizer waveform;

        public SongViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            artist = itemView.findViewById(R.id.artist);
            thumbnail = itemView.findViewById(R.id.thumbnail);

            waveform = (WaveformVisualizer) itemView.findViewById(R.id.waveform);
        }

    }

}