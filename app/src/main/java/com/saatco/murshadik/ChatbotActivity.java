package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.Consts;


import im.delight.android.webview.AdvancedWebView;

public class ChatbotActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    AdvancedWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot2);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        webView = findViewById(R.id.webView);
        makeToolbar("");
        User user = ProfileHelper.getAccount(getApplicationContext());
        String url = Consts.CHAT_BOT_URL + user.getFirstName() +" "+ user.getLastName() + "&userid=" + user.getId();

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setListener(this, this);
        webView.setMixedContentAllowed(false);
        webView.addJavascriptInterface(new WebViewJavascriptInterface(this), "showArticle");
        webView.addJavascriptInterface(new ConsultantsJavascriptInterface(this), "showConsultant");
        webView.addJavascriptInterface(new TwitterJavascriptInterface(this), "showTweet");
        webView.addJavascriptInterface(new CloseJavascriptInterface(this), "closeChat");

        webView.setWebViewClient(new WebViewClient()
        {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        webView.loadUrl(url);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }


    public class WebViewJavascriptInterface {

        private final Context context;

        public WebViewJavascriptInterface(Context _context) {
            context = _context;
        }

        @JavascriptInterface
        public void postMessage(String message) {

            Intent intent = new Intent(getApplicationContext(), CalendarDetailActivity.class);
            intent.putExtra("CAT_ID",Integer.parseInt(message));
            startActivity(intent);


        }

    }

    public class ConsultantsJavascriptInterface {
        private final Context context;

        public ConsultantsJavascriptInterface(Context _context) {
            context = _context;
        }

        @JavascriptInterface
        public void postMessage(String message) {

            if(message != null && !message.equals("")) {
                User user = new User();
                user.setChatId(message);

                Intent intent = new Intent(getApplicationContext(), ChatActivityNew.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("USER", user);
                intent.putExtra("is_cb", true);
                startActivity(intent);
            }
        }
    }

    public class TwitterJavascriptInterface {
        private final Context context;

        public TwitterJavascriptInterface(Context _context) {
            context = _context;
        }

        @JavascriptInterface
        public void postMessage(String message) {

            Log.v("TWITTER",message);

            if(message.contains("qna:")) {

                Intent intent = new Intent(getApplicationContext(), AnswerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("ID", Integer.parseInt(message.replace("qna:","")));
                startActivity(intent);

            } else {

                Intent intent = null;
                try {
                    // get the Twitter app if possible
                    getPackageManager().getPackageInfo("com.twitter.android", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    // no Twitter app, revert to browser
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message));
                }
                startActivity(intent);
            }

        }
    }

    public class CloseJavascriptInterface {
        private final Context context;

        public CloseJavascriptInterface(Context _context) {
            context = _context;
        }

        @JavascriptInterface
        public void postMessage(String message) {

           finish();

        }
    }

    public void makeToolbar(String title){

        ImageView backBtn = findViewById(R.id.btn_back);

        if(LanguageUtil.getLanguage(getApplicationContext()).equals("ar"))
            backBtn.setImageResource(R.drawable.arrow_right);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }
}