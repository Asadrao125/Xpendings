package com.gexton.xpendings.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gexton.xpendings.adapters.CategoriesListAdapter;
import com.gexton.xpendings.AddCategoryActivity;
import com.gexton.xpendings.R;
import com.gexton.xpendings.UpdateOrDeleteCategory;
import com.gexton.xpendings.model.CategoryBean;
import com.gexton.xpendings.util.AdUtil;
import com.gexton.xpendings.util.Database;
import com.gexton.xpendings.util.RecyclerItemClickListener;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class ExpenceFragment extends Fragment {
    ArrayList<CategoryBean> categoryBeanArrayList;
    CategoriesListAdapter adapter = null;
    RecyclerView rvCategories;
    Database database;
    TextView tv_add_category;
    View view;
    AdView adView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_expence, container, false);
        tv_add_category = view.findViewById(R.id.tv_add_category);
        rvCategories = view.findViewById(R.id.rvCategoriesList);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        database = new Database(getContext());

        adView = view.findViewById(R.id.adView);
        AdUtil adUtil = new AdUtil(getActivity());
        adUtil.loadBannerAd(adView);

        categoryBeanArrayList = new ArrayList<>();

        if (database.getAllCategories(1) != null) {
            categoryBeanArrayList.clear();
            categoryBeanArrayList = database.getAllCategories(1);
            adapter = new CategoriesListAdapter(getContext(), categoryBeanArrayList);
            rvCategories.setAdapter(adapter);
        }

        rvCategories.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), rvCategories, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (database.getAllCategories(1) != null) {
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

        adapter = new CategoriesListAdapter(getContext(), categoryBeanArrayList);
        rvCategories.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (database.getAllCategories(1) != null) {
            categoryBeanArrayList.clear();
            categoryBeanArrayList = database.getAllCategories(1);
            adapter = new CategoriesListAdapter(getContext(), categoryBeanArrayList);
            rvCategories.setAdapter(adapter);
        }
    }
}