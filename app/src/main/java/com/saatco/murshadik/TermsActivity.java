package com.saatco.murshadik;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.saatco.murshadik.utils.Consts;

public class TermsActivity extends BaseActivity {

    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        webView = findViewById(R.id.webView);
        makeToolbar();

        String url = Consts.TERMS_URL;

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);

    }

    @Override
    protected void attachBaseContext(Context newBase) {

        Context context = ContextWrapper.wrap(newBase);
        super.attachBaseContext(context);
    }


    @Override
    public void onBackPressed(){ finish(); }

    public void makeToolbar(){

        ImageView backBtn = findViewById(R.id.btn_back);

        if(LanguageUtil.getLanguage(getApplicationContext()).equals("ar"))
            backBtn.setImageResource(R.drawable.arrow_right);

        backBtn.setOnClickListener(view -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
