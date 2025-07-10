package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.AppointmentAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.databinding.ActivityLabReportActivitiyBinding;
import com.saatco.murshadik.model.Appointment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LabReportActivitiy extends AppCompatActivity implements AppointmentAdapter.OnSelectItemClickListener {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    FrameLayout layoutEmpty;
    Button btnBook;
    Button btnAppointments;

    AppointmentAdapter appointmentAdapter;
    ArrayList<Appointment> appointments = new ArrayList<>();

    ActivityLabReportActivitiyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLabReportActivitiyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();


        ToolbarHelper.setToolBar(this, "", findViewById(R.id.toolbarTrans));

        btnBook.setOnClickListener(view -> startActivity(new Intent(LabReportActivitiy.this, BookAppointmentActivity.class)));

        btnAppointments.setOnClickListener(view -> {

            Intent intent = new Intent(LabReportActivitiy.this, LabCompletedActivity.class);
            startActivity(intent);

        });
    }

    private void initViews() {
        recyclerView = binding.rvAppointments;
        progressBar = binding.progressBar;
        layoutEmpty = binding.layoutEmpty;
        btnBook = binding.btnBook;
        btnAppointments = binding.btnAppointments;
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

                    if (appointmentList != null) {

                        appointments.clear();

                        for (Appointment appointment : appointmentList) {
                            if (!appointment.isCompleted())
                                appointments.add(appointment);
                        }

                        if (hasCompleted(appointmentList))
                            btnAppointments.setVisibility(View.VISIBLE);
                        else
                            btnAppointments.setVisibility(View.GONE);

                        if (appointments.size() > 0) {
                            btnBook.setVisibility(View.GONE);
                            layoutEmpty.setVisibility(View.GONE);
                        } else {
                            btnBook.setVisibility(View.VISIBLE);
                            layoutEmpty.setVisibility(View.VISIBLE);
                        }


                    }

                    setAdapter();

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Appointment>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private boolean hasCompleted(ArrayList<Appointment> appointmentList) {
        for (Appointment appointment : appointmentList) {
            if (appointment.isCompleted())
                return true;
        }
        return false;
    }

    private void setAdapter() {
        if (appointments != null) {
            //  layoutEmpty.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            appointmentAdapter = new AppointmentAdapter(appointments, getApplicationContext(), this);
            recyclerView.setAdapter(appointmentAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }


    @Override
    public void onClickAppointment(View view, int position, Appointment appointment) {

        if (appointment.isCompleted()) {
            Intent intent = new Intent(LabReportActivitiy.this, LabDetailActivity.class);
            intent.putExtra("APPOINTMENT_ID", appointment.getId());
            startActivity(intent);
        }

    }

    @Override
    public void onCancelAppointment(View view, int position, Appointment appointment) {

        new AlertDialog.Builder(LabReportActivitiy.this)
                .setMessage("هل انت متاكد انك تريد الازالة؟")
                .setCancelable(false)
                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        cancelAppointment(appointment.getId());

                    }
                })
                .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert).show();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }

    private void cancelAppointment(int id) {

        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> call = apiInterface.cancelAppointment("Bearer " + TokenHelper.getToken(), id);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {

                try {

                    progressBar.setVisibility(View.GONE);

                    getAllAppointments();

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}