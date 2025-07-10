package com.saatco.murshadik.fragments.consultationAppointments;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.saatco.murshadik.CallActivity;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.PermissionsActivity;
import com.saatco.murshadik.PrefUtil;
import com.saatco.murshadik.adapters.ConsultaionAppointments.FragmentsMainAppointmentsAdapter;
import com.saatco.murshadik.databinding.FragmentConsultationAppointmentsMainBinding;
import com.saatco.murshadik.model.ConsultationAppointments.ConsultationAppointment;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.PermissionsChecker;
import com.saatco.murshadik.utils.PushNotificationSender;
import com.saatco.murshadik.utils.SharedPrefsHelper;
import com.saatco.murshadik.utils.Util;
import com.saatco.murshadik.utils.WebRtcSessionManager;
import com.saatco.murshadik.viewmodels.ConsultationAppointmentViewModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsultationAppointmentsMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultationAppointmentsMainFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    FragmentConsultationAppointmentsMainBinding binding;

    public boolean isConsultant = false;

    public ConsultationAppointmentsMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConsultationAppointmentsMain.
     */
    public static ConsultationAppointmentsMainFragment newInstance() {
        return new ConsultationAppointmentsMainFragment();
    }

    public void onNextAppointmentLoaded(ConsultationAppointment appointment){
        if (appointment == null) {
            binding.llNextAppointmentLayout.setVisibility(View.GONE);
            return;
        }
        binding.llNextAppointmentLayout.setVisibility(View.VISIBLE);
        binding.tvAppointmentCityName.setText(appointment.getAppointmentCity());
        binding.tvAppointmentDate.setText(appointment.getAppointmentDate());
        binding.tvAppointmentTime.setText(appointment.getAppointmentTime());
        binding.tvAppointmentSkillName.setText(appointment.getSkillName());
        binding.tvAppointmentUserName.setText(isConsultant? appointment.getFarmerName() : appointment.getConsultantName());
        binding.tvAppointmentDuration.setText(appointment.getCallDurationString());

        if (appointment.isTimePassed()){
            binding.llAppointmentCallAndVideoButtons.setVisibility(View.VISIBLE);
            binding.btnVideoCall.setOnClickListener(v -> {
                onVideoClick(appointment);
            });

            binding.btnAudioCall.setOnClickListener(v -> {
                onAudioClick(appointment);
            });
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentConsultationAppointmentsMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ConsultationAppointmentViewModel viewModel = new ViewModelProvider(this).get(ConsultationAppointmentViewModel.class);
        viewModel.getNextAppointment().observe(getViewLifecycleOwner(), this::onNextAppointmentLoaded);
        viewModel.getIsConsultant().observe(getViewLifecycleOwner(), isConsultant -> {
            this.isConsultant = isConsultant;
        });

        if (getContext() != null) {
            User user = ProfileHelper.getAccount(requireContext());
            if (user != null) {
                viewModel.updateIsConsultant(user.isConsultant());
            }
        }

        binding.vpAppointments.setAdapter(new FragmentsMainAppointmentsAdapter(this));
        binding.vpAppointments.setOffscreenPageLimit(2);

        //
        binding.vpAppointments.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
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
                binding.vpAppointments.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    public void onAudioClick(ConsultationAppointment appointment) {
        SharedPrefsHelper.getInstance().save(Consts.EXTRA_CONSULTATION_APPOINTMENT_ID, appointment.getId());

        User user = new User();
        user.setChatId(isConsultant? appointment.getFarmerChatId() : appointment.getConsultantChatId());
        user.setName(isConsultant? appointment.getFarmerName() : appointment.getConsultantName());
        startCall(false, user);
    }

    public void onVideoClick(ConsultationAppointment appointment) {
        SharedPrefsHelper.getInstance().save(Consts.EXTRA_CONSULTATION_APPOINTMENT_ID, appointment.getId());

        User user = new User();
        user.setChatId(isConsultant? appointment.getFarmerChatId() : appointment.getConsultantChatId());
        user.setName(isConsultant? appointment.getFarmerName() : appointment.getConsultantName());
        startCall(true, user);
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
            SharedPrefsHelper.getInstance().save(Consts.EXTRA_IS_CONSULTATION_CALL, true);

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
            Util.showToast( receiverUser.getName() + " غير متاح", getActivity());
        }
    }
}