package com.gexton.xpendings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.gexton.xpendings.adapters.ViewpagerAdapterVisiblistyCategories;
import com.google.android.material.tabs.TabLayout;

public class VisiblistyActivityForCategories extends AppCompatActivity {
    TabLayout tabs;
    ViewPager viewPager;
    ViewpagerAdapterVisiblistyCategories ViewpagerAdapterVisiblistyCategories;
    ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visiblisty_for_categories);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.navy_blue, this.getTheme()));
        }

        init();

        viewPager.setAdapter(ViewpagerAdapterVisiblistyCategories);
        tabs.setupWithViewPager(viewPager);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void init() {
        tabs = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewPager);
        ViewpagerAdapterVisiblistyCategories = new ViewpagerAdapterVisiblistyCategories(getSupportFragmentManager());
        img_back = findViewById(R.id.img_back);
    }

}