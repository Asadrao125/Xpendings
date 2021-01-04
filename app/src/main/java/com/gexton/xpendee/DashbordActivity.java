package com.gexton.xpendee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.gexton.xpendee.Fragments.HomeFragment;
import com.gexton.xpendee.Fragments.MoreFragment;
import com.gexton.xpendee.Fragments.NewBudgetFragment;
import com.gexton.xpendee.Fragments.NotificationFragment;
import com.gexton.xpendee.Fragments.TimelineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class DashbordActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashbord);

        bottomNav = (BottomNavigationView) findViewById(R.id.navigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        HomeFragment frag_name = new HomeFragment();
        FragmentManager manager = this.getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.container, frag_name, frag_name.getTag()).commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.home_home:
                            replaceFragment(new HomeFragment());
                            break;
                        case R.id.timeline:
                            replaceFragment(new TimelineFragment());
                            break;
                        case R.id.new_budget:
                            replaceFragment(new NewBudgetFragment());
                            break;
                        case R.id.notifications:
                            replaceFragment(new NotificationFragment());
                            break;
                        case R.id.more:
                            replaceFragment(new MoreFragment());
                            break;
                    }
                    return true;
                }
            };

    private void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (bottomNav.getSelectedItemId() == R.id.home_home) {
            super.onBackPressed();
        } else {
            bottomNav.setSelectedItemId(R.id.home_home);
            bottomNav.getMenu().getItem(0).setChecked(true);
        }
    }
}