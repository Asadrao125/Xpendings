package com.gexton.xpendings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gexton.xpendings.adapters.SectionListViewAdapter;
import com.gexton.xpendings.model.ExpenseBean;
import com.gexton.xpendings.util.AdUtil;
import com.gexton.xpendings.util.Database;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class CategoryWiseTransactionActivity extends AppCompatActivity {
    String category_name;
    Database database;
    ListView sectionListView;
    TextView tvToolbar;
    ImageView imgBack;
    ArrayList<ExpenseBean> expenseBeanArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_wise_transaction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.navy_blue, this.getTheme()));
        }

        category_name = getIntent().getStringExtra("category_name");
        database = new Database(this);
        expenseBeanArrayList = new ArrayList<>();
        sectionListView = findViewById(R.id.sectionListView);
        tvToolbar = findViewById(R.id.tvToolbar);
        imgBack = findViewById(R.id.imgBack);

        tvToolbar.setText(category_name);

        AdView adView = findViewById(R.id.adView);
        AdUtil adUtil = new AdUtil(this);
        adUtil.loadBannerAd(adView);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (database.getExpenseByName(category_name) != null) {
            expenseBeanArrayList = database.getExpenseByName(category_name);
            sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, getApplicationContext()));
        }
    }
}