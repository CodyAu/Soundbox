package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.visualizer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import androidx.annotation.ColorInt;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

public class CircleWaveformRenderer implements WaveformRenderer {
    public enum ColorPalette {
        RAINBOW,
        CUSTOM
    }

    private static final int Y_FACTOR = 0xFF;
    private static final float DEFAULT_RADIUS = 0.5f;
    private static final float STROKE_WIDTH = 2;
    private static final float BASE_POW = 1.011f;

    private ColorPalette palette;
    @ColorInt
    private int backgroundColor;
    @ColorInt
    int[] foregroundColors;
    private Paint foregroundPaint;
    private Paint foregroundPaintBlur;
    private float radius;

    CircleWaveformRenderer(ColorPalette palette) {
        this.palette = palette;
        setBackgroundColor(Color.TRANSPARENT);
        this.radius = DEFAULT_RADIUS;
    }

    CircleWaveformRenderer(ColorPalette palette, @ColorInt int backgroundColor) {
        this.palette = palette;
        setBackgroundColor(backgroundColor);
        this.radius = DEFAULT_RADIUS;
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

    private Paint getForegroundPaintBlur() {
        if (this.foregroundPaintBlur == null) {
            Paint paint = new Paint();
            paint.set(getForegroundPaint());
            paint.setStrokeCap(Paint.Cap.SQUARE);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
            this.foregroundPaintBlur = paint;
        }
        return this.foregroundPaintBlur;
    }

    private void setColor(float amount, float[] hsv) {
        if (hsv.length != 3) return;

        // Makes sure the setup is done correctly, if not default to a different pallet;
        ColorPalette palette = this.palette;
        if (this.foregroundColors == null) palette = ColorPalette.RAINBOW;

        switch (palette) {
            case CUSTOM: {
                float fraction = amount * this.foregroundColors.length;
                int section = (int) fraction;
                int colorFrom = foregroundColors[section];
                int colorTo = foregroundColors[(section + 1) % this.foregroundColors.length];
                int color = (int) ArgbEvaluator.getInstance().evaluate(fraction % 1, colorFrom, colorTo);
                getForegroundPaint().setColor(color);
                getForegroundPaintBlur().setColor(color);
                getForegroundPaintBlur().setAlpha(31);
                break;
            }
            case RAINBOW:
            default: {
                hsv[0] = 360 * amount;
                int color = Color.HSVToColor(hsv);
                getForegroundPaint().setColor(color);
                getForegroundPaintBlur().setColor(color);
                getForegroundPaintBlur().setAlpha(31);
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

    public void setRadius(float radius) {
        this.radius = Math.min(Math.max(radius, 0), 1.0f);
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

        renderBlank(canvas, width, height);
    }

    @Override
    public boolean renderMode() {
        return false;
    }

    private void renderWaveform(Canvas canvas, byte[] waveform, float width, float height) {
        float outerRadius = Math.min(width, height) / 2;
        float radius = outerRadius * this.radius;
        float barHeight = outerRadius - radius;

        float halfWidth = width / 2;
        float halfHeight = height / 2;

        float xIncrement = (float) (2 * Math.PI * radius / waveform.length);
        float yIncrement = barHeight / Y_FACTOR;
        float normalization = (float) (Y_FACTOR / (Math.pow(BASE_POW, Y_FACTOR) - 1));

        float[] hsv = {0, 1.0f, 1.0f};

        canvas.save();
        canvas.translate(halfWidth, halfHeight);
        getForegroundPaint().setStrokeWidth(xIncrement);
        getForegroundPaintBlur().setStrokeWidth(3.5f * xIncrement);
        for (int i = 0; i < waveform.length; i++) {
            float amount = ((float) i) / waveform.length;
            setColor(amount, hsv);

            canvas.save();
            canvas.rotate(360.0f * amount);

            float barPosition = waveform[i] > 0 ? Y_FACTOR - (waveform[i]) : -waveform[i];
            barPosition = (float) (normalization * (Math.pow(BASE_POW, barPosition) - 1));

            float yBarPosition = yIncrement * barPosition;
            float yPosition = radius + yBarPosition;
            canvas.drawLine(0, radius + STROKE_WIDTH, 0, yPosition, this.foregroundPaint);
            if (i % 2 == 0)
                canvas.drawLine(0, radius + STROKE_WIDTH, 0, yPosition, this.foregroundPaintBlur);

            canvas.restore();
        }
        canvas.restore();

        getForegroundPaint().setColor(Color.WHITE);
        getForegroundPaint().setStrokeWidth(STROKE_WIDTH);
        canvas.drawArc(
                halfWidth - radius,
                halfHeight - radius,
                halfWidth + radius,
                halfHeight + radius,
                0.0f,
                360.0f,
                false,
                this.foregroundPaint
        );
    }

    private void renderBlank(Canvas canvas, float width, float height) {
        float outerRadius = Math.min(width, height) / 2;
        float radius = outerRadius * this.radius;
        float halfWidth = width / 2;
        float halfHeight = height / 2;

        getForegroundPaint().setColor(Color.WHITE);
        canvas.drawArc(
                halfWidth - radius,
                halfHeight - radius,
                halfWidth + radius,
                halfHeight + radius,
                0.0f,
                360.0f,
                false,
                this.foregroundPaint
        );
    }

}
