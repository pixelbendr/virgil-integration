package com.psyphertxt.android.cyfa.model;

import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.Settings;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.util.HashMap;

/**
 * This class contains methods for creating, retrieving and settings
 * a users theme color. It might be expanded to include cover images and other
 * things to do with view customization.
 */
public class Themes {

    private int colorPrimary;
    private int colorAccent;
    private String colorName;
    private int themeName;

    public int getThemeName() {
        return themeName;
    }

    public void setThemeName(int themeName) {
        this.themeName = themeName;
    }

    public int getColorAccent() {
        return colorAccent;
    }

    public void setColorAccent(int colorAccent) {
        this.colorAccent = colorAccent;
    }

    public int getColorPrimary() {
        return colorPrimary;
    }

    public void setColorPrimary(int colorPrimary) {
        this.colorPrimary = colorPrimary;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public static int getThemeByName(String name) {
        switch (name) {
            case Config.THEME_NAME_RED:
                return R.style.AppTheme_Red;
            case Config.THEME_NAME_YELLOW:
                return R.style.AppTheme_Yellow;
            case Config.THEME_NAME_ORANGE:
                return R.style.AppTheme_Orange;
            case Config.THEME_NAME_PINK:
                return R.style.AppTheme_Pink;
            case Config.THEME_NAME_BLUE:
                return R.style.AppTheme_Blue;
            case Config.THEME_NAME_GRAY:
                return R.style.AppTheme_Gray;
            case Config.THEME_NAME_GREEN:
                return R.style.AppTheme_Green;
            default:
                return R.style.AppTheme_Base;
        }
    }

    public static int getStoredColor(Context context) {
        Settings settings = new Settings(context);
        int theme = getThemeByColor(settings.getTheme()).get(Config.KEY_COLOR_PRIMARY);
        return ContextCompat.getColor(context, theme);
    }

    public static HashMap<String, Integer> getThemeByColor(String name) {
        HashMap<String, Integer> colors = new HashMap<>();
        switch (name) {
            case Config.THEME_NAME_RED:
                colors.put(Config.KEY_COLOR_PRIMARY, R.color.pomegranate);
                colors.put(Config.KEY_COLOR_ACCENT, R.color.cinnabar);
                return colors;
            case Config.THEME_NAME_YELLOW:
                colors.put(Config.KEY_COLOR_PRIMARY, R.color.amber);
                colors.put(Config.KEY_COLOR_ACCENT, R.color.selective_yellow);
                return colors;
            case Config.THEME_NAME_ORANGE:
                colors.put(Config.KEY_COLOR_PRIMARY, R.color.california);
                colors.put(Config.KEY_COLOR_ACCENT, R.color.gold_drop);
                return colors;
            case Config.THEME_NAME_PINK:
                colors.put(Config.KEY_COLOR_PRIMARY, R.color.wild_strawberry);
                colors.put(Config.KEY_COLOR_ACCENT, R.color.razzmatazz);
                return colors;
            case Config.THEME_NAME_BLUE:
                colors.put(Config.KEY_COLOR_PRIMARY, R.color.robins_egg_blue);
                colors.put(Config.KEY_COLOR_ACCENT, R.color.pacific_blue);
                return colors;
            case Config.THEME_NAME_GRAY:
                colors.put(Config.KEY_COLOR_PRIMARY, R.color.lynch);
                colors.put(Config.KEY_COLOR_ACCENT, R.color.fiord);
                return colors;
            case Config.THEME_NAME_GREEN:
                colors.put(Config.KEY_COLOR_PRIMARY, R.color.green_haze);
                colors.put(Config.KEY_COLOR_ACCENT, R.color.green_leaf);
                return colors;
            default:
                colors.put(Config.KEY_COLOR_PRIMARY, R.color.danube);
                colors.put(Config.KEY_COLOR_ACCENT, R.color.mariner);
                return colors;
        }
    }
}
