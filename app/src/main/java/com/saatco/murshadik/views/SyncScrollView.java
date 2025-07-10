package com.saatco.murshadik.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class SyncScrollView extends ScrollView {

    boolean isKeyboardOpened = false;

    public SyncScrollView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public SyncScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SyncScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public SyncScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        setScrollBarSize(0);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            //r will be populated with the coordinates of your view that area still visible.
            getRootView().getWindowVisibleDisplayFrame(r);

            int heightDiff = getRootView().getRootView().getHeight() - r.height();
            // if more than 25% of the screen, its probably a keyboard...
            isKeyboardOpened = heightDiff > 0.25 * getRootView().getRootView().getHeight();
        });

        if (isKeyboardOpened) return;

        scrollBy(dxConsumed, dyConsumed);
    }
}
