package com.wmendez.newsreader.lib.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;

public class Utils {

    public static String join(Object[] pieces, String glue) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pieces.length; i++) {
            if (i != 0)
                sb.append(glue);
            sb.append(pieces[i].toString());
        }
        return sb.toString();
    }

    public static boolean isTablet(Context context) {
        return context.getResources().getConfiguration().smallestScreenWidthDp >= 600;
    }

    private static final int BRIGHTNESS_THRESHOLD = 130;

    /**
     * Calculate whether a color is light or dark, based on a commonly known
     * brightness formula.
     *
     * @see {@literal http://en.wikipedia.org/wiki/HSV_color_space%23Lightness}
     */
    public static boolean isColorDark(int color) {
        return ((30 * Color.red(color) +
                59 * Color.green(color) +
                11 * Color.blue(color)) / 100) <= BRIGHTNESS_THRESHOLD;
    }

    public static float getProgress(int value, int min, int max) {
        if (min == max) {
            throw new IllegalArgumentException("Max (" + max + ") cannot equal min (" + min + ")");
        }

        return (value - min) / (float) (max - min);
    }

    public static GradientDrawable getTitleBackground(int bottomColor) {
        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{bottomColor, Color.parseColor("#8FAAAAAA")});
        gradient.setShape(GradientDrawable.LINEAR_GRADIENT);
        gradient.setCornerRadius(0);
        return gradient;
    }

    public static void setTitleBackground(View ninfo, int mutedColor) {
        ninfo.setBackgroundColor(mutedColor);
        /*
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ninfo.setBackground(getTitleBackground(mutedColor));
        } else {
            ninfo.setBackgroundColor(mutedColor);
        }
        */
    }
}
