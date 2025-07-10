package com.saatco.murshadik.fragments.consultationAppointments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.adapters.ConsultaionAppointments.PastAppointmentAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.FragmentConsultationAppointmentsPastAppointmentsBinding;
import com.saatco.murshadik.model.ConsultationAppointments.ConsultationAppointment;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.utils.Util;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsultationAppointmentsPastAppointmentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultationAppointmentsPastAppointmentsFragment extends Fragment {

    FragmentConsultationAppointmentsPastAppointmentsBinding binding;

    ArrayList<ConsultationAppointment> appointments;

    public ConsultationAppointmentsPastAppointmentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConsultationAppointmentsPastAppointments.
     */
    public static ConsultationAppointmentsPastAppointmentsFragment newInstance() {

        return new ConsultationAppointmentsPastAppointmentsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentConsultationAppointmentsPastAppointmentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPastAppointments();
    }

    void initRecyclerView() {
        if (appointments == null) {
            binding.rvPastAppointments.setVisibility(View.GONE);
            binding.tvNoData.setVisibility(View.VISIBLE);
            return;
        }
        binding.rvPastAppointments.setVisibility(View.VISIBLE);
        binding.tvNoData.setVisibility(View.GONE);

        // init recycler view
        PastAppointmentAdapter adapter = new PastAppointmentAdapter(appointments, false);
        binding.rvPastAppointments.setAdapter(adapter);
        binding.rvPastAppointments.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPastAppointments.setAnimation(new AnimationSet(true));
    }

    void getPastAppointments() {
        // get past appointments
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<ConsultationAppointment>>> call = apiInterface.cAGetMyHistoryConsultationAppointments(TokenHelper.getBearerToken());

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<ConsultationAppointment>>> call, @NonNull Response<NewAPIsResponse<ArrayList<ConsultationAppointment>>> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                appointments = response.body().getData();
                initRecyclerView();

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<ConsultationAppointment>>> call, @NonNull Throwable t) {
                Log.e("API", "onFailure: " + t);
                Activity activity = getActivity();
                if (activity == null) return;
                Util.showErrorToast(t.getMessage(), activity);
            }
        });

    }
}