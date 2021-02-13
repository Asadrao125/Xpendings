package com.gexton.xpendee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.gexton.xpendee.Adapters.ViewPagerAdapter;
import com.gexton.xpendee.Adapters.ViewPagerAdapterHome;
import com.gexton.xpendee.Fragments.HomeFragment;
import com.gexton.xpendee.util.Database;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    TabLayout tabs;
    ViewPager viewPager;
    ViewPagerAdapterHome viewPagerAdapterHome;
    Database database;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    int indexVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        database = new Database(HomeActivity.this);
        database.createDatabase();
        editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.navy_blue, this.getTheme()));
        }

        tabs = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapterHome = new ViewPagerAdapterHome(getSupportFragmentManager());

        prefs = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        indexVal = prefs.getInt("index", 1000);

        viewPager.setAdapter(viewPagerAdapterHome);
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(0).setIcon(R.drawable.home_green);
        tabs.getTabAt(1).setIcon(R.drawable.timeline_green);
        tabs.getTabAt(2).setIcon(R.drawable.budget_green);
        tabs.getTabAt(3).setIcon(R.drawable.notification_green);
        tabs.getTabAt(4).setIcon(R.drawable.more_green);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    editor.putInt("index", 0);
                    editor.apply();
                } else if (tab.getPosition() == 1) {
                    editor.putInt("index", 1);
                    editor.apply();
                } else if (tab.getPosition() == 2) {
                    editor.putInt("index", 2);
                    editor.apply();
                } else if (tab.getPosition() == 3) {
                    editor.putInt("index", 3);
                    editor.apply();
                } else if (tab.getPosition() == 4) {
                    editor.putInt("index", 4);
                    editor.apply();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        permissionCheck();
        if (indexVal >= 0) {
            if (indexVal == 1) {
                viewPager.setCurrentItem(1, true);
            } else if (indexVal == 2) {
                viewPager.setCurrentItem(2, true);
            } else if (indexVal == 3) {
                viewPager.setCurrentItem(3, true);
            } else if (indexVal == 4) {
                viewPager.setCurrentItem(4, true);
            }
        }
    }

    public void permissionCheck() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(HomeActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

}