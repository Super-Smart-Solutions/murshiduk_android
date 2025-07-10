package com.saatco.murshadik;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatDelegate;

import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.utils.WebRtcSessionManager;


public class InitialActivity extends BaseActivity {

    boolean wasRun = false;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_initial);

        videoView = findViewById(R.id.videoView);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        boolean isFirstTime = PrefUtil.getBoolean(getApplicationContext(), "is_first_name");

        if (!isFirstTime) {
            LanguageUtil.saveLanguage(getApplicationContext(), "ar");
        }

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O)
            LanguageUtil.changeLanguage(InitialActivity.this);

        // LanguageUtil.changeLanguage(getApplicationContext());
        LocaleHelper.setLocale(InitialActivity.this, LanguageUtil.getLanguage(getApplicationContext()));

        new Thread(() -> {
            WebRtcSessionManager.createNotifyChannel(this, WebRtcSessionManager.CALL_CHANNEL_ID, getString(R.string.call_channel_name));
            WebRtcSessionManager.createNotifyChannel(this, WebRtcSessionManager.CALL_CHANNEL_ID_SILENCE, getString(R.string.call_channel_name));
        }).start();



        initIntro(videoView);

    }


    private void initIntro(VideoView videoView) {

        videoView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro_splash));
        videoView.start();
        videoView.setOnCompletionListener(mp -> {
            videoView.stopPlayback();
            mp.release();
            startNextActivity();
        });

    }


    @Override
    protected void attachBaseContext(Context newBase) {

        Context context = LocaleHelper.onAttach(newBase);
        super.attachBaseContext(context);

        Configuration config = new Configuration(newBase.getResources().getConfiguration());
        config.fontScale = 1.0f;
        applyOverrideConfiguration(config);
    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void startNextActivity() {
        if (ProfileHelper.hasAccount(getApplicationContext())) {
            Intent i = new Intent(InitialActivity.this, NewMainActivityDesign.class);
            startActivity(i);
        } else {
            Intent i = new Intent(InitialActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }
}
