package com.saatco.murshadik.fragments.consultationAppointments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;

import com.saatco.murshadik.ConsultationAppointmentsActivity;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.R;
import com.saatco.murshadik.adapters.ConsultaionAppointments.NextAppointmentAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.FragmentConsultationAppointmentsNextAppointmentsBinding;
import com.saatco.murshadik.model.ConsultationAppointments.ConsultationAppointment;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.utils.DialogUtil;
import com.saatco.murshadik.utils.Util;
import com.saatco.murshadik.viewmodels.ConsultationAppointmentViewModel;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsultationAppointmentsNextAppointmentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultationAppointmentsNextAppointmentsFragment extends Fragment {


    FragmentConsultationAppointmentsNextAppointmentsBinding binding;
    private ConsultationAppointmentViewModel viewModel;

    public ArrayList<ConsultationAppointment> appointments;

    public ConsultationAppointmentsNextAppointmentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConsultationAppointmentsNextAppointments.
     */
    public static ConsultationAppointmentsNextAppointmentsFragment newInstance() {

        return new ConsultationAppointmentsNextAppointmentsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentConsultationAppointmentsNextAppointmentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(ConsultationAppointmentViewModel.class);

        binding.fab.setOnClickListener(v -> {
            if (getActivity() == null) return;
            Intent intent = new Intent(requireContext(), ConsultationAppointmentsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getNextAppointments();
    }

    void initRecyclerView() {
        // init recycler view
        NextAppointmentAdapter adapter = new NextAppointmentAdapter(appointments, requireContext(), viewModel.getIsConsultant().getValue() , appointment -> {
            DialogUtil.yesNoDialog(requireActivity(), getString(R.string.cancel_appointment), getString(R.string.are_you_sure), (msg) -> {
                if (Objects.equals(msg, DialogUtil.YES)) {
                    cancelAppointment(appointment.getId());
                }
            });
        });
        binding.rvNextAppointments.setAdapter(adapter);
        binding.rvNextAppointments.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNextAppointments.setAnimation(new AnimationSet(true));
        binding.rvNextAppointments.setVisibility(View.VISIBLE);
    }

    void nextAppointmentsLoaded() {
        if (appointments == null || appointments.isEmpty()) {
            binding.rvNextAppointments.setVisibility(View.GONE);
            binding.tvNoData.setVisibility(View.VISIBLE);
            viewModel.updateNextAppointment(null);
            return;
        }
        binding.rvNextAppointments.setVisibility(View.VISIBLE);
        binding.tvNoData.setVisibility(View.GONE);

        // next appointments loaded
        initRecyclerView();

        viewModel.updateNextAppointment(appointments.get(0));
    }

    void getNextAppointments() {
        // get past appointments
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<ConsultationAppointment>>> call = apiInterface.cAGetMyNextConsultationAppointments(TokenHelper.getBearerToken());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<ConsultationAppointment>>> call, @NonNull Response<NewAPIsResponse<ArrayList<ConsultationAppointment>>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Activity activity = getActivity();
                    if (activity == null) return;
                    Util.showErrorToast(activity.getString(R.string.error_happend), activity);
                    return;
                }

                appointments = response.body().getData();

                nextAppointmentsLoaded();

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<ConsultationAppointment>>> call, @NonNull Throwable t) {
                Log.e("API", "onFailure: " + t);
                Activity activity = getActivity();
                if (activity == null) return;
                Util.showErrorToast(activity.getString(R.string.error_happend), activity);
            }
        });
    }

    void cancelAppointment(int appointmentId) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<String>> call = apiInterface.cAPutCancelAppointment(TokenHelper.getBearerToken(), appointmentId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Response<NewAPIsResponse<String>> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                Util.showSuccessToast(getString(R.string.appointment_canceled), requireActivity());
                getNextAppointments();
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Throwable t) {
                Log.e("API", "onFailure: " + t);
                Activity activity = getActivity();
                if (activity == null) return;
                Util.showErrorToast(activity.getString(R.string.error_happend), activity);
            }
        });
    }
}