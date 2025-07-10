package com.saatco.murshadik.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.adapters.ClinicService.ClinicAppointmentsHistoryAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.FragmentClinicAppointmentHistoryBinding;
import com.saatco.murshadik.model.clinicService.ClinicAppointment;
import com.saatco.murshadik.model.clinicService.ClinicAppointmentStatus;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClinicCurrentAppointmentHistoryFragment extends Fragment {


    ArrayList<ClinicAppointment> clinicAppointmentsHistory;
    String dateTimeOfServer;
    FragmentClinicAppointmentHistoryBinding binding;

    public ClinicCurrentAppointmentHistoryFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentClinicAppointmentHistoryBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        getAppointmentsHistory();

    }

    private void initHistoryRecyclerView() {
        if (clinicAppointmentsHistory == null)
            return;
        binding.historyAppointmentRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.historyAppointmentRv.setItemAnimator(new DefaultItemAnimator());
        ClinicAppointmentsHistoryAdapter adapter = new ClinicAppointmentsHistoryAdapter(filterByTime(clinicAppointmentsHistory), getContext());
        binding.historyAppointmentRv.setAdapter(adapter);
    }

    private List<ClinicAppointment> filterByTime(ArrayList<ClinicAppointment> clinicAppointments) {
        return clinicAppointments.stream().filter(clinicAppointment ->
                Util.getClinicAppointmentStatus(clinicAppointment.getDateTime(), dateTimeOfServer) != ClinicAppointmentStatus.AVAILABLE &&
                        Util.getClinicAppointmentStatus(clinicAppointment.getDateTime(), dateTimeOfServer) != ClinicAppointmentStatus.PENDING
        ).collect(Collectors.toList());
    }

    void getAppointmentsHistory() {
        binding.clinicMyAppointmentHistoryPb.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<ClinicAppointment>>> call = apiInterface.getAppointmentsHistory(TokenHelper.getBearerToken(), false);
        call.enqueue(new Callback<NewAPIsResponse<ArrayList<ClinicAppointment>>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<ClinicAppointment>>> call,
                                   @NonNull Response<NewAPIsResponse<ArrayList<ClinicAppointment>>> response) {
                binding.clinicMyAppointmentHistoryPb.setVisibility(View.GONE);
                if (response.body() == null || response.body().getData() == null || response.body().getData().isEmpty())
                    return;

                clinicAppointmentsHistory = response.body().getData();
                dateTimeOfServer = ((Map<String, String>) response.body().getInfo()).get("dateTime");
                initHistoryRecyclerView();
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<ClinicAppointment>>> call,
                                  @NonNull Throwable t) {

            }
        });

    }


}
