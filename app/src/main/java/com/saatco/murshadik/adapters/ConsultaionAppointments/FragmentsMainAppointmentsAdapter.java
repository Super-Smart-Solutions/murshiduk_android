package com.saatco.murshadik.adapters.ConsultaionAppointments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.firebase.database.annotations.NotNull;
import com.saatco.murshadik.fragments.consultationAppointments.ConsultationAppointmentsNextAppointmentsFragment;
import com.saatco.murshadik.fragments.consultationAppointments.ConsultationAppointmentsPastAppointmentsFragment;

public class FragmentsMainAppointmentsAdapter extends FragmentStateAdapter {

    public FragmentsMainAppointmentsAdapter(@NonNull @NotNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int p) {
        if (p == 1) {
            return ConsultationAppointmentsPastAppointmentsFragment.newInstance();
        }
        return ConsultationAppointmentsNextAppointmentsFragment.newInstance();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}