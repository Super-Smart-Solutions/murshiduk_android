package com.saatco.murshadik.views;

import android.animation.Animator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.CustomViewEditTextWithBorderNameBinding;
import com.saatco.murshadik.utils.Util;

public class EditWithBorderTitle extends FrameLayout {


    //    private static final int MOVE_OFFSET = 10;
    private static final int DEFAULT_ICON = R.drawable.arrow;
    private static final int DEFAULT_COLOR = Color.TRANSPARENT;
    private static final int MOVE_OFFSET = 20;

    String title, content;

    boolean isEditText = true, isSpinner = false;
    boolean arrow_icon_shown = true;

    boolean isPrefixShown = false;


    public CustomViewEditTextWithBorderNameBinding innerViews;


    public EditWithBorderTitle(@NonNull Context context) {
        super(context);
        init(null);
    }

    public EditWithBorderTitle(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EditWithBorderTitle(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public EditWithBorderTitle(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {

        innerViews = CustomViewEditTextWithBorderNameBinding.inflate(LayoutInflater.from(getContext()), this, true);

        if (attrs == null) return;

        try {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.EditWithBorderTitle);
            innerViews.tvContent.setText(ta.getString(R.styleable.EditWithBorderTitle_view_content));
            innerViews.etContent.setHint(ta.getString(R.styleable.EditWithBorderTitle_title));
            innerViews.tvTitle.setText(ta.getString(R.styleable.EditWithBorderTitle_title));


            if (innerViews.etContent.getHint() == null) {
                innerViews.etContent.setHint(attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "hint"));
            }

            int ems = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "ems", -1);
            if (ems != -1) {
                innerViews.etContent.setEms(ems);
            }

            int icon = ta.getResourceId(R.styleable.EditWithBorderTitle_arrow_icon, DEFAULT_ICON);
            int iconTint = ta.getColor(R.styleable.EditWithBorderTitle_arrow_icon_tint, DEFAULT_COLOR);

            @DrawableRes int prefixIcon = ta.getResourceId(R.styleable.EditWithBorderTitle_prefix_icon, 0);
            if (prefixIcon != 0) {
                innerViews.ivPrefix.setImageResource(prefixIcon);
                innerViews.ivPrefix.setVisibility(VISIBLE);
            }
            else {
                innerViews.ivPrefix.setVisibility(GONE);
            }

            int prefixIconTint = ta.getColor(R.styleable.EditWithBorderTitle_prefix_icon_tint, DEFAULT_COLOR);
            if (prefixIconTint != DEFAULT_COLOR)
                innerViews.ivPrefix.setImageTintList(ColorStateList.valueOf(prefixIconTint));


            isEditText = ta.getBoolean(R.styleable.EditWithBorderTitle_is_edit_text, true);
            isSpinner = ta.getBoolean(R.styleable.EditWithBorderTitle_is_spinner, false);
            arrow_icon_shown = ta.getBoolean(R.styleable.EditWithBorderTitle_arrow_icon_shown, true);


            if (isEditText) {
                initEditTextMode();
            } else if (isSpinner) {
                initSpinnerMode();
            } else {
                innerViews.etContent.setVisibility(GONE);
                innerViews.spinner.setVisibility(GONE);
                initArrowIconIfChange(icon, iconTint);
                innerViews.ivArrow.setVisibility(arrow_icon_shown ? VISIBLE : GONE);
            }


            if (ta.getBoolean(R.styleable.EditWithBorderTitle_is_multi_line_text, false)) {
                innerViews.etContent.getLayoutParams().height = Util.dpToPixels(getContext(), 130);
            } else {
                innerViews.etContent.setMaxLines(1);
            }
            ta.recycle();
        } catch (Exception exception) {
            Log.e("EditWithBorderTitle", "init: ", exception);
        }
    }

    public void initArrowIconIfChange(@DrawableRes int icon, int iconTint) {
        if (DEFAULT_ICON == icon) return;
        innerViews.ivArrow.setImageResource(icon);
        innerViews.ivArrow.setRotation(0);
        innerViews.ivArrow.setImageTintList(ColorStateList.valueOf(iconTint));
    }

    private void initSpinnerMode() {
        innerViews.spinner.setVisibility(VISIBLE);
        innerViews.tvTitle.setVisibility(VISIBLE);
        innerViews.etContent.setVisibility(GONE);
    }

    private void initEditTextMode() {
        innerViews.tvContent.setVisibility(GONE);
        innerViews.ivArrow.setVisibility(GONE);
        innerViews.tvTitle.animate()
                .translationY(Util.dpToPixels(getContext(), MOVE_OFFSET))
                .translationX(Util.dpToPixels(getContext(), MOVE_OFFSET))
                .setDuration(0)
                .start();
        innerViews.etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    innerViews.tvTitle.animate()
                            .translationY(Util.dpToPixels(getContext(), MOVE_OFFSET))
                            .translationX(Util.dpToPixels(getContext(), MOVE_OFFSET))
                            .setDuration(100)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(@NonNull Animator animator) {
                                }

                                @Override
                                public void onAnimationEnd(@NonNull Animator animator) {
                                    if (charSequence.toString().isEmpty())
                                        innerViews.tvTitle.setVisibility(GONE);
                                }

                                @Override
                                public void onAnimationCancel(@NonNull Animator animator) {
                                }

                                @Override
                                public void onAnimationRepeat(@NonNull Animator animator) {
                                }
                            })
                            .start();
                } else if (innerViews.tvTitle.getVisibility() == GONE) {
                    innerViews.tvTitle.setVisibility(VISIBLE);
                    innerViews.tvTitle.animate().translationY(0).translationX(0).setDuration(50).start();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                clearErrorInput();
            }
        });
    }


    public void setTitle(String title) {
        this.title = title;
        innerViews.tvTitle.setText(title);
        invalidate();
        requestLayout();
    }

    public void setContent(String content) {
        this.content = content;
        innerViews.tvContent.setText(content);
        clearErrorInput();
    }


    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }


    public CharSequence getText() {
        if (isEditText) {
            return innerViews.etContent.getText();
        }
        return innerViews.tvContent.getText();
    }

    public void setText(String itemValue) {
        if (isEditText) {
            innerViews.etContent.setText(itemValue);
        } else {
            innerViews.tvContent.setText(itemValue);
        }

        invalidate();
        requestLayout();
    }

    public void setErrorInput(String msg) {
        innerViews.flBorderLayout.setOutlineAmbientShadowColor(Color.RED);
        innerViews.flBorderLayout.setOutlineSpotShadowColor(Color.RED);

        innerViews.errMsg.setVisibility(VISIBLE);
        innerViews.errMsg.setText(msg);

        invalidate();
        requestLayout();
    }

    public void clearErrorInput() {
        innerViews.flBorderLayout.setOutlineAmbientShadowColor(Color.BLACK);
        innerViews.flBorderLayout.setOutlineSpotShadowColor(Color.BLACK);
        innerViews.errMsg.setVisibility(GONE);

        invalidate();
        requestLayout();
    }

    public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
        innerViews.etContent.setCustomSelectionActionModeCallback(actionModeCallback);
    }

    public void setTextIsSelectable(boolean selectable) {
        innerViews.etContent.setTextIsSelectable(selectable);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        innerViews.getRoot().setOnClickListener(onClickListener);
        innerViews.etContent.setOnClickListener(onClickListener);
        innerViews.etContent.setClickable(false);
        innerViews.etContent.setFocusable(false);
        innerViews.etContent.setFocusableInTouchMode(false);
        innerViews.etContent.setCursorVisible(false);
    }
}
