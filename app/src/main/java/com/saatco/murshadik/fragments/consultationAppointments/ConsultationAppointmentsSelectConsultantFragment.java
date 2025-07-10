package com.saatco.murshadik.fragments.consultationAppointments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.saatco.murshadik.ConsultationAppointmentsActivity;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.R;
import com.saatco.murshadik.adapters.ConsultaionAppointments.ConsultantAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.FragmentConsultationAppointmentsSelectConsultantBinding;
import com.saatco.murshadik.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsultationAppointmentsSelectConsultantFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultationAppointmentsSelectConsultantFragment extends Fragment {

    private static final String ARG_SKILL_NAME = "skillName";

    private FragmentConsultationAppointmentsSelectConsultantBinding binding;
    private ArrayList<User> consultants;
    private ConsultantAdapter adapter;
    private String skillName;

    public ConsultationAppointmentsSelectConsultantFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param skillName The name of the skill to filter the consultants by.
     * @return A new instance of fragment ConsultationAppointmentsSelectConsultantFragment.
     */
    public static ConsultationAppointmentsSelectConsultantFragment newInstance(String skillName) {
        ConsultationAppointmentsSelectConsultantFragment fragment = new ConsultationAppointmentsSelectConsultantFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SKILL_NAME, skillName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            skillName = getArguments().getString(ARG_SKILL_NAME);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentConsultationAppointmentsSelectConsultantBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConsultationAppointmentsActivity activity = (ConsultationAppointmentsActivity) getActivity();
        if (activity != null) {
            binding.appBar.btnBack.setOnClickListener(v -> activity.back());
            binding.appBar.toolbarTitle.setText(activity.getString(R.string.select_consultant));
            binding.appBar.btnCancel.setOnClickListener(v -> activity.close());
        }

        getConsultants();
    }

    void initRecyclerView() {
        if (getContext() == null) return;
        if (consultants.isEmpty()) {
            binding.tvNoConsultantsFound.setVisibility(View.VISIBLE);
            return;
        }

        binding.tvNoConsultantsFound.setVisibility(View.GONE);

        filterConsultantsBySkill();

        if (consultants.isEmpty()) {
            binding.tvNoConsultantsFound.setVisibility(View.VISIBLE);
            return;
        }

        adapter = new ConsultantAdapter(consultants, getContext(), (consultant, position) -> {
            // set the selected consultant
            next(consultant);
        });

        binding.rvConsultants.setAdapter(adapter);
        binding.rvConsultants.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvConsultants.setAnimation(new AnimationSet(true));
    }

    void next(User consultant) {
        ConsultationAppointmentsActivity activity = (ConsultationAppointmentsActivity) getActivity();
        if (activity != null) {
            activity.setConsultant(consultant);
            activity.showFragment(ConsultationAppointments.ConsultationAppointmentsSelectDateTimeFragment);
        }
    }

    void filterConsultantsBySkill() {
        if (consultants == null || consultants.isEmpty()) return;
        if (getContext() == null) return;

        Date threeMonthsAgo = new Date(System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000);

        consultants = (ArrayList<User>) consultants.stream()
                .filter(consultant -> consultant.getStatus().contains(skillName) && consultant.isLastLogin(threeMonthsAgo))
                .sorted((c1, c2) -> (int) (c1.getUserRatings() - c2.getUserRatings()))
                .collect(Collectors.toList());
    }

    void getConsultants() {

        binding.progressBar.setVisibility(View.VISIBLE);
        // get consultants from the server
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<User>> call = apiInterface.getConsultants(TokenHelper.getBearerToken());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    consultants = new ArrayList<>(response.body());
                    initRecyclerView();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Log.e("ConsultationAppointmentsSelectConsultantFragment", "Error message", t);
            }
        });
    }


}