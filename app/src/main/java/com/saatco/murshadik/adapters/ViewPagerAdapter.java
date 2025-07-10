package com.saatco.murshadik.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    private final FragmentManager fragmentManager;
    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
        this.fragmentManager = manager;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public void removeAll(){
        mFragmentList.clear();
        mFragmentTitleList.clear();
    }

    public void clear() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (Fragment fragment : mFragmentList) {
            transaction.remove(fragment);
        }
        mFragmentList.clear();
        transaction.commitAllowingStateLoss();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}