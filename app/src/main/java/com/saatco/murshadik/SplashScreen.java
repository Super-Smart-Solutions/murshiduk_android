package com.saatco.murshadik;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Locale;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isFirstTime = PrefUtil.getBoolean(getApplicationContext(),"is_first_name");

        if(!isFirstTime) {
            LanguageUtil.saveLanguage(getApplicationContext(),Locale.getDefault().getLanguage());
        }

               // LanguageUtil.changeLanguage(getApplicationContext());
               LocaleHelper.setLocale(SplashScreen.this,LanguageUtil.getLanguage(getApplicationContext()));

                startActivity(new Intent(SplashScreen.this, InitialActivity.class));
                finish();


            }

}
