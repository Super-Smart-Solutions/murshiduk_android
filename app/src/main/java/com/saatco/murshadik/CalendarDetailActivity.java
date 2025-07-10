package com.saatco.murshadik;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.ybq.android.spinkit.SpinKitView;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.api.APIClient;

import java.net.MalformedURLException;
import java.net.URL;


public class CalendarDetailActivity extends BaseActivity {

    WebView webView;
    SpinKitView spinKitView;

    int id;
    boolean isCoffee = false;
    double value = 0.0;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_detail);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        webView = findViewById(R.id.webView);


        id = getIntent().getIntExtra("CAT_ID", 0);
        isCoffee = getIntent().getBooleanExtra("is_coffee", false);
        value = getIntent().getDoubleExtra("value", 0);

        ToolbarHelper.setToolBar(this, "", findViewById(R.id.toolbarTrans));

        String url = isCoffee ? APIClient.baseUrl + "Mobile/Index/" + id + "?val=" + value : APIClient.baseUrl + "Mobile/Index/" + id;

        spinKitView = findViewById(R.id.spin_kit);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.loadUrl(url);
        // webView.addJavascriptInterface(new WebViewJavascriptInterface(this), "showParentCategoryDetailsMessageHandler");

        final boolean[] triggerLoadPdf = {false};
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                spinKitView.setVisibility(View.GONE);
                if (view.getTitle().equals("")) {
                    view.reload();
                }

                String jsString = "var iframe = document.getElementById('pdfUrl');" +
                        "iframe.src;";
                webView.evaluateJavascript(jsString, s -> {
                    Log.v("respons js", s);
                    if (s == null || s.equals("null") || triggerLoadPdf[0]) return;
                    triggerLoadPdf[0] = true;

                    String url1 = s.replaceAll("\"", "");
                    String fileName = url1.substring(url1.lastIndexOf('/') + 1);

                    webView.loadData( getHTMLData(url1, fileName), "text/html", "UTF-8");


                });
            }
        });


        webView.setWebChromeClient(new MyChrome());


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                spinKitView.setVisibility(View.GONE);
            }
        }, 2000);

    }
    private String getHTMLData(String url, String fileName) {
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<head>");
        html.append("<link rel=stylesheet href='css/style.css'>");
        html.append("</head>");
        html.append("<body>");
        html.append("\n" +
                "            <div id=\"adobe-dc-view\"></div>\n" +
                "             <script src=\"https://acrobatservices.adobe.com/view-sdk/viewer.js\"></script>\n" +
                "             <script type=\"text/javascript\">\n" +
                "                document.addEventListener(\"adobe_dc_view_sdk.ready\", function()\n" +
                "                {\n" +
                "                    var adobeDCView = new AdobeDC.View({clientId: \"01f1f1c87bdf415ab63bb2fd658979a6\", divId: \"adobe-dc-view\"});\n" +
                "                    adobeDCView.previewFile(\n" +
                "                   {\n" +
                "                      content:   {location: {url: \"" + url + "\"}},\n" +
                "                      metaData: {fileName: \"" + fileName + "\"}\n" +
                "                   }, {embedMode: \"IN_LINE\"});\n" +
                "                });\n" +
                "             </script>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    private class MyChrome extends WebChromeClient {

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }


}

