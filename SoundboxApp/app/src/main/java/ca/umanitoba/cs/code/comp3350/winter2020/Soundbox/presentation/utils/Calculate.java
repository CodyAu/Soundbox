package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.Window;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class Calculate {
    public static String DEFAULT_TIME_FORMAT = "mm:ss";

    /**
     * Converting dp to pixel
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dpToPx(Context context, float dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static int statusBarHeightResource(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0)
            result = activity.getResources().getDimensionPixelSize(resourceId);

        return result;
    }

    public static float mapRange(float value, float min, float max, float newMin, float newMax) {
        return newMin + ((newMax - newMin) / (max - min)) * (value - min);
    }

    public static int statusBarHeight(Activity activity) {
        Rect rectangle = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }

    public static int titleBarHeight(Activity activity) {
        int contentViewTop = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        return contentViewTop - statusBarHeight(activity);
    }

    public static String formatTime(String pattern, int milliseconds) {
        DateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date(milliseconds));
    }

    public static String formatTime(int milliseconds) {
        return formatTime(DEFAULT_TIME_FORMAT, milliseconds);
    }

    /**
     * Generates a human readable string of text
     * e.g. 245453245 -> 68 hours 10 minutes 53 seconds
     *
     * @param givenTime
     * @param givenUnits
     * @return a human readable string
     */
    public static String formatTimeReadable(final int givenTime, TimeUnit givenUnits) {
        int givenTimeLeft = givenTime;
        String readable = "";

        TimeUnit[] displayUnits = {TimeUnit.HOURS, TimeUnit.MINUTES, TimeUnit.SECONDS};
        for(TimeUnit unit : displayUnits){
            long localUnit = unit.convert(givenTimeLeft, givenUnits);
            givenTimeLeft -= givenUnits.convert(localUnit, unit);

            if(localUnit > 0) readable += localUnit + " " + unit.toString().toLowerCase() + " ";
        }

        if(readable.isEmpty())
            readable = "None";

        return readable;
    }

    /**
     * @param bytes
     * @return formatted size of bytes. e.g. 1500 -> 1.46KB
     */
    public static String formatSize(long bytes) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        if (bytes >= 1024 * 1024 * 1024)
            return formatter.format(bytes / 1024.0f / 1024.0f / 1024.0f) + "GB";
        else if (bytes > 1024 * 1024)
            return formatter.format(bytes / 1024.0f / 1024.0f) + "MB";
        else if (bytes >= 1024)
            return formatter.format(bytes / 1024.0f) + "KB";
        else
            return bytes + "B";
    }

    /**
     * @param bitrate
     * @return formatted size of bitrate. e.g. 1500 -> 1.5kbps
     */
    public static String formatBitrate(long bitrate) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        if (bitrate >= 1000 * 1000 * 1000)
            return formatter.format(bitrate / 1000.0f / 1000.0f / 1000.0f) + "Gbps";
        else if (bitrate > 1000 * 1000)
            return formatter.format(bitrate / 1000.0f / 1000.0f) + "Mbps";
        else if (bitrate >= 1000)
            return formatter.format(bitrate / 1000.0f) + "kbps";
        else
            return bitrate + "bps";
    }

    /**
     * @param frequency
     * @return formatted size of frequency. e.g. 1500 -> 1.5KhZ
     */
    public static String formatFrequency(long frequency) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        if (frequency >= 1000 * 1000 * 1000)
            return formatter.format(frequency / 1000.0f / 1000.0f / 1000.0f) + "GHz";
        else if (frequency > 1024 * 1024)
            return formatter.format(frequency / 1000.0f / 1000.0f) + "MHz";
        else if (frequency >= 1024)
            return formatter.format(frequency / 1000.0f) + "kHz";
        else
            return frequency + "Hz";
    }

}
