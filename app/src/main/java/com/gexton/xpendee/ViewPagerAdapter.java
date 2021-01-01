package com.gexton.xpendee;


import com.gexton.xpendee.Fragments.HomeFragment;
import com.gexton.xpendee.Fragments.TimelineFragment;

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
            title = "Expences";
        } else if (position == 1) {
            title = "Income";
        }
        return title;
    }
}