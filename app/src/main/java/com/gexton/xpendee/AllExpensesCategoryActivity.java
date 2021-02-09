package com.gexton.xpendee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.xpendee.Adapters.CategoriesAdapterForExpense;
import com.gexton.xpendee.Adapters.CategoriesAdapterForSelectCategoryBudget;
import com.gexton.xpendee.model.CategoryBean;
import com.gexton.xpendee.util.Database;
import com.gexton.xpendee.util.RecyclerItemClickListener;

import java.util.ArrayList;

public class AllExpensesCategoryActivity extends AppCompatActivity {
    ImageView img_back;
    TextView tv_save;
    RecyclerView rvCategories;
    Database database;
    ArrayList<CategoryBean> categoryBeanArrayList;
    CategoriesAdapterForSelectCategoryBudget adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_expenses_category);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.navy_blue, this.getTheme()));
        }

        img_back = findViewById(R.id.img_back);
        tv_save = findViewById(R.id.tv_save);
        rvCategories = findViewById(R.id.rvCategories);
        database = new Database(getApplicationContext());

        int numberOfColumns = 1;
        RecyclerView.LayoutManager mLayoutManagerRVBP = new GridLayoutManager(getApplicationContext(), numberOfColumns);
        rvCategories.setLayoutManager(mLayoutManagerRVBP);

        if (database.getAllCategories(1) != null) {
            categoryBeanArrayList = database.getAllCategories(1);
            adapter = new CategoriesAdapterForSelectCategoryBudget(this, categoryBeanArrayList);
            rvCategories.setAdapter(adapter);
            rvCategories.setVisibility(View.VISIBLE);
        }

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AllExpensesCategoryActivity.this, "Save", Toast.LENGTH_SHORT).show();
            }
        });
    }
}