package com.gexton.xpendee.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.xpendee.Adapters.SectionListViewAdapter;
import com.gexton.xpendee.AddExpenseActivity;
import com.gexton.xpendee.R;
import com.gexton.xpendee.UpdateOrDeleteExpense;
import com.gexton.xpendee.model.ExpenseBean;
import com.gexton.xpendee.util.Database;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TimelineFragment extends Fragment {
    View view;
    LinearLayout listviewLayout;
    ListView sectionListView;
    public static ArrayList<ExpenseBean> expenseBeanArrayList;
    Database database;
    ArrayList<String> dateBeanArrayList;
    FloatingActionButton fab_add_expense;
    RelativeLayout no_data_layout;
    TextView tvDailyCashFlow, tvWealth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_timeline, container, false);

        sectionListView = view.findViewById(R.id.sectionListView);
        listviewLayout = view.findViewById(R.id.listviewLayout);
        fab_add_expense = view.findViewById(R.id.fab_add_expense);
        database = new Database(getContext());
        no_data_layout = view.findViewById(R.id.no_data_layout);
        tvDailyCashFlow = view.findViewById(R.id.tvDailyCashFlow);
        tvWealth = view.findViewById(R.id.tvWealth);

        expenseBeanArrayList = new ArrayList<>();

        settingAddapter();

        fab_add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddExpenseActivity.class));
            }
        });

        sectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), UpdateOrDeleteExpense.class);
                intent.putExtra("position", i);
                startActivity(intent);
            }
        });

        if (expenseBeanArrayList != null) {
            Log.d("sum_of_expenses", "onCreateView: " + sumExpense(expenseBeanArrayList));
            tvWealth.setText("-$" + sumExpense(expenseBeanArrayList));
        }
        return view;
    }

    private void settingAddapter() {
        if (database.getAllExpenses() != null && database.getAllExpensesDates() != null) {
            expenseBeanArrayList = database.getAllExpenses();
            dateBeanArrayList = database.getAllExpensesDates();
            sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, dateBeanArrayList, getContext()));
        } else {
            no_data_layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        settingAddapter();
        if (expenseBeanArrayList != null) {
            Log.d("sum_of_expenses", "onCreateView: " + sumExpense(expenseBeanArrayList));
            tvWealth.setText("-$" + sumExpense(expenseBeanArrayList));
        }
    }

    public double sumExpense(ArrayList<ExpenseBean> list) {
        double sum = 0;
        for (ExpenseBean j : list) {
            sum = sum + j.expense;
        }
        return sum;
    }

}