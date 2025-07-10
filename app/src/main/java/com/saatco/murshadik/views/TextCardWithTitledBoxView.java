package com.saatco.murshadik.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.CustomViewTextCardWithTitledBoxBinding;

public class TextCardWithTitledBoxView extends FrameLayout {

    public CustomViewTextCardWithTitledBoxBinding binding;

    // Constructor methods
    public TextCardWithTitledBoxView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public TextCardWithTitledBoxView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TextCardWithTitledBoxView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public TextCardWithTitledBoxView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    // Method to initialize the view
    public void init(@Nullable AttributeSet attrs) {
        binding = CustomViewTextCardWithTitledBoxBinding.inflate(LayoutInflater.from(getContext()), this, true);

        try {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TextCardWithTitledBoxView);

            String title = ta.getString(R.styleable.TextCardWithTitledBoxView_text_card_title);
            String text = ta.getString(R.styleable.TextCardWithTitledBoxView_text_card_text);
            int icon = ta.getResourceId(R.styleable.TextCardWithTitledBoxView_text_card_icon, -1);

            binding.tvTitle.setText(title);

            Drawable img = ResourcesCompat.getDrawable(getResources(), icon, null);
            if (img != null) {
                img.setBounds(0, 0, 40, 40);
            }
            binding.tvTitle.setCompoundDrawables(null, null, img, null);

            binding.tvText.setText(text);
            ta.recycle();
        } catch (Exception e) {
            Log.d("TAG", "init: " + e.getMessage());
        }

    }

    // Method to set the title of the card
    public void setTitle(String title) {
        binding.tvTitle.setText(title);
    }

    // Method to set the text of the card
    public void setText(String text) {
        binding.tvText.setText(text);
    }

    // Method to set the icon of the card
    public void setIcon(@DrawableRes int icon) {
        binding.tvTitle.setCompoundDrawables(null, null, ResourcesCompat.getDrawable(getResources(), icon, null), null);
    }

}
