package com.saatco.murshadik;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.utils.Consts;

import im.delight.android.webview.AdvancedWebView;

public class ReportBugActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    AdvancedWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_bug);

        webView = findViewById(R.id.webView);

        makeToolbar();

        String url = Consts.REPORT_BUG_URL + ProfileHelper.getAccount(getApplicationContext()).getPhoneNumber();

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setListener(this, this);
        webView.setMixedContentAllowed(false);

        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        webView.loadUrl(url);

        webView.addJavascriptInterface(new Object()
        {
            @JavascriptInterface           // For API 17+
            public void performClick(String strl)
            {
                Toast.makeText (ReportBugActivity.this, strl, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ReportBugActivity.this,ViewImageActivity.class);
                intent.putExtra("URL",strl);
                intent.putExtra("qa",true);
                startActivity(intent);
            }
        }, "ok");
    }

    @Override
    protected void attachBaseContext(Context newBase) {

        Context context = ContextWrapper.wrap(newBase);
        super.attachBaseContext(context);
    }

    
    public void makeToolbar(){

        ImageView backBtn = findViewById(R.id.btn_back);

        if(LanguageUtil.getLanguage(getApplicationContext()).equals("ar"))
            backBtn.setImageResource(R.drawable.arrow_right);

        backBtn.setOnClickListener(view -> finish());
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        webView.onPause();
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        webView.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        webView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    @Override
    public void onBackPressed() {
        if (!webView.onBackPressed()) { return; }
        // ...
        super.onBackPressed();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) { }

    @Override
    public void onPageFinished(String url) { }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) { }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) { }

    @Override
    public void onExternalPageRequest(String url) { }

}