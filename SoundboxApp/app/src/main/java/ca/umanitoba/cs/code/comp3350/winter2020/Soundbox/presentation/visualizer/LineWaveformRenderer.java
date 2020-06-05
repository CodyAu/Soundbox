package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.visualizer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.ColorInt;

public class LineWaveformRenderer implements WaveformRenderer {
    private static final int Y_FACTOR = 0xFF;

    @ColorInt
    private int backgroundColor;
    private Paint foregroundPaint;
    private final Path waveformPath;

    LineWaveformRenderer(@ColorInt int foregroundColor) {
        setBackgroundColor(Color.TRANSPARENT);
        setForegroundColor(foregroundColor);
        this.waveformPath = new Path();
    }

    LineWaveformRenderer(@ColorInt int foregroundColor, @ColorInt int backgroundColor) {
        setForegroundColor(foregroundColor);
        setBackgroundColor(backgroundColor);
        this.waveformPath = new Path();
    }

    public void setBackgroundColor(@ColorInt int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setForegroundColor(@ColorInt int foregroundColor) {
        Paint paint = new Paint();
        paint.setColor(foregroundColor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        this.foregroundPaint = paint;
    }

    @Override
    public void renderWaveform(Canvas canvas, byte[] waveform) {
        canvas.drawColor(backgroundColor);

        float width = canvas.getWidth();
        float height = canvas.getHeight();

        waveformPath.reset();

        if (waveform != null) renderWaveform(waveform, width, height);
        else renderBlank(width, height);

        canvas.drawPath(waveformPath, foregroundPaint);
    }

    @Override
    public void renderFft(Canvas canvas, byte[] fft) {
        canvas.drawColor(backgroundColor);

        float width = canvas.getWidth();
        float height = canvas.getHeight();

        waveformPath.reset();

        renderBlank(width, height);

        canvas.drawPath(waveformPath, foregroundPaint);
    }

    @Override
    public boolean renderMode() {
        return false;
    }

    private void renderWaveform(byte[] waveform, float width, float height) {
        float xIncrement = width / (float) (waveform.length);
        float yIncrement = height / Y_FACTOR;
        int halfHeight = (int) (height * 0.5f);
        waveformPath.moveTo(0, halfHeight);
        for (int i = 1; i < waveform.length; i++) {
            float yPosition = waveform[i] > 0 ? height - (yIncrement * waveform[i]) : -(yIncrement * waveform[i]);
            waveformPath.lineTo(xIncrement * i, yPosition);
        }
        waveformPath.lineTo(width, halfHeight);
    }

    private void renderBlank(float width, float height) {
        int y = (int) (height * 0.5f);
        waveformPath.moveTo(0, y);
        waveformPath.lineTo(width, y);
    }

}
