package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.AppointmentAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.ActivityLabCompletedBinding;
import com.saatco.murshadik.model.Appointment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LabCompletedActivity extends AppCompatActivity implements AppointmentAdapter.OnSelectItemClickListener {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    AppointmentAdapter appointmentAdapter;
    ArrayList<Appointment> appointments = new ArrayList<>();

    ActivityLabCompletedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLabCompletedBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        recyclerView = binding.rvAppointments;
        progressBar = binding.progressBar;

        ToolbarHelper.setToolBar(this, "", findViewById(R.id.toolbarTrans));


    }

    @Override
    public void onResume() {
        super.onResume();
        getAllAppointments();
    }

    private void getAllAppointments() {
        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Appointment>> call = apiInterface.getUserAppointments("Bearer " + TokenHelper.getToken());
        call.enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Appointment>> call, @NonNull Response<List<Appointment>> response) {

                progressBar.setVisibility(View.GONE);

                try {

                    ArrayList<Appointment> appointmentList = (ArrayList<Appointment>) response.body();

                    appointments.clear();

                    assert appointmentList != null;
                    for (Appointment appointment : appointmentList) {
                        if (appointment.isCompleted())
                            appointments.add(appointment);
                    }


                    setAdapter();

                } catch (Exception ignored) {
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Appointment>> call, @NonNull Throwable t) {

            }
        });

    }

    private void setAdapter() {
        if (appointments != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            appointmentAdapter = new AppointmentAdapter(appointments, getApplicationContext(), this);
            recyclerView.setAdapter(appointmentAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }

    @Override
    public void onClickAppointment(View view, int position, Appointment appointment) {

        if (appointment.isCompleted()) {
            Intent intent = new Intent(LabCompletedActivity.this, LabDetailActivity.class);
            intent.putExtra("APPOINTMENT_ID", appointment.getId());
            startActivity(intent);
        }

    }

    @Override
    public void onCancelAppointment(View view, int position, Appointment appointment) {


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }
}