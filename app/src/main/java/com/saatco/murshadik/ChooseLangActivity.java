package com.saatco.murshadik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ChooseLangActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_lang);

        TextView tvEng = findViewById(R.id.btnEng);
        TextView tvAr = findViewById(R.id.btnAr);

        tvEng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LanguageUtil.saveLanguage(getApplicationContext(),"en");

                LocaleHelper.setLocale(ChooseLangActivity.this,LanguageUtil.getLanguage(getApplicationContext()));

                Intent intent = new Intent(ChooseLangActivity.this,InitialActivity.class);
                startActivity(intent);
                finish();
            }
        });



        tvAr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LanguageUtil.saveLanguage(getApplicationContext(),"ar");

                LocaleHelper.setLocale(ChooseLangActivity.this,LanguageUtil.getLanguage(getApplicationContext()));

                Intent intent = new Intent(ChooseLangActivity.this,InitialActivity.class);
                startActivity(intent);
                finish();

            }
        });


    }
}
