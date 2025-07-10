package com.saatco.murshadik;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class ContextWrapper extends android.content.ContextWrapper {

    public ContextWrapper(Context base) {
        super(base);
    }

    public static ContextWrapper wrap(Context context) {

        String newLocale = PrefUtil.getStringPref(context,"lang");

        Resources res = context.getResources();
        Configuration configuration = res.getConfiguration();
        configuration.setLocale(new Locale(newLocale));

        Locale locale = new Locale(newLocale);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        context = context.createConfigurationContext(configuration);

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.N){
            configuration.locale = new Locale(newLocale);
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }

        return new ContextWrapper(context);

    }

}