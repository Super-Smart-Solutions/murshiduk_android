package com.saatco.murshadik.backgroundtintlayouts;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

public class RelativeLayoutWithBackgroundTint extends RelativeLayout {
    public RelativeLayoutWithBackgroundTint(Context context) {
        super(context);
        init(context,null);
    }

    public RelativeLayoutWithBackgroundTint(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public RelativeLayoutWithBackgroundTint(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        new TintHelper(context, this, attrs);
    }


}
