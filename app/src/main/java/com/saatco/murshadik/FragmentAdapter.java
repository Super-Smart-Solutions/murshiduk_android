package com.saatco.murshadik.adapters;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.firebase.database.annotations.NotNull;
import com.saatco.murshadik.fragments.ConsultationFragment;
import com.saatco.murshadik.fragments.DashboardFragment;
import com.saatco.murshadik.fragments.MarketMainFragment;
import com.saatco.murshadik.fragments.NotificationFragment;
import com.saatco.murshadik.fragments.ProfileFragment;

public class FragmentAdapter extends FragmentStateAdapter {

    FragmentActivity fragmentActivity;
    public FragmentAdapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int p) {

        switch (p) {
            case 1:
                return new MarketMainFragment();
            case 2:
                return new ConsultationFragment();
            case 3:
                return new NotificationFragment();
            case 4:
                return new ProfileFragment();
            default:
                return new DashboardFragment();

        }
    }


    public Fragment getItem(@LayoutRes int id){
        return fragmentActivity.getSupportFragmentManager().findFragmentById(id);
    }



    @Override
    public int getItemCount() {
        return 5;
    }
}