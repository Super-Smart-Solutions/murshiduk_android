package com.saatco.murshadik;

import android.animation.LayoutTransition;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.ClinicService.ClinicAppointmentTimeAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.ActivitySelectClinicDateTimeBinding;
import com.saatco.murshadik.fcm.GetAlarmBroadcastReceiver;
import com.saatco.murshadik.model.clinicService.Clinic;
import com.saatco.murshadik.model.clinicService.ClinicAppointmentTime;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.ToastUtils;
import com.saatco.murshadik.utils.Util;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectClinicDateTimeActivity extends BaseActivity
        implements ClinicAppointmentTimeAdapter.OnClickListener, ClinicAppointmentTimeAdapter.OnDataChangeListener {

    private ActivitySelectClinicDateTimeBinding binding;
    private int clinicId;
    private int appointmentTimeId;
    private String selectedDate;
    private String selectedTime;
    ClinicAppointmentTimeAdapter adapter;

    private String serverTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySelectClinicDateTimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.parentCl.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        binding.contentLl.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        Clinic clinic = ((Clinic) getIntent().getSerializableExtra("clinic"));
        if (clinic == null) {
            finish();
            return;
        }

        clinicId = clinic.getId();

        ToolbarHelper.setToolBarTransWithTitle(this, getString(R.string.clinic, clinic.getName()), findViewById(R.id.toolbarTrans));

        binding.calendarView.setMinDate(System.currentTimeMillis() - 1000);

        binding.calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            binding.nextActionLl.setVisibility(View.GONE);
            DateTime dateTime = new DateTime(year, month + 1, dayOfMonth, 0, 0);
            selectedDate = dateTime.toString("yyyy-MM-dd");
            getAvailableAppointmentsApi(selectedDate);
        });

        binding.reserveBtn.setOnClickListener(v -> {
            postBookAppointmentApi();

        });

        initRecyclerView(new ArrayList<>());

    }

    @Override
    protected void onResume() {
        super.onResume();
        getServerTime();
    }

    @Override
    public void onTimeClick(ClinicAppointmentTime clinicAppointmentTime, int position) {
        binding.nextActionLl.setVisibility(View.VISIBLE);
        appointmentTimeId = clinicAppointmentTime.getId();
        selectedTime = clinicAppointmentTime.getTime();
        //"12/12/2020 | 12:00 PM"
        binding.selectedDateTv.setText(selectedDate.concat(" | ").concat(clinicAppointmentTime.getTime()));
    }


    void initRecyclerView(ArrayList<ClinicAppointmentTime> clinicAppointmentTimes) {
        binding.timesSlotRv.setItemAnimator(new DefaultItemAnimator());
        adapter = new ClinicAppointmentTimeAdapter(clinicAppointmentTimes, this, this, this);
        binding.timesSlotRv.setAdapter(adapter);
    }



    void makeNotificationReminder() {
        Util.createNotificationChannel(this, Consts.CLINIC_CHANNEL_NOTIFICATION_ID, getString(R.string.clinic_reminder));

        long when = getNotifyDateTime().getTime();

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), GetAlarmBroadcastReceiver.class);
        intent.putExtra(Consts.EXTRA_CLINIC_NOTIFICATION, "clinic_notification_reminder");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, when - (5 * 60 * 1000), pendingIntent);

    }

    Date getNotifyDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", new Locale("ar", "SA"));
        try {
            return sdf.parse(selectedDate.concat("T").concat(selectedTime));
        } catch (ParseException e) {
            DateTime dateTime = new DateTime(selectedDate.concat("T").concat(selectedTime).concat(":00"));
            return new Date(dateTime.getMillis());
        }
    }

    void goToVirtualClinicActivity() {
        // go to first activity in the stack
        Intent intent = new Intent(this, VirtualClinicActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    void postBookAppointmentApi() {
        binding.loadingProgressBar.getRoot().setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<Object>> call = apiInterface.postBookAppointment(TokenHelper.getBearerToken(), appointmentTimeId, " ", clinicId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<Object>> call, @NonNull Response<NewAPIsResponse<Object>> response) {
                binding.loadingProgressBar.getRoot().setVisibility(View.GONE);
                if (response.body() == null) {
                    ToastUtils.longToast(getString(com.vanillaplacepicker.R.string.something_went_worng));
                    return;
                }
                if (response.body().getStatus()) {
                    ToastUtils.longToast(getString(R.string.appointment_success));
                    makeNotificationReminder();
                    goToVirtualClinicActivity();
                } else {
                    ToastUtils.longToast(getString(com.vanillaplacepicker.R.string.something_went_worng));
                }

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<Object>> call, @NonNull Throwable t) {
                binding.loadingProgressBar.getRoot().setVisibility(View.GONE);
                ToastUtils.longToast(getString(com.vanillaplacepicker.R.string.something_went_worng));

            }
        });
    }

    void getAvailableAppointmentsApi(String dateTime) {
        binding.loadingAppointmentTimesPb.setVisibility(View.VISIBLE);
        binding.timesSlotRv.setEnabled(false);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<ClinicAppointmentTime>>> call = apiInterface.getAvailableAppointment(TokenHelper.getBearerToken(), dateTime, clinicId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<ClinicAppointmentTime>>> call, @NonNull Response<NewAPIsResponse<ArrayList<ClinicAppointmentTime>>> response) {
                binding.loadingAppointmentTimesPb.setVisibility(View.GONE);
                binding.timesSlotRv.setEnabled(true);
                if (response.body() == null) {
                    Util.showErrorToast(getString(com.vanillaplacepicker.R.string.something_went_worng), SelectClinicDateTimeActivity.this);
                    return;
                }
                adapter.updateItems(response.body().getData(), serverTime, selectedDate);
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<ClinicAppointmentTime>>> call, @NonNull Throwable t) {
                adapter.updateItems(new ArrayList<>(), serverTime, selectedDate);
                Util.showErrorToast(getString(com.vanillaplacepicker.R.string.something_went_worng), SelectClinicDateTimeActivity.this);
            }
        });

    }

    void getServerTime() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<String>> call = apiInterface.getServerTime(TokenHelper.getBearerToken());
        call.enqueue(new Callback<NewAPIsResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Response<NewAPIsResponse<String>> response) {
                if (response.body() == null)
                    return;

                //2024-01-18T05:03:00.49413+03:00
                DateTime dateTime = DateTime.parse(response.body().getData());
                binding.calendarView.setMinDate(dateTime.getMillis());
                serverTime = response.body().getData();

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Throwable t) {

            }
        });
    }


    @Override
    public void onTimeDataChanged(ArrayList<ClinicAppointmentTime> clinicAppointmentTimes) {
        if (clinicAppointmentTimes.size() == 0) {
            binding.noAvailableTimesTv.setVisibility(View.VISIBLE);
            return;
        }
        binding.noAvailableTimesTv.setVisibility(View.GONE);
    }
}