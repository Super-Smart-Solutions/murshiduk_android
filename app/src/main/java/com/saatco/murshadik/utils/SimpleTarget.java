package com.saatco.murshadik.utils;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

public interface SimpleTarget<T> extends Target<T> {

    @Override
    default void onLoadStarted(@Nullable Drawable placeholder) {

    }

    @Override
    default void onLoadFailed(@Nullable Drawable errorDrawable) {

    }

    @Override
    void onResourceReady(@NonNull T resource, @Nullable Transition<? super T> transition);

    @Override
    default void onLoadCleared(@Nullable Drawable placeholder) {

    }

    @Override
    default void getSize(@NonNull SizeReadyCallback cb) {

    }

    @Override
    default void removeCallback(@NonNull SizeReadyCallback cb) {

    }

    @Override
    default void setRequest(@Nullable Request request) {

    }

    @Nullable
    @Override
    default Request getRequest() {
        return null;
    }

    @Override
    default void onStart() {

    }

    @Override
    default void onStop() {

    }

    @Override
    default void onDestroy() {

    }
}

