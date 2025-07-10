package com.saatco.murshadik.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.saatco.murshadik.R;

public class TextViewWithIcon extends LinearLayout {

    private final int ALPHA = 66;
    public LinearLayout ll_container;
    public TextView tv_label;
    public ImageView iv_icon;
    int icon;

    public TextViewWithIcon(@NonNull Context context) {
        super(context);
        init(null);
    }

    public TextViewWithIcon(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TextViewWithIcon(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public TextViewWithIcon(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {

        inflate(getContext(), R.layout.custom_view_text_with_icon, this);
        ll_container = findViewById(R.id.ll_container);
        iv_icon = findViewById(R.id.iv_icon);
        tv_label = findViewById(R.id.tv_label);


        if (attrs == null) return;

        try {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TextViewWithIcon);
            setData(ta);
            ta.recycle();
        } catch (Exception ignore) {
        }
    }

    private void setData(@NonNull TypedArray ta) {
        tv_label.setText(ta.getString(R.styleable.TextViewWithIcon_icon_text_label));
        tv_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimension(R.styleable.TextViewWithIcon_icon_text_size, 11));
        tv_label.setTextColor(ta.getColor(R.styleable.TextViewWithIcon_icon_text_color, Color.BLACK));
        icon = ta.getResourceId(R.styleable.TextViewWithIcon_icon, Color.TRANSPARENT);
        int icon_tint = ta.getColor(R.styleable.TextViewWithIcon_icon_color, Color.GRAY);
        boolean is_multi_line = ta.getBoolean(R.styleable.TextViewWithIcon_is_multi_line, false);
        setMultiLine(is_multi_line);
        iv_icon.setImageResource(icon);
        iv_icon.setImageTintList(ColorStateList.valueOf(icon_tint));
    }

    public void setMultiLine(boolean is_multi_line) {
        tv_label.setSingleLine(!is_multi_line);
        tv_label.setEllipsize(TextUtils.TruncateAt.END);

    }

    public void setText(String itemValue) {
        tv_label.setText(itemValue);
    }

    public void setTextColor(int color) {
        tv_label.setTextColor(color);
    }

    public void setIcon(@DrawableRes int icon, int icon_tint) {
        iv_icon.setImageResource(icon);
        iv_icon.setImageTintList(ColorStateList.valueOf(icon_tint));
    }


}

