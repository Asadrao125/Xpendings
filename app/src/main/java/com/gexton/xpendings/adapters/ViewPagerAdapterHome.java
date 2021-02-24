package com.gexton.xpendings.adapters;

import com.gexton.xpendings.fragments.HomeFragment;
import com.gexton.xpendings.fragments.MoreFragment;
import com.gexton.xpendings.fragments.NewBudgetFragment;
import com.gexton.xpendings.fragments.NewTimelineFragment;
import com.gexton.xpendings.fragments.NotificationFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapterHome extends FragmentPagerAdapter {

    public ViewPagerAdapterHome(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new HomeFragment();
        } else if (position == 1) {
            fragment = new NewTimelineFragment();
        } else if (position == 2) {
            fragment = new NewBudgetFragment();
        } else if (position == 3) {
            fragment = new NotificationFragment();
        } else if (position == 4) {
            fragment = new MoreFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = "";
        } else if (position == 1) {
            title = "";
        } else if (position == 2) {
            title = "";
        } else if (position == 3) {
            title = "";
        } else if (position == 4) {
            title = "";
        }
        return title;
    }

}
