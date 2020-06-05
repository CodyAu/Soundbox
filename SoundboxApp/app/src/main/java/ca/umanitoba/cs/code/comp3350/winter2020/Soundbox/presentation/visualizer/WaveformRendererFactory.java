package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.visualizer;

import android.graphics.Color;

import androidx.annotation.ColorInt;

public class WaveformRendererFactory {
    public enum RendererType {
        CIRCLE_BAR_VERVE,
        CIRCLE_BAR_RAINBOW,
//        BAR,
        LINE,
        NONE;

        /**
         * @param ord
         * @return returns the Statistic with id of ord
         */
        public static RendererType fromOrdinal(int ord) {
            if (ord >= values().length || ord < 0)
                return null;
            return values()[ord];
        }
    }

    public static LineWaveformRenderer createLineWaveformRenderer(@ColorInt int foregroundColor) {
        return new LineWaveformRenderer(foregroundColor);
    }

    public static LineWaveformRenderer createLineWaveformRenderer(@ColorInt int backgroundColor, @ColorInt int foregroundColor) {
        return new LineWaveformRenderer(foregroundColor, backgroundColor);
    }

    public static BarWaveformRenderer createBarWaveformRenderer(BarWaveformRenderer.ColorPalette palette) {
        return new BarWaveformRenderer(palette);
    }

    public static BarWaveformRenderer createBarWaveformRenderer(BarWaveformRenderer.ColorPalette palette, @ColorInt int backgroundColor) {
        return new BarWaveformRenderer(palette, backgroundColor);
    }

    public static CircleWaveformRenderer createCircleWaveformRenderer(CircleWaveformRenderer.ColorPalette palette) {
        return new CircleWaveformRenderer(palette);
    }

    public static CircleWaveformRenderer createCircleWaveformRenderer(CircleWaveformRenderer.ColorPalette palette, @ColorInt int backgroundColor) {
        return new CircleWaveformRenderer(palette, backgroundColor);
    }

    public static WaveformRenderer createRenderer(RendererType type) {
        switch (type) {
            case CIRCLE_BAR_VERVE:
                CircleWaveformRenderer circleRenderer = createCircleWaveformRenderer(CircleWaveformRenderer.ColorPalette.CUSTOM);
                circleRenderer.setForegroundColors(new int[]{Color.MAGENTA, Color.BLUE, Color.CYAN, Color.BLUE});
                return circleRenderer;
            case CIRCLE_BAR_RAINBOW:
                return createCircleWaveformRenderer(CircleWaveformRenderer.ColorPalette.RAINBOW);
//            case BAR:
//                BarWaveformRenderer barRenderer = createBarWaveformRenderer(BarWaveformRenderer.ColorPalette.CUSTOM);
//                barRenderer.setForegroundColors(new int[]{Color.RED, Color.WHITE, Color.BLUE});
//                return barRenderer;
            case LINE:
                return createLineWaveformRenderer(Color.RED);
            case NONE:
            default:
                return null;
        }
    }

}
