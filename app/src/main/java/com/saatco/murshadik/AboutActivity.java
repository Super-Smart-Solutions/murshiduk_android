package com.saatco.murshadik;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.saatco.murshadik.Helpers.FirebaseObjectHelper;
import com.saatco.murshadik.databinding.ActivityAboutBinding;

public class AboutActivity extends BaseActivity {


    WebView webView;
    ActivityAboutBinding binding;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        webView = binding.webView;
        makeToolbar();

        String url = "https://mw.saatco.net/Mobile/AboutUs";

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



    public void makeToolbar(){

        ImageView backBtn = binding.btnBack;

        if(LanguageUtil.getLanguage(getApplicationContext()).equals("ar"))
            backBtn.setImageResource(R.drawable.arrow_right);

        backBtn.setOnClickListener(view -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseObjectHelper.updateLastTerminate(getApplicationContext());
    }
}
