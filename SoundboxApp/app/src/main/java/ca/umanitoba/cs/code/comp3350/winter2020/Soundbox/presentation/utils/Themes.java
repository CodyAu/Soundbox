package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatDelegate;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;

public class Themes {

    public static boolean changeThemeModeCreate(Activity activity, SharedPreferences sharedPreferences) {
        boolean isDarkTheme = getIsDark(activity, sharedPreferences);
        int newThemeId = isDarkTheme ? R.style.DarkTheme : R.style.LightTheme;
        activity.setTheme(newThemeId);

        if (isDarkTheme) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        return isDarkTheme;
    }

    public static void changeThemeMode(Activity activity, SharedPreferences sharedPreferences, boolean isDark) {
        boolean isDarkTheme = getIsDark(activity, sharedPreferences);
        if (isDarkTheme != isDark) {
            Intent intent = new Intent(activity, activity.getClass());
            activity.startActivity(intent);

            activity.finish();
        }
    }

    public static boolean getIsDark(Activity activity, SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(activity.getString(R.string.shared_pref_theme), true);
    }

    public static int getThemeColor(Context context, int resId) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(resId, typedValue, true);
        return typedValue.data;
    }

    public static void setSystemBarColor(Activity activity, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    // From https://www.materialui.co/colors
    public static int[] getMaterialColors() {
        return new int[]{
                Color.rgb(244, 67, 54),
                Color.rgb(233, 30, 99),
                Color.rgb(156, 39, 176),
                Color.rgb(103, 58, 183),
                Color.rgb(63, 81, 181),
                Color.rgb(33, 150, 243),
                Color.rgb(3, 169, 244),
                Color.rgb(0, 188, 212),
                Color.rgb(0, 150, 136),
                Color.rgb(76, 175, 80),
                Color.rgb(139, 195, 74),
                Color.rgb(205, 220, 57),
                Color.rgb(255, 235, 59),
                Color.rgb(255, 193, 7),
                Color.rgb(255, 152, 0),
                Color.rgb(255, 87, 34),
                Color.rgb(158, 158, 158),
                Color.rgb(96, 125, 139)
        };
    }

}
