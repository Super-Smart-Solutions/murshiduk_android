package com.saatco.murshadik.utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.PrecomputedText;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.transform.Result;

public class AsyncTaskUtil {
    public static class NewTask{
        NewTask(){
            ExecutorService executors = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executors.execute(() -> {
                //background work

                handler.post(()->{
                    //UI Thread work here

                });
            });
        }

    }
}
