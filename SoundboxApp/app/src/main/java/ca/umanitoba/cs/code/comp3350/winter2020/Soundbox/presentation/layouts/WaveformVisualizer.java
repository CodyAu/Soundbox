package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.layouts;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.visualizer.WaveformRenderer;

public class WaveformVisualizer extends View {
    public static final int CAPTURE_SIZE = 1 << 7; // Must be a power of 2

    private byte[] waveformData;
    private int sampleRateWaveform;

    private byte[] fftData;
    private int sampleRateFft;

    private float smoothing;

    private WaveformRenderer renderer;

    private boolean renderBlank;

    public WaveformVisualizer(Context context) {
        super(context);

        smoothing = 0.0f;
        renderBlank = false;
    }

    public WaveformVisualizer(Context context, AttributeSet attrs) {
        super(context, attrs);

        smoothing = 0.0f;
        renderBlank = false;
    }

    public WaveformVisualizer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        smoothing = 0.0f;
        renderBlank = false;
    }

    public void setSmoothing(float smoothing) {
        this.smoothing = Math.min(Math.max(smoothing, 0.0f), 1.0f);
    }

    public void setRenderer(WaveformRenderer renderer) {
        this.renderer = renderer;
    }

    public void setRenderBlank(boolean renderBlank) {
        this.renderBlank = renderBlank;
    }

    public void setWaveformData(byte[] waveform, int sampleRate) {
        if (this.waveformData == null || this.waveformData.length < waveform.length) {
            this.waveformData = Arrays.copyOf(waveform, waveform.length);
        } else {
            for (int i = 0; i < waveform.length; i++) {
                this.waveformData[i] = (byte) (waveform[i] + smoothing * (this.waveformData[i] - waveform[i]));
            }
        }
        this.sampleRateWaveform = sampleRate;

        if(renderer != null && !renderer.renderMode()) invalidate();
    }

    public void setFftData(byte[] fft, int sampleRate) {
        if (this.fftData == null || this.fftData.length < fft.length) {
            this.fftData = Arrays.copyOf(fft, fft.length);
        } else {
            for (int i = 0; i < fft.length; i++) {
                this.fftData[i] = (byte) (fft[i] + smoothing * (this.fftData[i] - fft[i]));
            }
        }
        this.sampleRateFft = sampleRate;

        if(renderer != null && renderer.renderMode()) invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (renderer != null) {
            if (renderer.renderMode()) {
                if (renderBlank || fftData != null)
                    renderer.renderFft(canvas, fftData);
            } else {
                if (renderBlank || waveformData != null)
                    renderer.renderWaveform(canvas, waveformData);
            }
        }
    }

}

