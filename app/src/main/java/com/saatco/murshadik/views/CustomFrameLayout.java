package com.saatco.murshadik.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.saatco.murshadik.R;

public class CustomFrameLayout extends FrameLayout {

    GradientDrawable border;
    int borderStroke;

    public CustomFrameLayout(@NonNull Context context) {
        super(context);
        init(null);
    }

    public CustomFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public CustomFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        border = new GradientDrawable();
        border.setColor(0xffffff);

        try  {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFrameLayout);
            float borderWidth = ta.getDimension(R.styleable.CustomFrameLayout_border_stroke, 1);
            float borderRadius = ta.getDimension(R.styleable.CustomFrameLayout_border_radius, 0);
            int borderBg = ta.getColor(R.styleable.CustomFrameLayout_border_bg, 0x000000);
            int color =  ta.getColor(R.styleable.CustomFrameLayout_border_color, 0xffffff);

            borderStroke = (int) borderWidth;
            setBorderStrokeAndColor(borderStroke, color);
            setBorderRadius(borderRadius);
            border.setColor(borderBg);
            setBackground(border);
            ta.recycle();
        }catch (Exception ignored){}
    }

    public void setBorderStrokeAndColor(int stroke, int color){
        border.setStroke(stroke, color);
        setBackground(border);
    }

    public void setBorderColor(int color){
        border.setStroke(borderStroke, color);
        setBackground(border);
    }

    public void setBorderRadius(float radius){
        border.setCornerRadius(radius);
        setBackground(border);
    }

}
