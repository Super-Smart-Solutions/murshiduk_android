package com.saatco.murshadik;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtil {

    private static SharedPreferences getDefaultSharedPreferences(final Context context) {
       // return context.getSharedPreferences("USER_DETAILS", 0);
        return context.getSharedPreferences("USER_DETAILS",0);
    }


    public static void writePreferenceValue(Context context, String prefsKey, String prefsValue) {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putString(prefsKey, prefsValue);
        editor.apply();

    }
    public static void writeBooleanValue(Context context, String prefsKey, boolean prefsValue) {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putBoolean(prefsKey, prefsValue);
        editor.apply();
    }
    public static void writeIntValue(Context context, String prefsKey, int prefsValue) {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putInt(prefsKey, prefsValue);
        editor.apply();
    }
    public static void removePreferenceValue(Context context, String key) {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.remove(key);
        editor.apply();
    }
    public static boolean getBoolean(final Context context, final String key) {
        return getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    public static int getInteger(final Context context, final String key) {
        return getDefaultSharedPreferences(context).getInt(key, 0);
    }

    public static void clearPreference(Context context){
        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.clear().commit();
    }

    public static void clearPreferenceByKey(Context context,String key){
        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.remove(key).commit();
    }

    public static String getStringPref(final Context context, final String key) {
        return getDefaultSharedPreferences(App.getInstance()).getString(key,"");
    }
}
