package com.psyphertxt.android.cyfa.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.View;

import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.R;

/**
 * This Class contains methods for helper methods for
 * creating and manipulating shape objects
 */
public class Shape {

    public static GradientDrawable oval(int stroke, int primaryColor, int accentColor) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(primaryColor);
        drawable.setStroke(stroke, accentColor);

        return drawable;
    }

    public static void background(View view, Drawable drawable) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }

    public static GradientDrawable oval(int primaryColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(primaryColor);

        return drawable;
    }

    public static GradientDrawable rectangle(int primaryColor) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(primaryColor);
        drawable.setCornerRadius(16);

        return drawable;
    }

    public static GradientDrawable box(int primaryColor) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(primaryColor);

        return drawable;
    }

    public static int getAccentColor(Context context) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    public static int getPrimaryColor(Context context) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    public static void modeColor(final View view, final int color) {

        ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();

        Integer colorFrom = colorDrawable.getColor();
        Integer colorTo = color;
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((Integer) animator.getAnimatedValue());
            }
        });

        colorAnimation.setDuration(Config.DEFAULT_ANIMATION_DURATION);
        colorAnimation.start();

    }

}
