package com.gexton.xpendee.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    //ArrayList<CategoryBean> categoryBeanArrayListPD;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_expence, container, false);
        tv_add_category = view.findViewById(R.id.tv_add_category);
        rvCategories = view.findViewById(R.id.rvCategoriesList);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        database = new Database(getContext());

        categoryBeanArrayList = new ArrayList<>();
        //categoryBeanArrayListPD = new ArrayList<>();

       /* categoryBeanArrayListPD.add(new CategoryBean(1, "Beauty", R.mipmap.beauty, "#123456", 1));
        categoryBeanArrayListPD.add(new CategoryBean(2, "Bill", R.mipmap.bill, "#122134", 1));
        categoryBeanArrayListPD.add(new CategoryBean(3, "Car", R.mipmap.car, "#987986", 1));
        categoryBeanArrayListPD.add(new CategoryBean(4, "Education", R.mipmap.education, "#652731", 1));
        categoryBeanArrayListPD.add(new CategoryBean(5, "Entertain", R.mipmap.entertainment, "#095685", 1));
        categoryBeanArrayListPD.add(new CategoryBean(6, "Family", R.mipmap.family, "#123214", 1));
        categoryBeanArrayListPD.add(new CategoryBean(7, "Food", R.mipmap.food, "#601382", 1));*/

        if (database.getAllCategories(1) != null) {
            categoryBeanArrayList.clear();
            categoryBeanArrayList = database.getAllCategories(1);
            //categoryBeanArrayList.addAll(categoryBeanArrayListPD);
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
                    Toast.makeText(getContext(), "" + id, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                System.out.println("-- item long press pos: " + position);
            }
        }));

        //rvCategories.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

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
            //categoryBeanArrayList.addAll(categoryBeanArrayListPD);
            adapter = new CategoriesListAdapter(getContext(), categoryBeanArrayList);
            rvCategories.setAdapter(adapter);
        }
    }
}