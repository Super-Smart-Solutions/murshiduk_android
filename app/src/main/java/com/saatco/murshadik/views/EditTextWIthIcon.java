package com.saatco.murshadik.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.saatco.murshadik.databinding.CustomViewEditTextWithBorderNameBinding;

public class EditTextWIthIcon extends androidx.appcompat.widget.AppCompatEditText {

    public CustomViewEditTextWithBorderNameBinding innerViews;


    public EditTextWIthIcon(Context context) {
        super(context);
    }

    public EditTextWIthIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextWIthIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(@Nullable AttributeSet attrs, Context context) {
        innerViews = CustomViewEditTextWithBorderNameBinding.inflate(LayoutInflater.from(getContext()), null, false);

    }
}
