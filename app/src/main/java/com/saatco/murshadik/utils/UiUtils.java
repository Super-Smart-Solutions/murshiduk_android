package com.saatco.murshadik.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;


import com.saatco.murshadik.R;
import com.saatco.murshadik.App;
import com.saatco.murshadik.views.SearchableToolbar;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

public class UiUtils {

    private static final int RANDOM_COLOR_START_RANGE = 0;
    private static final int RANDOM_COLOR_END_RANGE = 9;

    private static final int COLOR_MAX_VALUE = 255;
    private static final float COLOR_ALPHA = 0.8f;
    private static final Map<Integer, Integer> colorsMap = new HashMap<>();

    private static final Random random = new Random();
    private static int previousColor;

    private UiUtils() {
    }

    public static Drawable getGreyCircleDrawable() {
        return getColoredCircleDrawable(App.getInstance().getResources().getColor(R.color.grey, App.getInstance().getTheme()));
    }

    public static Drawable getRandomColorCircleDrawable() {
        return getColoredCircleDrawable(getRandomCircleColor());
    }

    public static Drawable getColorCircleDrawable(int colorPosition) {
        return getColoredCircleDrawable(getCircleColor(colorPosition % RANDOM_COLOR_END_RANGE));
    }

    public static Drawable getColoredCircleDrawable(@ColorInt int color) {
        GradientDrawable drawable = (GradientDrawable) App.getInstance().getResources().getDrawable(R.drawable.shape_circle, App.getInstance().getTheme());
        drawable.setColor(color);
        return drawable;
    }

    public static int getRandomCircleColor() {
        int randomNumber = random.nextInt(RANDOM_COLOR_END_RANGE) + 1;

        int generatedColor = getCircleColor(randomNumber);
        if (generatedColor != previousColor) {
            previousColor = generatedColor;
            return generatedColor;
        } else {
            do {
                generatedColor = getRandomCircleColor();
            } while (generatedColor != previousColor);
        }
        return previousColor;
    }

    public static int getCircleColor(@IntRange(from = RANDOM_COLOR_START_RANGE, to = RANDOM_COLOR_END_RANGE)
                                             int colorPosition) {
        String colorIdName = String.format("random_color_%d", colorPosition + 1);
        int colorId = App.getInstance().getResources()
                .getIdentifier(colorIdName, "color", App.getInstance().getPackageName());

        //BSM
        return App.getInstance().getResources().getColor(R.color.green, App.getInstance().getTheme());
    }

    public static int getRandomTextColorById(Integer senderId) {
        if (colorsMap.get(senderId) == null) {
            int colorValue = getRandomColor();
            colorsMap.put(senderId, colorValue);
        }
        return colorsMap.get(senderId);
    }

    public static int getRandomColor() {
        float[] hsv = new float[3];
        int color = Color.argb(COLOR_MAX_VALUE, random.nextInt(COLOR_MAX_VALUE), random.nextInt(
                COLOR_MAX_VALUE), random.nextInt(COLOR_MAX_VALUE));
        Color.colorToHSV(color, hsv);
        hsv[2] *= COLOR_ALPHA;
        color = Color.HSVToColor(hsv);
        return color;
    }


    /**
     * resize the RecyclerView height to make toolbar hidden
     */
    public static void resizeRecyclerViewHeight(Activity context, RecyclerView rv, SearchableToolbar searchableToolbar) {
        ViewTreeObserver vto = rv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                {
                    rv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    rv.getLayoutParams().height =
                            context.getWindow().getDecorView().getHeight() -  Util.dpToPixels(context, 45) ;
                }
            }
        });

    }


    public static boolean isScrollingUp(int n, int oldN) {
        return n < oldN;
    }

    public static boolean isScrollingDown(int n, int oldN) {
        return n > oldN;
    }
}