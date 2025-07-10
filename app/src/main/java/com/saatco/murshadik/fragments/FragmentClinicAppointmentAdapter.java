package com.saatco.murshadik.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.firebase.database.annotations.NotNull;

public class FragmentClinicAppointmentAdapter extends FragmentStateAdapter {

    public FragmentClinicAppointmentAdapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int p) {
        if (p == 1) {
            return new ClinicCurrentAppointmentHistoryFragment();
        }
        return new ClinicCurrentAppointmentFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}