package com.saatco.murshadik.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.CustomViewHeaderViewWithLikeButtonsAndTextBinding;

public class HeaderViewWithLikeButtonsAndText extends FrameLayout {


    public CustomViewHeaderViewWithLikeButtonsAndTextBinding innerViews;
    int likeColor, activeLikeColor;
    public HeaderViewWithLikeButtonsAndText(Context context) {
        super(context);
        init(null);
    }

    public HeaderViewWithLikeButtonsAndText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HeaderViewWithLikeButtonsAndText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public HeaderViewWithLikeButtonsAndText(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        innerViews = CustomViewHeaderViewWithLikeButtonsAndTextBinding.inflate(LayoutInflater.from(getContext()), this, true);

        try {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.HeaderViewWithLikeButtonsAndText);
            setAttrs(ta);

            ta.recycle();
        }catch (Exception Ignored){

        }
    }

    private void setAttrs(TypedArray ta){
        int defValue = ContextCompat.getColor(getContext(), R.color.off_white);
        innerViews.tvTitle.setText(ta.getString(R.styleable.HeaderViewWithLikeButtonsAndText_title_Header));
        innerViews.tvTitle.setBackgroundColor(ta.getColor(R.styleable.HeaderViewWithLikeButtonsAndText_backgroundColor_Header, defValue));
        innerViews.llLikes.setBackgroundColor(ta.getColor(R.styleable.HeaderViewWithLikeButtonsAndText_backgroundColor_Header, defValue));
        innerViews.ivIcon.setVisibility(ta.getBoolean(R.styleable.HeaderViewWithLikeButtonsAndText_is_icon_shown, true)? VISIBLE:GONE);
        innerViews.ivIcon.setImageResource(ta.getResourceId(R.styleable.HeaderViewWithLikeButtonsAndText_header_icon, R.drawable.ic_header_view));
        innerViews.tvText.setText(ta.getText(R.styleable.HeaderViewWithLikeButtonsAndText_icon_text));
        int tintColor = ta.getColor(R.styleable.HeaderViewWithLikeButtonsAndText_icon_tint_color, defValue);
        setImageViewTintColor(innerViews.ivIcon, tintColor, defValue);
        likeColor = ta.getColor(R.styleable.HeaderViewWithLikeButtonsAndText_likes_tint_color, ContextCompat.getColor(getContext(), com.vanillaplacepicker.R.color.gray5B));
        activeLikeColor = ta.getColor(R.styleable.HeaderViewWithLikeButtonsAndText_likes_tint_active_color, ContextCompat.getColor(getContext(), R.color.themeColor));

        innerViews.imgLikeBtn.setImageTintList(ColorStateList.valueOf(likeColor));
        innerViews.imgDislikeBtn.setImageTintList(ColorStateList.valueOf(likeColor));

        boolean isWithLikeButtons = ta.getBoolean(R.styleable.HeaderViewWithLikeButtonsAndText_is_with_like_buttons, true);
        if (!isWithLikeButtons) innerViews.llLikes.setVisibility(GONE);
    }

    private void setImageViewTintColor(ImageView imageView, int color, int defColor){
        if (color != defColor) {
            imageView.setImageTintList(ColorStateList.valueOf(color));
        }
    }

    public void setLikesNum(String num){
        innerViews.tvLikeCounter.setText(num);
    }

    public void setDislikesNum(String num){
        innerViews.tvDislikeCounter.setText(num);
    }

    public void setLikeActive(){
        innerViews.imgLikeBtn.setImageTintList(ColorStateList.valueOf(activeLikeColor));
        innerViews.imgDislikeBtn.setImageTintList(ColorStateList.valueOf(likeColor));
    }

    public void setDislikeActive(){
        innerViews.imgLikeBtn.setImageTintList(ColorStateList.valueOf(likeColor));
        innerViews.imgDislikeBtn.setImageTintList(ColorStateList.valueOf(activeLikeColor));
    }

    public void resetLike(){
        innerViews.imgLikeBtn.setImageTintList(ColorStateList.valueOf(likeColor));
    }

    public void resetDislike(){
        innerViews.imgDislikeBtn.setImageTintList(ColorStateList.valueOf(likeColor));
    }

    public void setDateText(String dateText){
        innerViews.tvText.setText(dateText);
    }
}
