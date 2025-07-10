package com.saatco.murshadik.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.firebase.database.annotations.NotNull;
import com.saatco.murshadik.fragments.ConsultansFragment;
import com.saatco.murshadik.fragments.ConsultationFragment;
import com.saatco.murshadik.fragments.DashboardFragment;
import com.saatco.murshadik.fragments.DashboardV3Fragment;
import com.saatco.murshadik.fragments.MarketMainFragment;
import com.saatco.murshadik.fragments.NotificationFragment;
import com.saatco.murshadik.fragments.ProfileFragment;
import com.saatco.murshadik.fragments.ServicesFragment;
import com.saatco.murshadik.fragments.consultationAppointments.ConsultationAppointmentsMainFragment;

public class FragmentsNewUiDesignAdapter extends FragmentStateAdapter {

    public FragmentsNewUiDesignAdapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int p) {
        switch (p){
            case 1:
                return ServicesFragment.newInstance();
            case 2:
                return new ConsultationAppointmentsMainFragment();
            case 3:
                return new NotificationFragment();
            case 4:
                return new ProfileFragment();
            default:
                return new DashboardV3Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}