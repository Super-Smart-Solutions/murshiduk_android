package com.saatco.murshadik.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.saatco.murshadik.fragments.MarketMapFragment;
import com.saatco.murshadik.fragments.MarketsFragment;

import org.jetbrains.annotations.NotNull;

public class MarketsFragmentsAdapter extends FragmentStateAdapter {

        Bundle bundle;
        public MarketsFragmentsAdapter(@NonNull FragmentActivity fragment, Bundle bundle) {
            super(fragment);
            this.bundle = bundle;
        }

        public void setBundle(Bundle bundle) {
            this.bundle = bundle;
        }

        @NonNull
        @NotNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 1) {
                return MarketMapFragment.newInstance(bundle);
            }
            return MarketsFragment.newInstance(bundle);
        }

        @Override
        public int getItemCount() {
            return 2;
        }
}
