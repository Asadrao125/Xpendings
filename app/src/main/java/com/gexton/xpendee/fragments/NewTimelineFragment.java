package com.gexton.xpendee.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gexton.xpendee.HomeActivity;
import com.gexton.xpendee.adapters.SectionListViewAdapter;
import com.gexton.xpendee.AddExpenseActivity;
import com.gexton.xpendee.R;
import com.gexton.xpendee.SearchActivity;
import com.gexton.xpendee.UpdateOrDeleteExpense;
import com.gexton.xpendee.model.ExpenseBean;
import com.gexton.xpendee.model.WalletBean;
import com.gexton.xpendee.util.Database;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class NewTimelineFragment extends Fragment {
    View view;
    String json;
    Database database;
    String currentDate;
    String filter_value;
    View transparentView;
    ImageView img_search;
    ImageView img_calender;
    SharedPreferences prefs;
    ListView sectionListView;
    CheckBox cbDaily, cbAllTime;
    Calendar defaultSelectedDate;
    View viewBelowCashFlowLayout;
    LinearLayout cashflow_layout;
    SharedPreferences.Editor editor;
    String MY_PREFS_NAME = "Xpendee";
    String TAG = "TimelineFragmentTag";
    TextView tvWealth, tvDailyCashFlow;
    FloatingActionButton fab_add_expense;
    HorizontalCalendar horizontalCalendar;
    public static ArrayList<ExpenseBean> expenseBeanArrayList;
    RelativeLayout filter_layout, daily_layout, all_time_layout, parentLayout, no_data_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_new_timeline, container, false);

        init();

        listeners();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                String dateNew = DateFormat.format("dd-MM-yyyy", date).toString();

                if (database.getAllDailyExpenses(dateNew) != null) {
                    expenseBeanArrayList = database.getAllDailyExpenses(dateNew);
                    sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, getContext()));
                    sectionListView.setVisibility(View.VISIBLE);
                    cashflow_layout.setVisibility(View.VISIBLE);
                    viewBelowCashFlowLayout.setVisibility(View.VISIBLE);
                    tvWealth.setText("$ -" + sumExpense(expenseBeanArrayList));

                    double newDailyCashFlow = sumIncome(expenseBeanArrayList) - sumExpense(expenseBeanArrayList);

                    tvDailyCashFlow.setText("$ " + newDailyCashFlow);
                    no_data_layout.setVisibility(View.GONE);
                } else {
                    no_data_layout.setVisibility(View.VISIBLE);
                    sectionListView.setVisibility(View.GONE);
                    cashflow_layout.setVisibility(View.GONE);
                    viewBelowCashFlowLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView, int dx, int dy) {

            }

            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                return true;
            }
        });

        checkWalletExistance();

        return view;

    }

    private void checkWalletExistance() {
        SharedPreferences prefs1 = getContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        json = prefs1.getString("Wallet_Bean", "");
        Gson gson = new Gson();
        WalletBean walletBean = gson.fromJson(json, WalletBean.class);
        if (walletBean != null) {
            fab_add_expense.setVisibility(View.VISIBLE);
        } else {
            fab_add_expense.setVisibility(View.GONE);
        }
    }

    private void listeners() {

        img_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filter_layout.isShown()) {
                    filter_layout.setVisibility(View.GONE);
                    transparentView.setVisibility(View.GONE);
                    fab_add_expense.setVisibility(View.VISIBLE);
                } else {
                    filter_layout.setVisibility(View.VISIBLE);
                    transparentView.setVisibility(View.VISIBLE);
                    fab_add_expense.setVisibility(View.GONE);
                }
            }
        });

        transparentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filter_layout.isShown()) {
                    filter_layout.setVisibility(View.GONE);
                    transparentView.setVisibility(View.GONE);
                    fab_add_expense.setVisibility(View.VISIBLE);
                } else {
                    transparentView.setVisibility(View.GONE);
                    fab_add_expense.setVisibility(View.VISIBLE);
                }
            }
        });

        filter_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter_layout.setVisibility(View.GONE);
                transparentView.setVisibility(View.GONE);
                fab_add_expense.setVisibility(View.VISIBLE);
            }
        });

        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter_layout.setVisibility(View.GONE);
                transparentView.setVisibility(View.GONE);
                //fab_add_expense.setVisibility(View.VISIBLE);
            }
        });

        fab_add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddExpenseActivity.class));
            }
        });

        daily_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbDaily.isChecked()) {
                    cbDaily.setChecked(false);
                } else {
                    cbDaily.setChecked(true);
                    cbAllTime.setChecked(false);
                    editor.putString("filter", "daily");
                    editor.apply();
                    filter_layout.setVisibility(View.GONE);
                    transparentView.setVisibility(View.GONE);
                    fab_add_expense.setVisibility(View.VISIBLE);
                    horizontalCalendar.getCalendarView().setVisibility(View.VISIBLE);
                    sectionListView.setVisibility(View.VISIBLE);

                    if (database.getAllDailyExpenses(currentDate) != null) {
                        expenseBeanArrayList = database.getAllDailyExpenses(currentDate);
                        sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, getContext()));
                        sectionListView.setVisibility(View.VISIBLE);
                        cashflow_layout.setVisibility(View.VISIBLE);
                        viewBelowCashFlowLayout.setVisibility(View.VISIBLE);

                        tvWealth.setText("$ -" + sumExpense(expenseBeanArrayList));

                        double newDailyCashFlow = sumIncome(expenseBeanArrayList) - sumExpense(expenseBeanArrayList);

                        tvDailyCashFlow.setText("$ " + newDailyCashFlow);

                        horizontalCalendar.getCalendarView().setVisibility(View.VISIBLE);
                        no_data_layout.setVisibility(View.GONE);

                        startActivity(new Intent(getContext(), HomeActivity.class));
                        getActivity().finish();

                    } else {
                        no_data_layout.setVisibility(View.VISIBLE);
                        sectionListView.setVisibility(View.GONE);
                        cashflow_layout.setVisibility(View.GONE);
                        viewBelowCashFlowLayout.setVisibility(View.GONE);
                        horizontalCalendar.getCalendarView().setVisibility(View.GONE);
                    }

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
                    cbDaily.setChecked(false);
                    editor.putString("filter", "all_time");
                    editor.apply();
                    filter_layout.setVisibility(View.GONE);
                    transparentView.setVisibility(View.GONE);
                    fab_add_expense.setVisibility(View.VISIBLE);
                    horizontalCalendar.getCalendarView().setVisibility(View.GONE);
                    sectionListView.setVisibility(View.VISIBLE);
                    horizontalCalendar.getCalendarView().setVisibility(View.GONE);
                    if (database.getAllExpenses() != null) {
                        expenseBeanArrayList = database.getAllExpenses();
                        sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, getContext()));
                        sectionListView.setVisibility(View.VISIBLE);
                        cashflow_layout.setVisibility(View.VISIBLE);
                        viewBelowCashFlowLayout.setVisibility(View.VISIBLE);

                        tvWealth.setText("$ -" + sumExpense(expenseBeanArrayList));
                        double newDailyCashFlow = sumIncome(expenseBeanArrayList) - sumExpense(expenseBeanArrayList);
                        tvDailyCashFlow.setText("$ " + newDailyCashFlow);
                        no_data_layout.setVisibility(View.GONE);

                        startActivity(new Intent(getContext(), HomeActivity.class));
                        getActivity().finish();

                    } else {
                        no_data_layout.setVisibility(View.VISIBLE);
                        sectionListView.setVisibility(View.GONE);
                        cashflow_layout.setVisibility(View.GONE);
                        viewBelowCashFlowLayout.setVisibility(View.GONE);
                    }
                }
            }
        });

        sectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), UpdateOrDeleteExpense.class);
                if (expenseBeanArrayList.get(i).flag == 1) {
                    intent.putExtra("position", i);
                    intent.putExtra("val", "exp");
                    startActivity(intent);
                } else if (expenseBeanArrayList.get(i).flag == 2) {
                    intent.putExtra("position", i);
                    intent.putExtra("val", "inc");
                    startActivity(intent);
                }
            }
        });

        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SearchActivity.class));
            }
        });
    }

    @SuppressLint("CommitPrefEdits")
    private void init() {
        filter_layout = view.findViewById(R.id.filter_layout);
        daily_layout = view.findViewById(R.id.daily_layout);
        all_time_layout = view.findViewById(R.id.all_time_layout);
        img_calender = view.findViewById(R.id.img_calender);
        parentLayout = view.findViewById(R.id.parentLayout);
        fab_add_expense = view.findViewById(R.id.fab_add_expense);
        sectionListView = view.findViewById(R.id.sectionListView);
        cashflow_layout = view.findViewById(R.id.cashflow_layout);
        expenseBeanArrayList = new ArrayList<>();
        editor = getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        prefs = getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        cbDaily = view.findViewById(R.id.cbDaily);
        cbAllTime = view.findViewById(R.id.cbAllTime);
        filter_value = prefs.getString("filter", "");
        database = new Database(getContext());
        currentDate = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        no_data_layout = view.findViewById(R.id.no_data_layout);
        sectionListView = view.findViewById(R.id.sectionListView);
        tvWealth = view.findViewById(R.id.tvWealth);
        tvDailyCashFlow = view.findViewById(R.id.tvDailyCashFlow);
        viewBelowCashFlowLayout = view.findViewById(R.id.viewBelowCashFlowLayout);
        transparentView = view.findViewById(R.id.transparentView);
        img_search = view.findViewById(R.id.img_search);

        defaultSelectedDate = Calendar.getInstance();

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -2);

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 2);

        horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(3)
                .configure()
                .showTopText(true)
                .showBottomText(false)
                .textSize(/*Top*/14, /*Middle*/16, /*Bottom*/14)
                .end()
                .defaultSelectedDate(defaultSelectedDate)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!TextUtils.isEmpty(filter_value) && filter_value.equals("daily")) {

            if (database.getAllDailyExpenses(currentDate) != null) {
                expenseBeanArrayList = database.getAllDailyExpenses(currentDate);
                sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, getContext()));
                sectionListView.setVisibility(View.VISIBLE);
                cashflow_layout.setVisibility(View.VISIBLE);
                viewBelowCashFlowLayout.setVisibility(View.VISIBLE);

                tvWealth.setText("$ -" + sumExpense(expenseBeanArrayList));
                double newDailyCashFlow = sumIncome(expenseBeanArrayList) - sumExpense(expenseBeanArrayList);
                tvDailyCashFlow.setText("$ " + newDailyCashFlow);

                horizontalCalendar.getCalendarView().setVisibility(View.VISIBLE);
                no_data_layout.setVisibility(View.GONE);
            } else {
                no_data_layout.setVisibility(View.VISIBLE);
                sectionListView.setVisibility(View.GONE);
                cashflow_layout.setVisibility(View.GONE);
                viewBelowCashFlowLayout.setVisibility(View.GONE);
                horizontalCalendar.getCalendarView().setVisibility(View.GONE);
            }
            cbDaily.setChecked(true);

        } else if (!TextUtils.isEmpty(filter_value) && filter_value.equals("all_time")) {

            horizontalCalendar.getCalendarView().setVisibility(View.GONE);
            if (database.getAllExpenses() != null) {
                expenseBeanArrayList = database.getAllExpenses();
                sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, getContext()));
                sectionListView.setVisibility(View.VISIBLE);
                cashflow_layout.setVisibility(View.VISIBLE);
                viewBelowCashFlowLayout.setVisibility(View.VISIBLE);

                tvWealth.setText("$ -" + sumExpense(expenseBeanArrayList));
                double newDailyCashFlow = sumIncome(expenseBeanArrayList) - sumExpense(expenseBeanArrayList);
                tvDailyCashFlow.setText("$ " + newDailyCashFlow);

                no_data_layout.setVisibility(View.GONE);
            } else {
                no_data_layout.setVisibility(View.VISIBLE);
                sectionListView.setVisibility(View.GONE);
                cashflow_layout.setVisibility(View.GONE);
                viewBelowCashFlowLayout.setVisibility(View.GONE);
            }
            cbAllTime.setChecked(true);

        } else {
            cbAllTime.setChecked(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        filter_layout.setVisibility(View.GONE);
        transparentView.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(filter_value) && filter_value.equals("daily")) {
            if (database.getAllDailyExpenses(currentDate) != null) {
                expenseBeanArrayList = database.getAllDailyExpenses(currentDate);
                sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, getContext()));
                sectionListView.setVisibility(View.VISIBLE);
                cashflow_layout.setVisibility(View.VISIBLE);
                viewBelowCashFlowLayout.setVisibility(View.VISIBLE);

                tvWealth.setText("$ -" + sumExpense(expenseBeanArrayList));
                double newDailyCashFlow = sumIncome(expenseBeanArrayList) - sumExpense(expenseBeanArrayList);
                tvDailyCashFlow.setText("$ " + newDailyCashFlow);

                horizontalCalendar.getCalendarView().setVisibility(View.VISIBLE);
                no_data_layout.setVisibility(View.GONE);
            } else {
                no_data_layout.setVisibility(View.VISIBLE);
                sectionListView.setVisibility(View.GONE);
                cashflow_layout.setVisibility(View.GONE);
                viewBelowCashFlowLayout.setVisibility(View.GONE);
                horizontalCalendar.getCalendarView().setVisibility(View.GONE);
            }
            cbDaily.setChecked(true);
            cbAllTime.setChecked(false);
        } else if (!TextUtils.isEmpty(filter_value) && filter_value.equals("all_time")) {
            horizontalCalendar.getCalendarView().setVisibility(View.GONE);
            if (database.getAllExpenses() != null) {
                expenseBeanArrayList = database.getAllExpenses();
                sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, getContext()));
                sectionListView.setVisibility(View.VISIBLE);
                cashflow_layout.setVisibility(View.VISIBLE);
                viewBelowCashFlowLayout.setVisibility(View.VISIBLE);

                tvWealth.setText("$ -" + sumExpense(expenseBeanArrayList));

                double newDailyCashFlow = sumIncome(expenseBeanArrayList) - sumExpense(expenseBeanArrayList);

                tvDailyCashFlow.setText("$ " + newDailyCashFlow);

                no_data_layout.setVisibility(View.GONE);
            } else {
                no_data_layout.setVisibility(View.VISIBLE);
                sectionListView.setVisibility(View.GONE);
                cashflow_layout.setVisibility(View.GONE);
                viewBelowCashFlowLayout.setVisibility(View.GONE);
            }
            cbAllTime.setChecked(true);
            cbDaily.setChecked(false);

        } else {
            cbAllTime.setChecked(true);
            cbDaily.setChecked(false);
            horizontalCalendar.getCalendarView().setVisibility(View.GONE);
            if (database.getAllExpenses() != null) {
                expenseBeanArrayList = database.getAllExpenses();
                sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, getContext()));
                sectionListView.setVisibility(View.VISIBLE);
                cashflow_layout.setVisibility(View.VISIBLE);
                viewBelowCashFlowLayout.setVisibility(View.VISIBLE);

                tvWealth.setText("$ -" + sumExpense(expenseBeanArrayList));
                double newDailyCashFlow = sumIncome(expenseBeanArrayList) - sumExpense(expenseBeanArrayList);
                tvDailyCashFlow.setText("$ " + newDailyCashFlow);

                no_data_layout.setVisibility(View.GONE);
            } else {
                no_data_layout.setVisibility(View.VISIBLE);
                sectionListView.setVisibility(View.GONE);
                cashflow_layout.setVisibility(View.GONE);
                viewBelowCashFlowLayout.setVisibility(View.GONE);
            }
        }
    }

    public double sumExpense(ArrayList<ExpenseBean> list) {
        double sum = 0;
        for (ExpenseBean j : list) {
            if (j.flag == 1) {
                sum = sum + j.expense;
                Log.d(TAG, "sumExpense: " + sum);
            }
        }
        return sum;
    }

    public double sumIncome(ArrayList<ExpenseBean> list) {
        double sum = 0;
        for (ExpenseBean j : list) {
            if (j.flag == 2) {
                sum = sum + j.expense;
                Log.d(TAG, "sumIncome: " + sum);
            }
        }
        return sum;
    }
}