package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.databinding.ActivityContactUsBinding;
import com.saatco.murshadik.db.StorageHelper;
import com.saatco.murshadik.model.ContactUs;

public class ContactUsActivity extends BaseActivity {

    LinearLayout btnWhatsapp;
    LinearLayout btnCall;
    LinearLayout btnFB;
    LinearLayout btnTwitter;
    LinearLayout btnSnapchat;
    LinearLayout btnInstagram;
    LinearLayout btnYoutube;
    TextView whatsApp;
    TextView support;


    ActivityContactUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactUsBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initViews();
        ToolbarHelper.setToolBar(this, "", findViewById(R.id.toolbarTrans));

        ContactUs contactUs = StorageHelper.getContactUS();

        if (contactUs != null) {

            if (contactUs.whatsApp != null) {
                if (!contactUs.whatsApp.equals(""))
                    btnWhatsapp.setVisibility(View.VISIBLE);
            } else {
                btnWhatsapp.setVisibility(View.GONE);
            }

            if (contactUs.facebook != null) {
                if (contactUs.facebook.length() > 1)
                    btnFB.setVisibility(View.VISIBLE);
            } else
                btnFB.setVisibility(View.GONE);


            if (contactUs.instagram != null) {
                if (contactUs.instagram.length() > 1)
                    btnInstagram.setVisibility(View.VISIBLE);
            } else
                btnInstagram.setVisibility(View.GONE);


            if (contactUs.snapChat != null) {
                if (contactUs.snapChat.length() > 1)
                    btnSnapchat.setVisibility(View.VISIBLE);
            } else
                btnSnapchat.setVisibility(View.GONE);

            if (contactUs.youtube != null) {
                if (contactUs.youtube.length() > 1)
                    btnYoutube.setVisibility(View.VISIBLE);
            } else
                btnYoutube.setVisibility(View.GONE);


            if (contactUs.twitter != null) {
                if (contactUs.twitter.length() > 1)
                    btnTwitter.setVisibility(View.VISIBLE);
            } else
                btnTwitter.setVisibility(View.GONE);


            if (contactUs.supportNumber != null) {
                if (contactUs.supportNumber.length() > 1)
                    btnCall.setVisibility(View.VISIBLE);
            } else {
                btnCall.setVisibility(View.GONE);
            }

            if (contactUs.supportNumber != null) {
                support.setText(contactUs.supportNumber);
                whatsApp.setText(contactUs.whatsApp);
            }
        }

        btnWhatsapp.setOnClickListener(view -> {

            assert contactUs != null;
            String url = "https://api.whatsapp.com/send?phone=" + contactUs.whatsApp.replace("+", "");
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        btnCall.setOnClickListener(view -> {

            if (ContextCompat.checkSelfPermission(ContactUsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ContactUsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            } else {
                assert contactUs != null;
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactUs.supportNumber));
                startActivity(intent);
            }
        });

        btnFB.setOnClickListener(view -> {

            assert contactUs != null;
            String url = contactUs.facebook;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        btnInstagram.setOnClickListener(view -> {

            assert contactUs != null;
            String url = contactUs.instagram;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

        });

        btnSnapchat.setOnClickListener(view -> {

            assert contactUs != null;
            String url = contactUs.snapChat;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        btnYoutube.setOnClickListener(view -> {

            assert contactUs != null;
            String url = contactUs.youtube;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

        });

        btnTwitter.setOnClickListener(view -> {

            assert contactUs != null;
            String url = contactUs.twitter;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

        });

    }

    private void initViews() {
        btnWhatsapp = binding.btnWhatsapp;
        btnCall = binding.btnCall;
        btnFB = binding.btnFB;
        btnTwitter = binding.btnTwitter;
        btnSnapchat = binding.btnSnapchat;
        btnInstagram = binding.btnInstagram;
        btnYoutube = binding.btnYoutube;
        whatsApp = binding.whatsApp;
        support = binding.support;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
