package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.visualizer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.ColorInt;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

public class BarWaveformRenderer implements WaveformRenderer {
    public enum ColorPalette {
        RAINBOW,
        CUSTOM
    }

    private static final int Y_FACTOR = 0xFF;
    private static final float STROKE_WIDTH = 2;

    private ColorPalette palette;
    @ColorInt
    private int backgroundColor;
    @ColorInt
    int[] foregroundColors;
    private Paint foregroundPaint;

    BarWaveformRenderer(ColorPalette palette) {
        this.palette = palette;
        setBackgroundColor(Color.TRANSPARENT);
    }

    BarWaveformRenderer(ColorPalette palette, @ColorInt int backgroundColor) {
        this.palette = palette;
        setBackgroundColor(backgroundColor);
    }

    private Paint getForegroundPaint() {
        if (this.foregroundPaint == null) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStrokeWidth(STROKE_WIDTH);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            this.foregroundPaint = paint;
        }
        return this.foregroundPaint;
    }

    private void setColor(float amount, float[] hsv) {
        if (hsv.length != 3) return;

        // Makes sure the setup is done correctly, if not default to a different pallet;
        ColorPalette palette = this.palette;
        if (this.foregroundColors == null) palette = ColorPalette.RAINBOW;

        switch (palette) {
            case CUSTOM: {
                float fraction = amount * (this.foregroundColors.length - 1);
                int section = (int) fraction;
                int colorFrom = foregroundColors[section];
                int colorTo = foregroundColors[(section + 1) % this.foregroundColors.length];
                int color = (int) ArgbEvaluator.getInstance().evaluate(fraction % 1, colorFrom, colorTo);
                getForegroundPaint().setColor(color);
                break;
            }
            case RAINBOW:
            default: {
                hsv[0] = 360 * amount;
                int color = Color.HSVToColor(hsv);
                getForegroundPaint().setColor(color);
                break;
            }
        }
    }

    public void setBackgroundColor(@ColorInt int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setForegroundColors(@ColorInt int[] colors) {
        if (colors == null || colors.length <= 0) return;
        this.foregroundColors = colors.clone();
    }

    public void setPalette(ColorPalette palette) {
        this.palette = palette;
    }

    @Override
    public void renderWaveform(Canvas canvas, byte[] waveform) {
        canvas.drawColor(backgroundColor);

        float width = canvas.getWidth();
        float height = canvas.getHeight();

        if (waveform != null) renderWaveform(canvas, waveform, width, height);
        else renderBlank(canvas, width, height);
    }

    @Override
    public void renderFft(Canvas canvas, byte[] fft) {
        canvas.drawColor(backgroundColor);

        float width = canvas.getWidth();
        float height = canvas.getHeight();

        if (fft != null) renderFft(canvas, fft, width, height);
        else renderBlank(canvas, width, height);
    }

    @Override
    public boolean renderMode() {
        return true;
    }

    private void renderWaveform(Canvas canvas, byte[] waveform, float width, float height) {
        float xIncrement = width / (float) (waveform.length);
        float yIncrement = (height / 4) / Y_FACTOR;
        float basline = 3 * height / 4;

        float[] hsv = {0, 1.0f, 1.0f};

        getForegroundPaint().setStrokeWidth(xIncrement);
        for (int i = 0; i < waveform.length; i++) {
            float amount = ((float) i) / waveform.length;
            setColor(amount, hsv);

            float barPosition = waveform[i] > 0 ? Y_FACTOR - (waveform[i]) : -waveform[i];
            barPosition = yIncrement * barPosition;

            float yPosition = basline - barPosition;
            canvas.drawLine(i * xIncrement, basline, i * xIncrement, yPosition, this.foregroundPaint);
        }
    }

    private void renderFft(Canvas canvas, byte[] fft, float width, float height) {
        int n = fft.length;
        float[] magnitudes = new float[n / 2 + 1];

        magnitudes[0] = (float) (Math.abs(fft[0]) * Math.abs(fft[0]));      // DC
        magnitudes[n / 2] = (float) (Math.abs(fft[1]) * Math.abs(fft[1]));  // Nyquist

        // Calculate magnitudes
        for (int k = 1; k < n / 2; k++) {
            int i = k * 2;

            float rfk = fft[i];
            float ifk = fft[i + 1];

            magnitudes[k] = rfk * rfk + ifk * ifk;
        }

        float barWidth = 0.9f;
        float xIncrement = barWidth * width / (float) (n / 2);
        float basline = 3 * height / 4;

        // Render db values

        float[] hsv = {0, 1.0f, 1.0f};

        getForegroundPaint().setStrokeWidth(xIncrement);
        for (int i = 0; i < magnitudes.length; i++) {
            float amount = ((float) i) / magnitudes.length;
            setColor(amount, hsv);

            float dbValue = (float) (10 * Math.log10(1.0f + magnitudes[i]));

            float scale = 4.0f;
            float yPosition = basline - (scale * dbValue) - STROKE_WIDTH;
            float xPosition = i * xIncrement + (1 - barWidth) / 2 * width;
            canvas.drawLine(xPosition, basline, xPosition, yPosition, this.foregroundPaint);
        }
    }

    private void renderBlank(Canvas canvas, float width, float height) {
        // Pass
    }

}
