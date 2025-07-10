package com.saatco.murshadik.Helpers;

import com.saatco.murshadik.App;
import com.saatco.murshadik.PrefUtil;

public class TokenHelper {

    public static final String PREFS_KEY_TOKEN = "access_token";

    public static void createToken(String token) {
        PrefUtil.writePreferenceValue(App.getInstance(),PREFS_KEY_TOKEN,token);
    }

    public static String getToken() {
        return PrefUtil.getStringPref(App.getInstance(),PREFS_KEY_TOKEN);
    }

    public static String getBearerToken() {
        return "Bearer " + getToken();
    }
}
