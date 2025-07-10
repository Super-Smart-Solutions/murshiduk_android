package com.saatco.murshadik.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import com.saatco.murshadik.R;

public class AnimationUtilsLocal {

    public static void makeAnimation(View view, Context context) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationX", 100f);
        animation.setDuration(2000);
        animation.start();
    }

    public static void changeHeight(View view, int height, int duration) {
        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), height);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = val;
                view.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(duration);
        anim.start();


    }

    /**
     * animate hiding and showing views by translation on y dimension
     *
     * @param view:     the view that will move and hide or show
     * @param dy:       the distance of moving before hiding (if set 0 resit the view to it's original place
     * @param duration: the time in millisecond
     */
    public static void hideOrShowViewOnY(View view, int dy, long duration) {

        view.animate()
                .translationY(dy)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (dy != 0)
                            view.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        view.setVisibility(View.VISIBLE);
                    }
                })
                .setInterpolator(new AccelerateInterpolator(2));
    }

    /***
     * To animate view slide out from left to right
     * @param view
     */
    public static void slideToRight(View view){
        TranslateAnimation animate = new TranslateAnimation(0,view.getWidth(),0,0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }
    /***
     * To animate view slide out from right to left
     * @param view
     */
    public static void slideToLeft(View view){
        TranslateAnimation animate = new TranslateAnimation(0,-view.getWidth(),0,0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    /***
     * To animate view slide out from top to bottom
     * @param view
     */
    public static void slideToBottom(View view){
        TranslateAnimation animate = new TranslateAnimation(0,0,0,view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    /***
     * To animate view slide out from bottom to top
     * @param view
     */
    public static void slideToTop(View view){
        TranslateAnimation animate = new TranslateAnimation(0,0,0,-view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public static void fade_in(Context context, View view){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    public static void fade_out(Context context, View view){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.setEnabled(false);
        view.startAnimation(animation);

    }

    public static void fadeRotateScaleOut(Context context, View view){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_rotate_scale_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.setEnabled(false);
        view.startAnimation(animation);

    }

    public static void slid_(Context context, View view){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);

    }

    /***
     * keep animating until view visibility is gone
     * @param context
     * @param view
     */
    public static void upDownInfinite(Context context, View view){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.bounce_interpolator);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (view.getVisibility() == View.VISIBLE){
                    view.startAnimation(animation);
                }else{
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }





}
