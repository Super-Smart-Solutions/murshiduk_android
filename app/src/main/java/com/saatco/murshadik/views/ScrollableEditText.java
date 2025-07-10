package com.saatco.murshadik.views;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.CustomViewScrollableEditTextBinding;
import com.saatco.murshadik.databinding.CustomViewSearchableToolbarBinding;
import com.saatco.murshadik.utils.Util;

public class ScrollableEditText extends FrameLayout {
    private static final int MOVE_OFFSET = 15;
    private static final int MOVE_OFFSET_Y = 10;

    public CustomViewScrollableEditTextBinding innerViews;

    public ScrollableEditText(@NonNull Context context) {
        super(context);
        init(null);
    }

    public ScrollableEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ScrollableEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public ScrollableEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {

        innerViews = CustomViewScrollableEditTextBinding.inflate(LayoutInflater.from(getContext()), this, true);

        if (attrs == null) return;

        try {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ScrollableEditText);
            int minHeight = (int) ta.getDimension(R.styleable.ScrollableEditText_min_height, 100);

            String hint = ta.getString(R.styleable.ScrollableEditText_edit_text_hint);
            innerViews.editText.setHint(hint);
            innerViews.tvTitle.setText(hint);

            if (minHeight >= 0)
                innerViews.editText.setMinHeight(minHeight);
            else innerViews.editText.getLayoutParams().height = minHeight;

            initEditText();

            ta.recycle();
        } catch (Exception ignore) {
        }
    }

    void initEditText() {

        innerViews.tvTitle.setVisibility(GONE);
        innerViews.tvTitle.animate()
                .translationY(Util.dpToPixels(getContext(), MOVE_OFFSET_Y))
                .translationX(Util.dpToPixels(getContext(), MOVE_OFFSET))
                .setDuration(0)
                .start();
        innerViews.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    innerViews.tvTitle.animate()
                            .translationY(Util.dpToPixels(getContext(), MOVE_OFFSET_Y))
                            .translationX(Util.dpToPixels(getContext(), MOVE_OFFSET))
                            .setDuration(50)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    if (charSequence.toString().isEmpty())
                                        innerViews.tvTitle.setVisibility(GONE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {
                                }
                            })
                            .start();
                } else if (innerViews.tvTitle.getVisibility() == GONE) {
                    innerViews.tvTitle.setVisibility(VISIBLE);
                    innerViews.tvTitle.animate().translationX(0).translationY(0).setDuration(50).start();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public EditText getEditTextView() {
        return innerViews.editText;
    }

    public String getText() {
        return innerViews.editText.getText().toString();
    }

    public void setText(String text) {
        innerViews.editText.setText(text);
    }


}
