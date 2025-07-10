package com.saatco.murshadik;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.github.ybq.android.spinkit.SpinKitView;

import java.util.Locale;

public class PDFViewerActivity extends BaseActivity {

    ImageView backButton;
    WebView webView;
    SpinKitView spinKitView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);


        backButton = findViewById(R.id.btn_back);

        spinKitView = findViewById(R.id.spin_kit);
        webView =  findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);


        backButton.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume(){
        super.onResume();

        String format = "https://drive.google.com/viewerng/viewer?embedded=true&url=%s";
        String fullPath = String.format(Locale.ENGLISH, format, getIntent().getStringExtra("file_uri"));
        Log.v("PATH",fullPath);
        webView.loadUrl(getIntent().getStringExtra("file_uri"));
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                spinKitView.setVisibility(View.GONE);
                if (view.getTitle().equals("")) {
                    spinKitView.setVisibility(View.VISIBLE);
                    view.reload();
                }
            }
        });
    }


    @Override
    protected void attachBaseContext (Context base){
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();


    }
}

