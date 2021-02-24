package com.gexton.xpendings.adapters;

import com.gexton.xpendings.fragments.VisiblistyExpenseFragment;
import com.gexton.xpendings.fragments.VisiblistyIncomeFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewpagerAdapterVisiblistyCategories extends FragmentPagerAdapter {

    public ViewpagerAdapterVisiblistyCategories(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new VisiblistyExpenseFragment();
        } else if (position == 1) {
            fragment = new VisiblistyIncomeFragment();
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
