package com.saatco.murshadik.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.firebase.database.annotations.NotNull;
import com.saatco.murshadik.fragments.ConsultansFragment;
import com.saatco.murshadik.fragments.DashboardFragment;
import com.saatco.murshadik.fragments.MarketMainFragment;
import com.saatco.murshadik.fragments.NotificationFragment;
import com.saatco.murshadik.fragments.ProfileFragment;

public class OtherFragmentsNewUiDesignAdapter extends FragmentStateAdapter {

    public OtherFragmentsNewUiDesignAdapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int p) {

        switch (p) {
            case 1:
                return new NotificationFragment();
            case 2:
                return new ProfileFragment();
            default:
                return new MarketMainFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}