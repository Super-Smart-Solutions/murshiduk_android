package com.saatco.murshadik.utils;

import android.app.Application;
import android.content.Context;


/**
 * Created by Devlomi on 13/08/2017.
 */

public class MyApp extends Application {
    private static final MyApp mApp = null;
    private static String currentChatId = "";
    private static boolean chatActivityVisible;
    private static boolean phoneCallActivityVisible;
    private static boolean baseActivityVisible;
    private static boolean isCallActive = false;

    public static boolean isChatActivityVisible() {
        return chatActivityVisible;
    }

    public static String getCurrentChatId() {
        return currentChatId;
    }

    public static void chatActivityResumed(String chatId) {
        chatActivityVisible = true;
        currentChatId = chatId;
    }

    public static void chatActivityPaused() {
        chatActivityVisible = false;
        currentChatId = "";
    }

    public static boolean isPhoneCallActivityVisible() {
        return phoneCallActivityVisible;
    }

    public static void phoneCallActivityResumed() {
        phoneCallActivityVisible = true;
    }

    public static void phoneCallActivityPaused() {
        phoneCallActivityVisible = false;
    }


    public static boolean isBaseActivityVisible() {
        return baseActivityVisible;
    }

    public static void baseActivityResumed() {
        baseActivityVisible = true;
    }

    public static void baseActivityPaused() {
        baseActivityVisible = false;
    }


    public static void setCallActive(boolean mCallActive) {
        isCallActive = mCallActive;
    }

    public static boolean isIsCallActive() {
        return isCallActive;
    }


    @Override
    public void onCreate() {
        super.onCreate();


    }

    public static Context context() {
        return mApp.getApplicationContext();
    }

    //to run multi dex
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

}
