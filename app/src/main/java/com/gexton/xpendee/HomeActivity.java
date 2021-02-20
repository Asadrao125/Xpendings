package com.gexton.xpendee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.gexton.xpendee.adapters.ViewPagerAdapterHome;
import com.gexton.xpendee.model.CategoryBean;
import com.gexton.xpendee.util.Database;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    TabLayout tabs;
    ViewPager viewPager;
    ViewPagerAdapterHome viewPagerAdapterHome;
    Database database;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    int indexVal;
    ArrayList<CategoryBean> categoryBeanArrayListPD;
    ArrayList<CategoryBean> newList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        database = new Database(HomeActivity.this);
        database.createDatabase();
        editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();

        categoryBeanArrayListPD = new ArrayList<>();
        newList = new ArrayList<>();

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

        //Saving Predefined Data
        // Expense
        categoryBeanArrayListPD.add(new CategoryBean(1, "Beauty", R.drawable.beauty, "#7944d0", 1, 1));
        categoryBeanArrayListPD.add(new CategoryBean(2, "Entertainment", R.drawable.entertainment, "#ffa801", 1, 1));
        categoryBeanArrayListPD.add(new CategoryBean(3, "Food and Drink", R.drawable.food, "#ffa801", 1, 1));
        categoryBeanArrayListPD.add(new CategoryBean(4, "Gifts", R.drawable.gift, "#18b272", 1, 1));
        categoryBeanArrayListPD.add(new CategoryBean(5, "Groceries", R.drawable.grocery, "#dd8138", 1, 1));
        categoryBeanArrayListPD.add(new CategoryBean(6, "Health Care", R.drawable.family, "#e16476", 1, 1));
        categoryBeanArrayListPD.add(new CategoryBean(7, "Home", R.drawable.home, "#b6985c", 1, 1));
        categoryBeanArrayListPD.add(new CategoryBean(7, "Sport & Hobbies", R.drawable.sport, "#60d0ca", 1, 1));
        categoryBeanArrayListPD.add(new CategoryBean(7, "Transport", R.drawable.travel, "#fcce00", 1, 1));
        categoryBeanArrayListPD.add(new CategoryBean(7, "Work", R.drawable.work, "#6d6e8a", 1, 1));

        //Income
        categoryBeanArrayListPD.add(new CategoryBean(8, "Business", R.drawable.gift, "#ffa200", 2, 1));
        categoryBeanArrayListPD.add(new CategoryBean(9, "Extra Income", R.drawable.grocery, "#72c541", 2, 1));
        categoryBeanArrayListPD.add(new CategoryBean(10, "Insurance Payout", R.drawable.home, "#18b272", 2, 1));
        categoryBeanArrayListPD.add(new CategoryBean(11, "Loan", R.drawable.other, "#45a7e6", 2, 1));

        if (database.getAllCategories(1) != null && database.getAllCategories(2) != null) {
            newList = database.getAllCategories(2);
            if (newList.get(3).id == 11) {
                Log.d("kdkjdkkjdk", "onCreate: nckldlklklsklksljkkn");
            }
        } else {
            for (int i = 0; i < categoryBeanArrayListPD.size(); i++) {
                Log.d("predefined_data", "Data Inserted At: " + database.insertCategory(categoryBeanArrayListPD.get(i)));
            }
        }
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
    }

    public void permissionCheck() {

        boolean shouldShowRequestPersmissionRational = ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        System.out.println("-- shouldShowRequestPersmissionRational: " + shouldShowRequestPersmissionRational);

        boolean isPermissionGranted = ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        System.out.println("-- isPermissionGranted: " + isPermissionGranted);

        Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Log.d("TAG", "onPermissionsChecked: Permission Granted");
                            //Toast.makeText(HomeActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
}