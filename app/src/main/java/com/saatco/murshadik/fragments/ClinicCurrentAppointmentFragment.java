package com.saatco.murshadik.fragments;

import static android.content.Context.ALARM_SERVICE;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.saatco.murshadik.CallActivity;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.PermissionsActivity;
import com.saatco.murshadik.PrefUtil;
import com.saatco.murshadik.R;
import com.saatco.murshadik.SelectClinicsActivity;
import com.saatco.murshadik.adapters.ClinicService.ClinicAppointmentsAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.FragmentClinicAppointmentBinding;
import com.saatco.murshadik.fcm.GetAlarmBroadcastReceiver;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.model.clinicService.ClinicAppointment;
import com.saatco.murshadik.model.clinicService.ClinicAppointmentStatus;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.DialogUtil;
import com.saatco.murshadik.utils.PermissionsChecker;
import com.saatco.murshadik.utils.PushNotificationSender;
import com.saatco.murshadik.utils.SharedPrefsHelper;
import com.saatco.murshadik.utils.Util;
import com.saatco.murshadik.utils.WebRtcSessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClinicCurrentAppointmentFragment extends Fragment implements ClinicAppointmentsAdapter.OnClickListener {

    FragmentClinicAppointmentBinding binding;

    ArrayList<ClinicAppointment> clinicAppointments;
    ClinicAppointmentsAdapter adapter;
    String dateTimeOfServer;
    boolean isDoctor = false;

    public ClinicCurrentAppointmentFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentClinicAppointmentBinding.inflate(inflater, container, false);

        binding.makeAppointmentBtn.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SelectClinicsActivity.class));
        });
        binding.currentAppointmentLl.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        clinicAppointments = new ArrayList<>();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        getIsDoctorApi();
        getMyAppointments();

    }

    private void initRecyclerView() {
        binding.currentAppointmentRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.currentAppointmentRv.setItemAnimator(new DefaultItemAnimator());
        adapter = new ClinicAppointmentsAdapter(clinicAppointments, dateTimeOfServer, getContext(), this);
        binding.currentAppointmentRv.setAdapter(adapter);

    }

    private List<ClinicAppointment> filterByTime(ArrayList<ClinicAppointment> clinicAppointments) {
        return clinicAppointments.stream().filter(clinicAppointment ->
                Util.getClinicAppointmentStatus(clinicAppointment.getDateTime(), dateTimeOfServer) == ClinicAppointmentStatus.AVAILABLE ||
                        Util.getClinicAppointmentStatus(clinicAppointment.getDateTime(), dateTimeOfServer) == ClinicAppointmentStatus.PENDING
        ).collect(Collectors.toList());
    }

    @Override
    public void onVoiceClick(ClinicAppointment clinicAppointment) {
        SharedPrefsHelper.getInstance().save(Consts.EXTRA_CLINIC_APPOINTMENT_ID, clinicAppointment.getId());

        User user = new User();
        user.setChatId(clinicAppointment.getChatId());
        user.setName(clinicAppointment.getDocName());
        startCall(false, user);
    }

    @Override
    public void onVideoClick(ClinicAppointment clinicAppointment) {
        SharedPrefsHelper.getInstance().save(Consts.EXTRA_CLINIC_APPOINTMENT_ID, clinicAppointment.getId());

        User user = new User();
        user.setChatId(clinicAppointment.getChatId());
        user.setName(clinicAppointment.getDocName());
        startCall(true, user);
    }

    @Override
    public void onCancelClick(ClinicAppointment clinicAppointment) {
        DialogUtil.yesNoDialog(getContext(), getString(R.string.cancel_appointment), getString(R.string.are_you_sure), (msg) -> {
            if (msg.equals("yes"))
                cancelAppointment(clinicAppointment.getId());
        });
    }

    void showMakeAppointmentBtn(boolean show) {
        if (isDoctor) {
            binding.makeAppointmentBtn.setVisibility(View.GONE);
            return;
        }
        binding.makeAppointmentBtn.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    void cancelNotificationReminder() {
        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(requireActivity().getApplicationContext(), GetAlarmBroadcastReceiver.class);
        intent.putExtra(Consts.EXTRA_CLINIC_NOTIFICATION, "clinic_notification_reminder");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireActivity().getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }

    void getMyAppointments() {
        binding.clinicMyAppointmentPb.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<ClinicAppointment>>> call = apiInterface.getMyAppointments(TokenHelper.getBearerToken(), false);
        call.enqueue(new Callback<NewAPIsResponse<ArrayList<ClinicAppointment>>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<ClinicAppointment>>> call,
                                   @NonNull Response<NewAPIsResponse<ArrayList<ClinicAppointment>>> response) {
                binding.clinicMyAppointmentPb.setVisibility(View.GONE);
                if (response.body() == null)
                    return;

                clinicAppointments = response.body().getData();
                dateTimeOfServer = ((Map<String, String>) response.body().getInfo()).get("dateTime");

                clinicAppointments = (ArrayList<ClinicAppointment>) filterByTime(clinicAppointments);


                initRecyclerView();

                if (clinicAppointments == null || clinicAppointments.isEmpty()) {
                    showMakeAppointmentBtn(true);
                    binding.noAppointmentFl.setVisibility(View.VISIBLE);
                } else {
                    binding.noAppointmentFl.setVisibility(View.GONE);
                    showMakeAppointmentBtn(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<ClinicAppointment>>> call,
                                  @NonNull Throwable t) {
                Log.v("VirtualClinicActivity", "onFailure: " + t.getMessage());
            }
        });
    }


    void cancelAppointment(int appointmentId) {
        binding.clinicMyAppointmentPb.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<Object>> call = apiInterface.putCancelAppointmentBooking(TokenHelper.getBearerToken(), appointmentId);
        call.enqueue(new Callback<NewAPIsResponse<Object>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<Object>> call,
                                   @NonNull Response<NewAPIsResponse<Object>> response) {
                binding.clinicMyAppointmentPb.setVisibility(View.GONE);
                if (response.body() == null)
                    return;

                Util.showSuccessToast(getString(R.string.appointment_cancelled), getActivity());
                cancelNotificationReminder();

                getMyAppointments();


            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<Object>> call,
                                  @NonNull Throwable t) {

            }
        });
    }

    void getDateTimeOfServer() {
        binding.clinicMyAppointmentPb.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<String>> call = apiInterface.getServerTime(TokenHelper.getBearerToken());
        call.enqueue(new Callback<NewAPIsResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<String>> call,
                                   @NonNull Response<NewAPIsResponse<String>> response) {
                binding.clinicMyAppointmentPb.setVisibility(View.GONE);
                if (response.body() == null)
                    return;

                dateTimeOfServer = response.body().getData();

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<String>> call,
                                  @NonNull Throwable t) {

            }
        });

    }

    void getIsDoctorApi() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<Object>> call = apiInterface.getIsDoctor(TokenHelper.getBearerToken());
        call.enqueue(new Callback<NewAPIsResponse<Object>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<Object>> call,
                                   @NonNull Response<NewAPIsResponse<Object>> response) {
                if (response.body() == null)
                    return;

                isDoctor = response.body().getStatus();
                if (isDoctor)
                    showMakeAppointmentBtn(false);
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<Object>> call,
                                  @NonNull Throwable t) {

            }
        });
    }


    private boolean checkPermission() {
        PermissionsChecker checker = new PermissionsChecker(requireActivity().getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return checker.lacksPermissions(Manifest.permission.BLUETOOTH_CONNECT) || checker.lacksPermissions(Consts.PERMISSIONS);
        }
        return checker.lacksPermissions(Consts.PERMISSIONS);
    }

    //******************* START CALL from QB SDK **********************//
    private void startCall(boolean isVideoCall, User receiverUser) {
        if (checkPermission()) {
            PermissionsActivity.startActivity(getContext(), !isVideoCall, Consts.PERMISSIONS);
            return;
        }
        if (receiverUser.getChatId() != null) {
            SharedPrefsHelper.getInstance().save(Consts.EXTRA_IS_CLINIC_CALL, true);

            ArrayList<Integer> opponentsList = new ArrayList<>();
            opponentsList.add(Integer.valueOf(receiverUser.getChatId()));

            QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                    ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                    : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

            QBRTCClient qbrtcClient = QBRTCClient.getInstance(requireActivity().getApplicationContext());
            QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);
            WebRtcSessionManager.getInstance(getContext()).setCurrentSession(newQbRtcSession);
            String newSessionID = newQbRtcSession.getSessionID();
            //******************* send push to user **********************/
            PushNotificationSender.sendPushMessage(opponentsList, newSessionID, ProfileHelper.getAccount(requireActivity().getApplicationContext()).getName(), "1");

            PrefUtil.writePreferenceValue(requireActivity().getApplicationContext(), "call_user_name", receiverUser.getName());

            CallActivity.start(getContext(), false);
        } else {
            Util.showToast("المرشد غير متاح", getActivity());
        }
    }

}
