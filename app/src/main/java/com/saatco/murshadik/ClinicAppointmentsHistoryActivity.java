package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.saatco.murshadik.adapters.ClinicService.ClinicAppointmentsAdapter;
import com.saatco.murshadik.adapters.ClinicService.ClinicAppointmentsHistoryAdapter;
import com.saatco.murshadik.databinding.ActivityClinicAppointmentsHistoryBinding;
import com.saatco.murshadik.model.clinicService.ClinicAppointment;
import com.saatco.murshadik.model.clinicService.ClinicAppointmentStatus;
import com.saatco.murshadik.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClinicAppointmentsHistoryActivity extends AppCompatActivity {

    ActivityClinicAppointmentsHistoryBinding binding;
    ArrayList<ClinicAppointment> clinicAppointments;
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClinicAppointmentsHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        clinicAppointments = (ArrayList<ClinicAppointment>) getIntent().getSerializableExtra("appointments");
        String dateTimeOfServer = getIntent().getStringExtra("dateTimeOfServer");
        initRecyclerView();

    }

    private void initRecyclerView() {
        if (clinicAppointments == null)
            return;

        binding.previousAppointmentRv.setLayoutManager(new LinearLayoutManager(this));
        binding.previousAppointmentRv.setItemAnimator(new DefaultItemAnimator());
        ClinicAppointmentsHistoryAdapter adapter = new ClinicAppointmentsHistoryAdapter(clinicAppointments, this);
        binding.previousAppointmentRv.setAdapter(adapter);
    }


}