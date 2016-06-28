package com.psyphertxt.android.cyfa.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Class for managing fonts
 */
public class Fonts {

    public static String FONT_THIN = "fonts/Roboto-Thin.ttf";
    public static String FONT_LIGHT = "fonts/Roboto-Light.ttf";
    public static String FONT_MEDIUM = "fonts/Roboto-Medium.ttf";
    public static String FONT_REGULAR = "fonts/Roboto-Regular.ttf";

    public static void thinFont(TextView view, Context context) {

        view.setTypeface(Typeface.createFromAsset(context.getAssets(),FONT_THIN));

    }

    public static void lightFont(TextView view, Context context) {

        view.setTypeface(Typeface.createFromAsset(context.getAssets(),FONT_LIGHT));
    }

    public static void mediumFont(TextView view, Context context) {

        view.setTypeface(Typeface.createFromAsset(context.getAssets(),FONT_MEDIUM));

    }

    public static void regularFont(TextView view, Context context) {

        view.setTypeface(Typeface.createFromAsset(context.getAssets(),FONT_REGULAR));

    }
}
