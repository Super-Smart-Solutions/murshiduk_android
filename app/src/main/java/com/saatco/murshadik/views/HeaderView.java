package com.saatco.murshadik.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.saatco.murshadik.R;

public class HeaderView extends FrameLayout {

    public TextView tv_title;
    public ImageView iv_icon;

    public HeaderView(Context context) {
        super(context);
        init(null);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        inflate(getContext(), R.layout.custom_view_header_view, this);
        tv_title = findViewById(R.id.tv_title);
        iv_icon = findViewById(R.id.iv_icon);
        try {
            int defValue = ContextCompat.getColor(getContext(), R.color.off_white);
            int defTextValue = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark);
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.HeaderView);
            tv_title.setText(ta.getString(R.styleable.HeaderView_title_HeaderView));
            tv_title.setTextColor(ta.getColor(R.styleable.HeaderView_text_color_HeaderView, defTextValue));
            tv_title.setBackgroundColor(ta.getColor(R.styleable.HeaderView_backgroundColor_HeaderView, defValue));
            iv_icon.setVisibility(ta.getBoolean(R.styleable.HeaderView_is_with_icon, true)? VISIBLE:GONE);
            ta.recycle();
        }catch (Exception Ignored){

        }
    }

    public void setTitle(String title){
        tv_title.setText(title);
    }
}
