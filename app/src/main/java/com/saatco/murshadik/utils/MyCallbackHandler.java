package com.saatco.murshadik.utils;

public interface MyCallbackHandler<T> {
    void onResponse(T msg);
    default void onPosition(int position){}
}
