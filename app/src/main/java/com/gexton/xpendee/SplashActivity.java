package com.gexton.xpendee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;

import com.gexton.xpendee.model.CategoryBean;
import com.gexton.xpendee.util.Database;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 500;
    Database database;
    ArrayList<CategoryBean> categoryBeanArrayListPD;
    ArrayList<CategoryBean> newList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        database = new Database(getApplicationContext());
        newList = new ArrayList<>();
        categoryBeanArrayListPD = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    private void savingPredefinedCategories() {
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

}