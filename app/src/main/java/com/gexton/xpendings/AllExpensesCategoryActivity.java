package com.gexton.xpendings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.xpendings.adapters.CategoriesAdapterForSelectCategoryBudget;
import com.gexton.xpendings.model.CategoryBean;
import com.gexton.xpendings.util.Database;
import com.gexton.xpendings.util.RecyclerItemClickListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class AllExpensesCategoryActivity extends AppCompatActivity {
    ImageView img_back;
    TextView tv_save;
    RecyclerView rvCategories;
    Database database;
    ArrayList<CategoryBean> categoryBeanArrayList;
    CategoriesAdapterForSelectCategoryBudget adapter = null;
    int id;
    SharedPreferences.Editor prefsEditor;
    ArrayList<String> arrayListId;
    SharedPreferences prefs;
    TextView no_category;

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
        arrayListId = new ArrayList<>();
        no_category = findViewById(R.id.no_category);
        prefsEditor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
        prefs = getApplicationContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);

        int numberOfColumns = 1;
        RecyclerView.LayoutManager mLayoutManagerRVBP = new GridLayoutManager(getApplicationContext(), numberOfColumns);
        rvCategories.setLayoutManager(mLayoutManagerRVBP);

        if (database.getAllCategories(1) != null) {

            Log.d("categories_list", "onCreate: " + database.getAllCategories(1));
            categoryBeanArrayList = database.getAllCategories(1);
            adapter = new CategoriesAdapterForSelectCategoryBudget(this, categoryBeanArrayList);
            rvCategories.setAdapter(adapter);
            rvCategories.setVisibility(View.VISIBLE);
            no_category.setVisibility(View.GONE);

        } else {
            rvCategories.setVisibility(View.GONE);
            no_category.setVisibility(View.VISIBLE);
        }

        rvCategories.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                rvCategories, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                id = categoryBeanArrayList.get(position).id;
                arrayListId.add(String.valueOf(id));
                Log.d("click_item_ids", "onItemClick: " + id);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                System.out.println("-- item long press pos: " + position);
            }
        }));

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayListId.size() > 0) {
                    Log.d("category_bean_id's", "Array List Id's: " + arrayListId.toString());
                    Gson gson = new Gson();
                    String jsonText = gson.toJson(arrayListId);
                    prefsEditor.putString("category_bean_id's", jsonText);
                    prefsEditor.apply();
                    Log.d("category_bean_id's", "Saving Data: Data Saved");
                    onBackPressed();
                } else {
                    Toast.makeText(AllExpensesCategoryActivity.this, "Please select category", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}