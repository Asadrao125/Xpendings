package com.gexton.xpendings.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gexton.xpendings.NewBudgetActivity;
import com.gexton.xpendings.R;
import com.gexton.xpendings.model.BudgetBean;
import com.gexton.xpendings.util.AdUtil;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class NewBudgetFragment extends Fragment {
    Button btnCreateBudget;
    View view;
    RelativeLayout static_layout_wallet, budget_complete;
    TextView tvBudgetName, tvRecurrance, tvBudgetAmount, tvBudgetPercentage;
    String MY_PREFS_NAME = "MY_PREFS_NAME", json;
    LinearLayout no_data_layout;
    ProgressBar progressBar;
    AdView adView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_budget, container, false);

        btnCreateBudget = view.findViewById(R.id.btnCreateBudget);
        static_layout_wallet = view.findViewById(R.id.static_layout_wallet);
        tvBudgetName = view.findViewById(R.id.tvBudgetName);
        tvRecurrance = view.findViewById(R.id.tvRecurrance);
        tvBudgetAmount = view.findViewById(R.id.tvBudgetAmount);
        no_data_layout = view.findViewById(R.id.no_data_layout);
        budget_complete = view.findViewById(R.id.budget_complete);
        progressBar = view.findViewById(R.id.progressBar);
        tvBudgetPercentage = view.findViewById(R.id.tvBudgetPercentage);

        adView = view.findViewById(R.id.adView);
        AdUtil adUtil = new AdUtil(getActivity());
        adUtil.loadBannerAd(adView);

        static_layout_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NewBudgetActivity.class));
            }
        });

        converSionAndSettingData();

        btnCreateBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NewBudgetActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        converSionAndSettingData();
    }

    public void converSionAndSettingData() {
        SharedPreferences prefs1 = getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        json = prefs1.getString("Budget_Bean", "");
        Gson gson = new Gson();
        BudgetBean budgetBean = gson.fromJson(json, BudgetBean.class);
        if (budgetBean != null) {
            tvBudgetName.setText(budgetBean.budgetName);
            tvBudgetAmount.setText("$" + budgetBean.amount);
            int maxProgress = (int) Math.round(budgetBean.amount);
            progressBar.setMax(100); //Maximum value for progress
            progressBar.setProgress(maxProgress); //Desired value for progress

            //Calculating percentage
            double percentage = ((budgetBean.amount * 100) / budgetBean.amount);
            tvBudgetPercentage.setText(percentage + " %");

            budget_complete.setVisibility(View.VISIBLE);
            no_data_layout.setVisibility(View.GONE);
        } else {
            no_data_layout.setVisibility(View.VISIBLE);
            budget_complete.setVisibility(View.GONE);
        }
    }
}