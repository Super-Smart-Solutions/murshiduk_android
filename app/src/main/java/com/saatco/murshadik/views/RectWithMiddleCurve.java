package com.saatco.murshadik.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.saatco.murshadik.R;

public class RectWithMiddleCurve extends View {
    private final Paint paint;
    public RectWithMiddleCurve(Context context) {
        super(context);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(getContext().getColor(R.color.themeColor));


        super.onDraw(canvas);
    }
}
