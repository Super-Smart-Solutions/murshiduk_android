package com.saatco.murshadik;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.saatco.murshadik.databinding.ActivityRateClinicDoctorBinding;

public class RateClinicDoctorActivity extends AppCompatActivity {

    ActivityRateClinicDoctorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRateClinicDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}