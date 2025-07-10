package com.saatco.murshadik.fragments.consultationAppointments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saatco.murshadik.ConsultationAppointmentsActivity;
import com.saatco.murshadik.R;
import com.saatco.murshadik.databinding.FragmentConsultationAppointmentsReasonBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsultationAppointmentsReasonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultationAppointmentsReasonFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_REASON = "reason";

    private String reason = "";
    private FragmentConsultationAppointmentsReasonBinding binding;

    public ConsultationAppointmentsReasonFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param reason Parameter 1.
     * @return A new instance of fragment ConsultationAppointmentsReasonFragment.
     */
    public static ConsultationAppointmentsReasonFragment newInstance(String reason) {
        ConsultationAppointmentsReasonFragment fragment = new ConsultationAppointmentsReasonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REASON, reason);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reason = getArguments().getString(ARG_REASON);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentConsultationAppointmentsReasonBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConsultationAppointmentsActivity activity = (ConsultationAppointmentsActivity) getActivity();
        if (activity != null) {
            binding.appBar.btnBack.setOnClickListener(v -> activity.back());
            binding.appBar.toolbarTitle.setText(getString(R.string.more_information));
            binding.appBar.btnCancel.setOnClickListener(v -> activity.close());
        }

        binding.etReason.setText(reason);

        binding.btnNext.setOnClickListener(v -> {
            reason = binding.etReason.getText().toString();
            if (reason.isEmpty()) {
                binding.etReason.setError(getString(R.string.empty_field));
                return;
            }

            next();
        });
    }

    void next() {
        ConsultationAppointmentsActivity activity = (ConsultationAppointmentsActivity) getActivity();
        if (activity != null) {
            activity.setReason(reason);
            activity.showFragment(ConsultationAppointments.ConsultationAppointmentsSelectConsultantFragment);
        }
    }




}