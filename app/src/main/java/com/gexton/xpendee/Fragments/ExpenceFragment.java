package com.gexton.xpendee.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gexton.xpendee.Adapters.CategoriesListAdapter;
import com.gexton.xpendee.AddCategoryActivity;
import com.gexton.xpendee.R;
import com.gexton.xpendee.UpdateOrDeleteCategory;
import com.gexton.xpendee.model.CategoryBean;
import com.gexton.xpendee.util.Database;
import com.gexton.xpendee.util.RecyclerItemClickListener;

import java.util.ArrayList;

public class ExpenceFragment extends Fragment {
    ArrayList<CategoryBean> categoryBeanArrayList;
    CategoriesListAdapter adapter = null;
    RecyclerView rvCategories;
    Database database;
    TextView tv_add_category;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_expence, container, false);
        tv_add_category = view.findViewById(R.id.tv_add_category);
        rvCategories = view.findViewById(R.id.rvCategoriesList);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        database = new Database(getContext());

        categoryBeanArrayList = new ArrayList<>();

        if (database.getAllCategories(1) != null) {
            categoryBeanArrayList = database.getAllCategories(1);
            adapter = new CategoriesListAdapter(getContext(), categoryBeanArrayList);
            rvCategories.setAdapter(adapter);
        }

        rvCategories.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), rvCategories, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(getContext(), UpdateOrDeleteCategory.class);
                int id = categoryBeanArrayList.get(position).id;
                String category_name = categoryBeanArrayList.get(position).categoryName;
                int resID = categoryBeanArrayList.get(position).categoryIcon;
                String color_code = categoryBeanArrayList.get(position).categoryHashCode;
                int flag = categoryBeanArrayList.get(position).catFlag;
                intent.putExtra("id", id);
                intent.putExtra("category_name", category_name);
                intent.putExtra("resId", resID);
                intent.putExtra("color_code", color_code);
                intent.putExtra("flag", flag);
                intent.putExtra("position", position);
                startActivity(intent);

            }

            @Override
            public void onItemLongClick(View view, int position) {
                System.out.println("-- item long press pos: " + position);
            }
        }));

        tv_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddCategoryActivity.class);
                intent.putExtra("farment_value", "EXPENCE");
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (database.getAllCategories(2) != null) {
            categoryBeanArrayList = database.getAllCategories(1);
            adapter = new CategoriesListAdapter(getContext(), categoryBeanArrayList);
            rvCategories.setAdapter(adapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (database.getAllCategories(2) != null) {
            categoryBeanArrayList = database.getAllCategories(1);
            adapter = new CategoriesListAdapter(getContext(), categoryBeanArrayList);
            rvCategories.setAdapter(adapter);
        }
    }

}