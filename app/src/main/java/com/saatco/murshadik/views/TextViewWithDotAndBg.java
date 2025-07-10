package com.saatco.murshadik.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.saatco.murshadik.R;
import com.saatco.murshadik.utils.Util;

public class TextViewWithDotAndBg extends LinearLayout {

    private final int ALPHA = 66;
    public LinearLayout ll_container;
    public TextView tv_label;
    public ImageView iv_circle;
    int color;

    public TextViewWithDotAndBg(@NonNull Context context) {
        super(context);
        init(null);
    }

    public TextViewWithDotAndBg(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TextViewWithDotAndBg(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public TextViewWithDotAndBg(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {

        inflate(getContext(), R.layout.custom_view_text_with_dot_and_bg, this);
        ll_container = findViewById(R.id.ll_container);
        iv_circle = findViewById(R.id.iv_circle);
        tv_label = findViewById(R.id.tv_label);


        if (attrs == null) return;

        try {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TextViewWithDotAndBg);
            setData(ta);
            ta.recycle();
        } catch (Exception ignore) {
        }
    }

    private void setData(@NonNull TypedArray ta){
        tv_label.setText(ta.getString(R.styleable.TextViewWithDotAndBg_label));
        tv_label.setTextSize(TypedValue.COMPLEX_UNIT_PX,ta.getDimension(R.styleable.TextViewWithDotAndBg_text_size, 12));
        color = ta.getColor(R.styleable.TextViewWithDotAndBg_dot_color, Color.BLUE);
        iv_circle.setImageTintList(ColorStateList.valueOf(color));
        ll_container.setBackgroundTintList(ColorStateList.valueOf(color).withAlpha(ALPHA));
    }

    public void setText(String itemValue) {
        tv_label.setText(itemValue);
    }

    public void setTextColor(int color){
        tv_label.setTextColor(color);
    }

    public void setDotColor(int color){
        iv_circle.setImageTintList(ColorStateList.valueOf(color));
        ll_container.setBackgroundTintList(ColorStateList.valueOf(color).withAlpha(ALPHA));
    }


}

