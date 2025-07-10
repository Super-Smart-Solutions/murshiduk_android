package com.saatco.murshadik.fragments.consultationAppointments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.saatco.murshadik.ConsultationAppointmentsActivity;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.FragmentConsultationAppointmentsConfirmationBinding;
import com.saatco.murshadik.model.ConsultationAppointments.ConsultationAppointment;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.utils.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsultationAppointmentsConfirmationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultationAppointmentsConfirmationFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CITY = "city";
    private static final String ARG_SKILL = "skill";
    private static final String ARG_CONSULTANT = "consultant";
    private static final String ARG_DATE = "date";
    private static final String ARG_TIME = "time";
    private static final String ARG_REASON = "reason";


    private String selectedCity;
    private Item selectedSkill;
    private User consultant;
    private String date;
    private String time;
    private String reason;

    private FragmentConsultationAppointmentsConfirmationBinding binding;

    public ConsultationAppointmentsConfirmationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param city       Parameter 1.
     * @param skill      Parameter 2.
     * @param consultant Parameter 3.
     * @param date       Parameter 4.
     * @param time       Parameter 5.
     * @param reason     Parameter 6.
     * @return A new instance of fragment ConsultationAppointmentsConfirmationFragment.
     */
    public static ConsultationAppointmentsConfirmationFragment newInstance(
            String city,
            Item skill,
            User consultant,
            String date,
            String time,
            String reason
    ) {
        ConsultationAppointmentsConfirmationFragment fragment = new ConsultationAppointmentsConfirmationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CITY, city);
        args.putSerializable(ARG_SKILL, skill);
        args.putSerializable(ARG_CONSULTANT, consultant);
        args.putString(ARG_DATE, date);
        args.putString(ARG_TIME, time);
        args.putString(ARG_REASON, reason);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedCity = getArguments().getString(ARG_CITY);
            selectedSkill = (Item) getArguments().getSerializable(ARG_SKILL);
            consultant = (User) getArguments().getSerializable(ARG_CONSULTANT);
            date = getArguments().getString(ARG_DATE);
            time = getArguments().getString(ARG_TIME);
            reason = getArguments().getString(ARG_REASON);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentConsultationAppointmentsConfirmationBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConsultationAppointmentsActivity activity = (ConsultationAppointmentsActivity) getActivity();
        if (activity != null) {
            binding.appBar.btnBack.setOnClickListener(v -> activity.back());
            binding.appBar.toolbarTitle.setText(activity.getString(R.string.confirm_appointment));
            binding.appBar.btnCancel.setOnClickListener(v -> activity.close());
        }

        binding.tcvCity.setText(selectedCity);
        binding.tcvSkill.setText(selectedSkill.getNameAr());
        binding.tcvReason.setText(reason);
        binding.tvTime.setText(time.substring(0, 5));

        binding.tvConsultantName.setText(consultant.getName());
        binding.tvRating.setText(consultant.getUserRatingStr());
        Glide.with(requireContext())
                .load(APIClient.apiUrl + consultant.getPhotoUrl())
                .placeholder(R.drawable.logo)
                .into(binding.ivConsultantAvatar);

        // convert string date from "2024-12-31" to "31 Dec 2024"
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("ar", "AE"));
            Date date = sdf.parse(this.date);
            sdf.applyPattern("MMM");
            String[] dateParts = this.date.split("-");
            binding.tvDay.setText(dateParts[2]);
            binding.tvYear.setText(dateParts[0]);
            binding.tvMonth.setText(sdf.format(date));
        } catch (Exception e) {
            Log.e("ConsultationAppointmentsConfirmationFragment", "onViewCreated: ", e);
        }

        binding.btnConfirm.setOnClickListener(v -> conformAppointment());
    }

    void next() {
        Util.showSuccessToast(getString(R.string.appointment_success), getActivity());
        ConsultationAppointmentsActivity activity = (ConsultationAppointmentsActivity) getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    void conformAppointment() {
        binding.btnConfirm.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        String saveCityAndSkillInReason = "المدينة: " + selectedCity + "\n" + "التخصص: " + selectedSkill.getNameAr() + "\n" + reason;
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ConsultationAppointment>> call = apiInterface.cAPostBookAppointment(
                TokenHelper.getBearerToken(),
                consultant.getId(),
                selectedSkill.getId(),
                saveCityAndSkillInReason,
                date,
                time
        );
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ConsultationAppointment>> call, @NonNull Response<NewAPIsResponse<ConsultationAppointment>> response) {

                binding.btnConfirm.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                if (response.body() != null && response.body().getStatus()) {
                    next();
                } else if (response.body() != null && getActivity() != null) {
                    Util.showErrorToast(response.body().getMessage(), getActivity());
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ConsultationAppointment>> call, @NonNull Throwable throwable) {
                Util.showErrorToast(throwable.getMessage(), getActivity());

                binding.btnConfirm.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

}

