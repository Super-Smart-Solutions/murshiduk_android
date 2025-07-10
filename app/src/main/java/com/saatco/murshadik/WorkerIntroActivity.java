package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.ActivityWorkerIntroBinding;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.model.workersService.Worker;
import com.saatco.murshadik.utils.ToastUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkerIntroActivity extends BaseActivity {

    ActivityWorkerIntroBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkerIntroBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        binding.appBar.btnBack.setOnClickListener(v -> onBackPressed());

        binding.appBar.toolbarTitle.setText(getString(R.string.worker_service));

        binding.btnSearchForWorkers.setOnClickListener(v -> {
            startActivity(new Intent(this, AvailableWorkerActivity.class));
        });

        binding.btnRegisterAsWorkers.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterWorkerActivity.class));
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


}