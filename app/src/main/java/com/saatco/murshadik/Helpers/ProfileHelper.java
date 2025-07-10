package com.saatco.murshadik.Helpers;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.saatco.murshadik.PrefUtil;
import com.saatco.murshadik.model.User;

import org.json.JSONObject;

public class ProfileHelper {

    public static final String PREFS_KEY_USER = "User";


    public static void createOrUpdateAccount(User user, Context context) {
            Gson gson = new Gson();
            String json = gson.toJson(user);
            PrefUtil.writePreferenceValue(context,PREFS_KEY_USER,json);
            PrefUtil.writeBooleanValue(context,"isLogin",true);
    }

    public static User getAccount(Context context) {
        User user = null;
        Gson gson = new Gson();
        String json = PrefUtil.getStringPref(context,PREFS_KEY_USER);

        JSONObject j;

        try {
            j = new JSONObject(json);
            user = gson.fromJson(j.toString(), User.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }


    public static boolean hasPhoto(){
        return true;
    }

    public static boolean hasAccount(Context context) {
        return ProfileHelper.getAccount(context) != null;
    }

    public static void clear(Context context) {
        PrefUtil.clearPreference(context);
    }

    public static void removeAccount(Context context) {
        PrefUtil.writeBooleanValue(context,"isLogin",false);
    }

    public static void logout(final Activity activity){

    }
}
