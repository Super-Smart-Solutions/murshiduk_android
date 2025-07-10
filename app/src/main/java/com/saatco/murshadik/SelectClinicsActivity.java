package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.ClinicService.ClinicAdapter;
import com.saatco.murshadik.adapters.ClinicService.ClinicAppointmentTimeAdapter;
import com.saatco.murshadik.adapters.ClinicService.DoctorAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.ActivitySelectClinicsBinding;
import com.saatco.murshadik.fcm.GetAlarmBroadcastReceiver;
import com.saatco.murshadik.model.clinicService.Clinic;
import com.saatco.murshadik.model.clinicService.ClinicAppointmentTime;
import com.saatco.murshadik.model.clinicService.Doctor;
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

enum Stage {
    CLINIC, DOCTOR, DAY, TIME, CONFIRM
}

public class SelectClinicsActivity extends BaseActivity implements ClinicAppointmentTimeAdapter.OnClickListener, ClinicAppointmentTimeAdapter.OnDataChangeListener {

    ActivitySelectClinicsBinding binding;
    ClinicAppointmentTimeAdapter adapter;
    DatePickerDialog datePickerDialog;

    private int clinicId;
    private int appointmentTimeId;
    private String selectedDate = "";
    private String selectedTime;
    private String serverTime;
    private int doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySelectClinicsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        ToolbarHelper.setToolBarTransWithTitle(this, getString(R.string.clinics), findViewById(R.id.toolbarTrans));

        next(Stage.CLINIC);


        binding.edDate.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.setOnDateSetListener((datePicker, i, i1, i2) -> {
            int month = i1 + 1;
            String date = i + "-" + month + "-" + i2;
            binding.edDate.setText(date);
            selectedDate = date;
            next(Stage.TIME);
        });

        initRecyclerViewOfTimes(new ArrayList<>());

        binding.btnConfirm.setOnClickListener(v -> {
            postBookAppointmentApi();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getServerTime();
    }

    void initRecyclerViewOfClinics(ArrayList<Clinic> clinics) {
        if (clinics == null || clinics.isEmpty()) return;
        binding.rvClinics.setItemAnimator(new DefaultItemAnimator());
        ClinicAdapter adapter = new ClinicAdapter(clinics, this, clinic -> {
            clinicId = clinic.getId();
            next(Stage.DOCTOR);
        });
        binding.rvClinics.setAdapter(adapter);
    }

    void initRecyclerViewOfDoctors(ArrayList<Doctor> doctors) {
        if (doctors == null || doctors.isEmpty()) return;
        binding.rvDoctors.setItemAnimator(new DefaultItemAnimator());
        DoctorAdapter adapter = new DoctorAdapter(doctors, this, doctor -> {
            doctorId = doctor.getId();
            next(Stage.DAY);

        });
        binding.rvDoctors.setAdapter(adapter);
    }


    private void showDatePickerDialog() {
        datePickerDialog.show();
    }

    void initRecyclerViewOfTimes(ArrayList<ClinicAppointmentTime> clinicAppointmentTimes) {
        adapter = new ClinicAppointmentTimeAdapter(clinicAppointmentTimes, this, this, this);
        binding.rvTimesSlot.setItemAnimator(new DefaultItemAnimator());
        binding.rvTimesSlot.setAdapter(adapter);
    }

    @Override
    public void onTimeDataChanged(ArrayList<ClinicAppointmentTime> clinicAppointmentTimes) {
        if (clinicAppointmentTimes.isEmpty()) {
            binding.noAvailableTimesTv.setVisibility(View.VISIBLE);
            return;
        }
        binding.noAvailableTimesTv.setVisibility(View.GONE);
    }

    @Override
    public void onTimeClick(ClinicAppointmentTime clinicAppointmentTime, int position) {
        appointmentTimeId = clinicAppointmentTime.getId();
        selectedTime = clinicAppointmentTime.getTime();
        //"12/12/2020 | 12:00 PM"
        next(Stage.CONFIRM);
    }

    void next(Stage stage) {
        switch (stage) {
            case DOCTOR:
                showDoctors();
                hideDays();
                hideTimes();
                hideConfirm();
                getDoctorsApi();
                break;
            case DAY:
                showDays();
                hideTimes();
                hideConfirm();
                if (!selectedDate.isEmpty()) {
                    next(Stage.TIME);
                }
                break;
            case TIME:
                showTimes();
                hideConfirm();
                getAvailableAppointmentsApi(selectedDate);
                break;
            case CONFIRM:
                showConfirm();
                break;
            case CLINIC:
                hideDoctors();
                hideDays();
                hideTimes();
                hideConfirm();
                getClinicsApi();
                break;


        }
    }

    private void hideDoctors() {
        binding.rvDoctors.setVisibility(View.GONE);
        binding.tvSelectDoctor.setVisibility(View.GONE);
    }

    private void showDoctors() {
        getDoctorsApi();
        binding.rvDoctors.setVisibility(View.VISIBLE);
        binding.tvSelectDoctor.setVisibility(View.VISIBLE);
    }

    private void hideDays() {
        binding.edDate.setVisibility(View.GONE);
        binding.tvSelectDay.setVisibility(View.GONE);
    }

    private void showDays() {
        binding.edDate.setVisibility(View.VISIBLE);
        binding.tvSelectDay.setVisibility(View.VISIBLE);
    }

    private void hideTimes() {
        binding.rvTimesSlot.setVisibility(View.GONE);
        binding.tvSelectTime.setVisibility(View.GONE);
        binding.btnConfirm.setVisibility(View.GONE);
    }

    private void showTimes() {
        binding.rvTimesSlot.setVisibility(View.VISIBLE);
        binding.tvSelectTime.setVisibility(View.VISIBLE);
    }

    private void hideConfirm() {
        binding.btnConfirm.setVisibility(View.GONE);
    }

    private void showConfirm() {
        binding.btnConfirm.setVisibility(View.VISIBLE);
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

    void postBookAppointmentLoading(Boolean isLoading) {
        binding.pbConfirmLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnConfirm.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }


    void getClinicsApi() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<Clinic>>> call = apiInterface.getClinics(TokenHelper.getBearerToken());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<Clinic>>> call, @NonNull Response<NewAPIsResponse<ArrayList<Clinic>>> response) {

                if (response.body() == null) return;

                initRecyclerViewOfClinics(response.body().getData());

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<Clinic>>> call, @NonNull Throwable t) {

            }
        });
    }

    void getDoctorsApi() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<Doctor>>> call = apiInterface.getAllDoctorsOfClinic(TokenHelper.getBearerToken(), clinicId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<Doctor>>> call, @NonNull Response<NewAPIsResponse<ArrayList<Doctor>>> response) {

                if (response.body() == null) return;

                initRecyclerViewOfDoctors(response.body().getData());

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<Doctor>>> call, @NonNull Throwable t) {

            }
        });
    }

    void postBookAppointmentApi() {
        postBookAppointmentLoading(true);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<Object>> call = apiInterface.postBookAppointment(TokenHelper.getBearerToken(), appointmentTimeId, " ", clinicId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<Object>> call, @NonNull Response<NewAPIsResponse<Object>> response) {
                postBookAppointmentLoading(false);
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
                postBookAppointmentLoading(false);
                ToastUtils.longToast(getString(com.vanillaplacepicker.R.string.something_went_worng));

            }
        });
    }


    void getAvailableAppointmentsApi(String dateTime) {
        binding.pbLoadingAppointmentTimes.setVisibility(View.VISIBLE);
        binding.rvTimesSlot.setEnabled(false);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<ClinicAppointmentTime>>> call = apiInterface.getAvailableAppointmentOfDoctor(TokenHelper.getBearerToken(), dateTime, clinicId, doctorId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<ClinicAppointmentTime>>> call, @NonNull Response<NewAPIsResponse<ArrayList<ClinicAppointmentTime>>> response) {
                binding.pbLoadingAppointmentTimes.setVisibility(View.GONE);
                binding.rvTimesSlot.setEnabled(true);
                if (response.body() == null) {
                    Util.showErrorToast(getString(com.vanillaplacepicker.R.string.something_went_worng), SelectClinicsActivity.this);
                    return;
                }
                adapter.updateItems(response.body().getData(), serverTime, selectedDate);
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<ClinicAppointmentTime>>> call, @NonNull Throwable t) {
                adapter.updateItems(new ArrayList<>(), serverTime, selectedDate);
                Util.showErrorToast(getString(com.vanillaplacepicker.R.string.something_went_worng), SelectClinicsActivity.this);
            }
        });

    }

    void getServerTime() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<String>> call = apiInterface.getServerTime(TokenHelper.getBearerToken());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Response<NewAPIsResponse<String>> response) {
                if (response.body() == null) return;

                //2024-01-18T05:03:00.49413+03:00
                DateTime dateTime = DateTime.parse(response.body().getData());
                datePickerDialog.getDatePicker().setMinDate(dateTime.getMillis());
                serverTime = response.body().getData();
                selectedDate = dateTime.toString("yyyy-MM-dd");
                binding.edDate.setText(selectedDate);

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Throwable t) {

            }
        });
    }


}