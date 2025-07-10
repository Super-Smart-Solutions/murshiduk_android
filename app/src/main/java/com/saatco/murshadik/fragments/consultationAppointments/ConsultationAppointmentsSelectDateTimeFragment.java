package com.saatco.murshadik.fragments.consultationAppointments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;

import com.saatco.murshadik.ConsultationAppointmentsActivity;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.R;
import com.saatco.murshadik.adapters.ConsultaionAppointments.AppointmentTimeAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.FragmentConsultationAppointmentsSelectDateTimeBinding;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.utils.Util;

import java.util.ArrayList;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsultationAppointmentsSelectDateTimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultationAppointmentsSelectDateTimeFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DATE = "date";
    private static final String ARG_TIME = "date";
    private static final String ARG_CONSULTATION_ID = "consultationId";

    private String mDate;
    private String mTime;
    private int consultationId;


    FragmentConsultationAppointmentsSelectDateTimeBinding binding;

    public ConsultationAppointmentsSelectDateTimeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param date Parameter 1.
     * @param time Parameter 2.
     * @param consultationId Parameter 3.
     * @return A new instance of fragment ConsultationAppointmentsSelectDateTimeFragment.
     */
    public static ConsultationAppointmentsSelectDateTimeFragment newInstance(String date, String time, int consultationId) {
        ConsultationAppointmentsSelectDateTimeFragment fragment = new ConsultationAppointmentsSelectDateTimeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, date);
        args.putString(ARG_TIME, time);
        args.putInt(ARG_CONSULTATION_ID, consultationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mDate = args.getString(ARG_DATE);
            mTime = args.getString(ARG_TIME);
            consultationId = args.getInt(ARG_CONSULTATION_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentConsultationAppointmentsSelectDateTimeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConsultationAppointmentsActivity activity = (ConsultationAppointmentsActivity) getActivity();
        if (activity != null) {
            binding.appBar.btnBack.setOnClickListener(v -> activity.back());
            binding.appBar.toolbarTitle.setText(activity.getString(R.string.select_skill));
            binding.appBar.btnCancel.setOnClickListener(v -> activity.close());
        }

        binding.calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            mDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            mTime = "";
            getAvailableTimes();
        });

        binding.calendarView.setMinDate(System.currentTimeMillis());
        binding.calendarView.setDate(System.currentTimeMillis());



        binding.btnNext.setOnClickListener(v -> {
            if (mTime == null || mTime.isEmpty()) {
                Util.showErrorToast(getString(R.string.please_select_time), getActivity());
            }
            next();
        });

    }

    void initTimesRecyclerView(ArrayList<String> availableTimes) {
        if (getContext() == null) return;
        if (availableTimes == null || availableTimes.isEmpty()) {
            binding.noAvailableTimesTv.setVisibility(View.VISIBLE);
            binding.rvTimesSlot.setVisibility(View.GONE);
        } else {
            binding.noAvailableTimesTv.setVisibility(View.GONE);
            binding.rvTimesSlot.setVisibility(View.VISIBLE);
            binding.rvTimesSlot.setAdapter(new AppointmentTimeAdapter(availableTimes, getContext(), (time, position) -> {
                mTime = time;
                binding.btnNext.setEnabled(true);
            }));
            binding.rvTimesSlot.setAnimation(new AnimationSet(true));
        }
    }

    void next() {
        ConsultationAppointmentsActivity activity = (ConsultationAppointmentsActivity) getActivity();
        if (activity != null) {
            activity.setDate(mDate);
            activity.setTime(mTime);
            activity.showFragment(ConsultationAppointments.ConsultationAppointmentsConfirmationFragment);
        }
    }


    void getAvailableTimes() {

        binding.loadingAppointmentTimesPb.setVisibility(View.VISIBLE);

        // Call the API to get the available times for the selected date
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<String>>> call = apiInterface.cAGetAvailableTimes(TokenHelper.getBearerToken(), consultationId, mDate);
        call.enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<String>>> call, @NonNull retrofit2.Response<NewAPIsResponse<ArrayList<String>>> response) {
                binding.loadingAppointmentTimesPb.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    initTimesRecyclerView(response.body().getData());
                } else {
                    binding.noAvailableTimesTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<String>>> call, @NonNull Throwable t) {
                binding.loadingAppointmentTimesPb.setVisibility(View.GONE);
                binding.noAvailableTimesTv.setVisibility(View.VISIBLE);
                Log.e("ConsultationAppointmentsSelectDateTimeFragment", "getAvailableTimes: " + t);
            }
        });


    }
}