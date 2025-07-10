package com.saatco.murshadik;


import android.Manifest;
import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.ClinicService.ClinicAppointmentsAdapter;
import com.saatco.murshadik.adapters.ClinicService.ClinicAppointmentsHistoryAdapter;
import com.saatco.murshadik.adapters.FragmentsNewUiDesignAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.ActivityVirtualClinicBinding;
import com.saatco.murshadik.fragments.FragmentClinicAppointmentAdapter;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.model.clinicService.ClinicAppointment;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.DialogUtil;
import com.saatco.murshadik.utils.PermissionsChecker;
import com.saatco.murshadik.utils.PushNotificationSender;
import com.saatco.murshadik.utils.SharedPrefsHelper;
import com.saatco.murshadik.utils.Util;
import com.saatco.murshadik.utils.WebRtcSessionManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VirtualClinicActivity extends BaseActivity {

    ActivityVirtualClinicBinding binding;

    ViewPager2 mViewPager2Clinic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVirtualClinicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ToolbarHelper.setToolBarTransWithTitle(this, getString(R.string.virtual_clinic), findViewById(R.id.toolbarTrans));

        mViewPager2Clinic = binding.viewPagerClinicAppointment;
        mViewPager2Clinic.setAdapter(new FragmentClinicAppointmentAdapter(this));
        mViewPager2Clinic.setOffscreenPageLimit(2);
        mViewPager2Clinic.setUserInputEnabled(true);



        mViewPager2Clinic.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                TabLayout.Tab tab = binding.tlAppointments.getTabAt(position);
                binding.tlAppointments.selectTab(tab);
            }
        });

        binding.tlAppointments.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager2Clinic.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}