package com.gexton.xpendee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gexton.xpendee.Adapters.ViewPagerAdapter;
import com.gexton.xpendee.Adapters.ViewPagerAdapterHome;
import com.gexton.xpendee.Fragments.HomeFragment;
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

        //Expense
        categoryBeanArrayListPD.add(new CategoryBean(1, "Beauty", R.mipmap.beauty, "#123456", 1));
        categoryBeanArrayListPD.add(new CategoryBean(2, "Bill", R.mipmap.bill, "#122134", 1));
        categoryBeanArrayListPD.add(new CategoryBean(3, "Car", R.mipmap.car, "#987986", 1));
        categoryBeanArrayListPD.add(new CategoryBean(4, "Education", R.mipmap.education, "#652731", 1));
        categoryBeanArrayListPD.add(new CategoryBean(5, "Entertain", R.mipmap.entertainment, "#095685", 1));
        categoryBeanArrayListPD.add(new CategoryBean(6, "Family", R.mipmap.family, "#123214", 1));
        categoryBeanArrayListPD.add(new CategoryBean(7, "Food", R.mipmap.food, "#601382", 1));

        //Income
        categoryBeanArrayListPD.add(new CategoryBean(8, "Gift", R.mipmap.gift, "#957043", 2));
        categoryBeanArrayListPD.add(new CategoryBean(9, "Grocery", R.mipmap.grocery, "#123456", 2));
        categoryBeanArrayListPD.add(new CategoryBean(10, "Home", R.mipmap.home, "#654321", 2));
        categoryBeanArrayListPD.add(new CategoryBean(11, "Others", R.mipmap.other, "#987654", 2));

        if (database.getAllCategories(1) != null && database.getAllCategories(2) != null) {
            newList = database.getAllCategories(2);
            if (newList.get(3).id != 11) {
                for (int i = 0; i < categoryBeanArrayListPD.size(); i++) {
                    Log.d("predefined_data", "Data Inserted At: " + database.insertCategory(categoryBeanArrayListPD.get(i)));
                }
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
        if (indexVal == 0 || indexVal >= 0) {
            if (indexVal == 0) {
                //viewPager.setCurrentItem(0, true);
            } else if (indexVal == 1) {
                //viewPager.setCurrentItem(1, true);
            } else if (indexVal == 2) {
                //viewPager.setCurrentItem(2, true);
            } else if (indexVal == 3) {
                //viewPager.setCurrentItem(3, true);
            } else if (indexVal == 4) {
                //viewPager.setCurrentItem(4, true);
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