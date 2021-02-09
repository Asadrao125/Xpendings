package com.gexton.xpendee.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
    ImageView img_calender;
    private AlertDialog alertDialog;
    String TAG = "TimelineFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_timeline, container, false);

        sectionListView = view.findViewById(R.id.sectionListView);
        listviewLayout = view.findViewById(R.id.listviewLayout);
        fab_add_expense = view.findViewById(R.id.fab_add_expense);
        database = new Database(getContext());
        no_data_layout = view.findViewById(R.id.no_data_layout);
        tvDailyCashFlow = view.findViewById(R.id.tvDailyCashFlow);
        tvWealth = view.findViewById(R.id.tvWealth);
        expenseBeanArrayList = new ArrayList<>();
        img_calender = view.findViewById(R.id.img_calender);

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

        img_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });

        return view;
    }

    private void settingAddapter() {
        //getAllExpenses()
        if (database.getAllExpensesFlag(1) != null && database.getAllExpensesDates() != null) {
            expenseBeanArrayList = database.getAllExpensesFlag(1);
            dateBeanArrayList = database.getAllExpensesDates();
            sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, dateBeanArrayList, getContext()));
            if (expenseBeanArrayList != null) {
                Log.d("sum_of_expenses", "onCreateView: " + sumExpense(expenseBeanArrayList));
                tvWealth.setText("-$" + sumExpense(expenseBeanArrayList));
            }
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

    private void showFilterDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.dialog_for_filtering_expenses, null);

        CheckBox cbDaily = v.findViewById(R.id.cbDaily);
        CheckBox cbWeekly = v.findViewById(R.id.cbWeekly);
        CheckBox cbMonthly = v.findViewById(R.id.cbMonthly);
        CheckBox cbYearly = v.findViewById(R.id.cbYearly);
        CheckBox cbAllTime = v.findViewById(R.id.cbAllTime);

        RelativeLayout daily_layout = v.findViewById(R.id.daily_layout);
        RelativeLayout weekly_layout = v.findViewById(R.id.weekly_layout);
        RelativeLayout monthly_layout = v.findViewById(R.id.monthly_layout);
        RelativeLayout yearly_layout = v.findViewById(R.id.yearly_layout);
        RelativeLayout all_time_layout = v.findViewById(R.id.all_time_layout);
        RelativeLayout dialog_filter_layout = v.findViewById(R.id.dialog_filter_layout);

        dialog_filter_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        daily_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbDaily.isChecked()) {
                    cbDaily.setChecked(false);
                } else {
                    cbDaily.setChecked(true);
                    cbMonthly.setChecked(false);
                    cbWeekly.setChecked(false);
                    cbYearly.setChecked(false);
                    cbAllTime.setChecked(false);

                    String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
                    expenseBeanArrayList = database.getAllDailyExpenses(formattedDate);
                    dateBeanArrayList = database.getAllExpensesDates();
                    if (expenseBeanArrayList != null) {
                        Log.d(TAG, "getAllDailyExpenses: " + expenseBeanArrayList);
                        sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, dateBeanArrayList, getContext()));
                        sumExpense(expenseBeanArrayList);
                        tvWealth.setText("-$" + sumExpense(expenseBeanArrayList));
                    } else {
                        Toast.makeText(getContext(), "No Record Found", Toast.LENGTH_SHORT).show();
                    }
                    //alertDialog.dismiss();
                }
            }
        });

        weekly_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbWeekly.isChecked()) {
                    cbWeekly.setChecked(false);
                } else {
                    cbWeekly.setChecked(true);
                    cbDaily.setChecked(false);
                    cbMonthly.setChecked(false);
                    cbYearly.setChecked(false);
                    cbAllTime.setChecked(false);
                    Log.d(TAG, "getAllWeeklyExpenses: " + database.getAllWeeklyExpenses());
                    //alertDialog.dismiss();
                }
            }
        });

        monthly_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbMonthly.isChecked()) {
                    cbMonthly.setChecked(false);
                } else {
                    cbMonthly.setChecked(true);
                    cbDaily.setChecked(false);
                    cbWeekly.setChecked(false);
                    cbYearly.setChecked(false);
                    cbAllTime.setChecked(false);
                    //alertDialog.dismiss();
                }
            }
        });

        yearly_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbYearly.isChecked()) {
                    cbYearly.setChecked(false);
                } else {
                    cbYearly.setChecked(true);
                    cbMonthly.setChecked(false);
                    cbDaily.setChecked(false);
                    cbWeekly.setChecked(false);
                    cbAllTime.setChecked(false);
                    //alertDialog.dismiss();
                }
            }
        });

        all_time_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbAllTime.isChecked()) {
                    cbAllTime.setChecked(false);
                } else {
                    cbAllTime.setChecked(true);
                    cbMonthly.setChecked(false);
                    cbDaily.setChecked(false);
                    cbWeekly.setChecked(false);
                    cbYearly.setChecked(false);
                    settingAddapter();
                }
            }
        });

        alertDialog = new AlertDialog.Builder(getContext()).setView(v).create();

        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setAttributes(wlp);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }
}