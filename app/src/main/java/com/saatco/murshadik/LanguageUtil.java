package com.saatco.murshadik;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LanguageUtil {

    public static void changeLanguage(Context context){

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
//            // LanguageUtil.changeLanguage(getApplicationContext());
//            LocaleHelper.setLocale(context, LanguageUtil.getLanguage(context));
//            return;
//        }

        String languageToLoad = PrefUtil.getStringPref(context,"lang");


        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();
        Locale locale = new Locale(languageToLoad, "AE");
        Locale.setDefault(locale);
        conf.setLocale(locale);
        context.createConfigurationContext(conf);


        DisplayMetrics dm = res.getDisplayMetrics();
        conf.setLocale(locale);
        res.updateConfiguration(conf, dm);

    }

    public static void saveLanguage(Context context, String language){

           PrefUtil.writePreferenceValue(context,"lang",language);

    }

    public static String getLanguage(Context context){

        return PrefUtil.getStringPref(context,"lang");
    }

    public static String getTextByLanguage(Context context,String nameEng,String nameAr){

        String code = PrefUtil.getStringPref(context,"lang");

        if(code.equals("ar"))
            return nameAr;

        return nameEng;
    }


}
