package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.visualizer;

import android.graphics.Canvas;

public interface WaveformRenderer {

    void renderWaveform(Canvas canvas, byte[] waveform);

    void renderFft(Canvas canvas, byte[] fft);

    boolean renderMode();

}