package com.gexton.xpendee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.gexton.xpendee.Adapters.ViewPagerAdapter;
import com.gexton.xpendee.Adapters.ViewPagerAdapterHome;
import com.google.android.material.tabs.TabLayout;

public class HomeActivity extends AppCompatActivity {
    TabLayout tabs;
    ViewPager viewPager;
    ViewPagerAdapterHome viewPagerAdapterHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tabs = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapterHome = new ViewPagerAdapterHome(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapterHome);
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(0).setIcon(R.drawable.home_green);
        tabs.getTabAt(1).setIcon(R.drawable.timeline_green);
        tabs.getTabAt(2).setIcon(R.drawable.budget_green);
        tabs.getTabAt(3).setIcon(R.drawable.notification_green);
        tabs.getTabAt(4).setIcon(R.drawable.more_green);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(0);
        }
    }

}