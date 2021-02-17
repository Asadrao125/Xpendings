package com.gexton.xpendee.adapters;


import com.gexton.xpendee.fragments.ExpenceFragment;
import com.gexton.xpendee.fragments.IncomeFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new ExpenceFragment();
        } else if (position == 1) {
            fragment = new IncomeFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = "Expence";
        } else if (position == 1) {
            title = "Income";
        }
        return title;
    }
}