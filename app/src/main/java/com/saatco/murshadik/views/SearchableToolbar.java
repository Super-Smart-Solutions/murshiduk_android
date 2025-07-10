package com.saatco.murshadik.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.CustomViewSearchableToolbarBinding;
import com.saatco.murshadik.utils.Util;

public class SearchableToolbar extends FrameLayout {


    public CustomViewSearchableToolbarBinding innerViews;
    public View view;
    private int viewId = 0;
    private Activity activity;


    public SearchableToolbar(@NonNull Context context) {
        super(context);
        init(null);
    }

    public SearchableToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SearchableToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public SearchableToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        innerViews = CustomViewSearchableToolbarBinding.inflate(LayoutInflater.from(getContext()), this, true);

        try {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SearchableToolbar);

            viewId = ta.getResourceId(R.styleable.SearchableToolbar_view_container, -1);

            innerViews.searchView.setVisibility(ta.getBoolean(R.styleable.SearchableToolbar_is_contain_search_box, false) ? VISIBLE : GONE);
            innerViews.llSearchView.setVisibility(ta.getBoolean(R.styleable.SearchableToolbar_with_second_row, true) ? VISIBLE : GONE);


            innerViews.ivBtnSort.setVisibility(ta.getBoolean(R.styleable.SearchableToolbar_is_contain_sort, false) ? VISIBLE : GONE);
            innerViews.ivBtnFilter.setVisibility(ta.getBoolean(R.styleable.SearchableToolbar_is_contain_filter, false) ? VISIBLE : GONE);
            innerViews.tvTitle.setText(ta.getText(R.styleable.SearchableToolbar_toolbar_title));

            initViewsActions();
            ta.recycle();
        } catch (Exception ignored) {

        }
    }

    /**
     * class need activity to set back press btn
     */
    public void setBackPressed(Activity activity) {
        this.activity = activity;
        innerViews.ivBtnBack.setOnClickListener(v -> {
            this.activity.onBackPressed();
        });
    }

    private void initViewsActions() {

    }


}
