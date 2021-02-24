package com.gexton.xpendings.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gexton.xpendings.adapters.VisiblistyCategoriesListAdapter;
import com.gexton.xpendings.R;
import com.gexton.xpendings.model.CategoryBean;
import com.gexton.xpendings.util.AdUtil;
import com.gexton.xpendings.util.Database;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class VisiblistyIncomeFragment extends Fragment {
    View view;
    AdView adView;
    Database database;
    RecyclerView rvCategories;
    VisiblistyCategoriesListAdapter VisiblistyCategoriesListAdapter = null;
    ArrayList<CategoryBean> categoryBeanArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_visiblisty_income, container, false);

        rvCategories = view.findViewById(R.id.rvCategoriesList);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        database = new Database(getContext());

        adView = view.findViewById(R.id.adView);
        AdUtil adUtil = new AdUtil(getActivity());
        adUtil.loadBannerAd(adView);

        if (database.getAllCategories(2) != null) {
            categoryBeanArrayList = database.getAllCategories(2);
            VisiblistyCategoriesListAdapter = new VisiblistyCategoriesListAdapter(getContext(), categoryBeanArrayList);
            rvCategories.setAdapter(VisiblistyCategoriesListAdapter);
        }

        VisiblistyCategoriesListAdapter = new VisiblistyCategoriesListAdapter(getContext(), categoryBeanArrayList);
        rvCategories.setAdapter(VisiblistyCategoriesListAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (database.getAllCategories(2) != null) {
            categoryBeanArrayList.clear();
            categoryBeanArrayList = database.getAllCategories(2);
            VisiblistyCategoriesListAdapter = new VisiblistyCategoriesListAdapter(getContext(), categoryBeanArrayList);
            rvCategories.setAdapter(VisiblistyCategoriesListAdapter);
        }
    }
}