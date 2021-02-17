package com.gexton.xpendee.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

import android.text.TextUtils;
import android.text.format.DateFormat;
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

import com.gexton.xpendee.adapters.SectionListViewAdapter;
import com.gexton.xpendee.AddExpenseActivity;
import com.gexton.xpendee.HomeActivity;
import com.gexton.xpendee.R;
import com.gexton.xpendee.UpdateOrDeleteExpense;
import com.gexton.xpendee.model.ExpenseBean;
import com.gexton.xpendee.util.Database;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class TimelineFragment extends Fragment {

    View view;
    LinearLayout listviewLayout;
    ListView sectionListView;
    public static ArrayList<ExpenseBean> expenseBeanArrayList;
    Database database;
    FloatingActionButton fab_add_expense;
    RelativeLayout no_data_layout;
    TextView tvDailyCashFlow, tvWealth;
    ImageView img_calender;
    private AlertDialog alertDialog;
    String TAG = "TimelineFragment";
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    String filter_value;
    CheckBox cbDaily, cbWeekly, cbMonthly, cbYearly, cbAllTime;
    HorizontalCalendar horizontalCalendar;
    Calendar defaultSelectedDate;
    LinearLayout cashflow_layout;
    View cashflow_layout_border;

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
        cashflow_layout = view.findViewById(R.id.cashflow_layout);
        cashflow_layout_border = view.findViewById(R.id.cashflow_layout_border);
        editor = getContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
        prefs = getContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        filter_value = prefs.getString("filter", "");

        defaultSelectedDate = Calendar.getInstance();

        /* start before 1 month from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -2);

        /* end after 1 month from now */
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

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

                String dateNew = DateFormat.format("dd-MM-yyyy", date).toString();
                expenseBeanArrayList = database.getAllDailyExpenses(dateNew);
                if (expenseBeanArrayList != null) {
                    sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, getContext()));
                    tvWealth.setText("-$" + sumExpense(expenseBeanArrayList));

                    sectionListView.setVisibility(View.VISIBLE);
                    no_data_layout.setVisibility(View.GONE);
                    cashflow_layout.setVisibility(View.VISIBLE);
                    cashflow_layout_border.setVisibility(View.VISIBLE);

                } else {
                    cashflow_layout.setVisibility(View.GONE);
                    sectionListView.setVisibility(View.GONE);
                    no_data_layout.setVisibility(View.VISIBLE);
                    cashflow_layout_border.setVisibility(View.GONE);
                    tvWealth.setText("$0");
                    tvDailyCashFlow.setText("$0");
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

        if (!TextUtils.isEmpty(filter_value)) {
            if (filter_value.equals("daily")) {
                if (expenseBeanArrayList != null) {
                    horizontalCalendar.getCalendarView().setVisibility(View.VISIBLE);
                    sectionListView.setVisibility(View.VISIBLE);
                    String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
                    expenseBeanArrayList = database.getAllDailyExpenses(formattedDate);

                    sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, getContext()));
                    sumExpense(expenseBeanArrayList);
                    tvWealth.setText("-$" + sumExpense(expenseBeanArrayList));
                    //tvDailyCashFlow.setText("$" + sumIncome(database.getAllIncome(2)));
                }
            } else if (filter_value.equals("all_time")) {
                horizontalCalendar.getCalendarView().setVisibility(View.GONE);
                settingAddapter();
            }
        }

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

        img_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });

        return view;
    }

    private void settingAddapter() {
        horizontalCalendar.getCalendarView().setVisibility(View.GONE);
        if (database.getAllExpensesFlag() != null && expenseBeanArrayList != null) {
            expenseBeanArrayList = database.getAllExpensesFlag();
            sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, getContext()));
            if (expenseBeanArrayList != null) {
                Log.d("sum_of_expenses", "onCreateView: " + sumExpense(expenseBeanArrayList));
                tvWealth.setText("-$" + sumExpense(expenseBeanArrayList));
                //tvDailyCashFlow.setText("$" + sumIncome(database.getAllIncome(2)));
            }
        } else {
            no_data_layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (expenseBeanArrayList != null) {
            Log.d("sum_of_expenses", "onCreateView: " + sumExpense(expenseBeanArrayList));
            tvWealth.setText("-$" + sumExpense(expenseBeanArrayList));
            //tvDailyCashFlow.setText("$" + sumIncome(database.getAllIncome(2)));

            if (expenseBeanArrayList != null) {

                no_data_layout.setVisibility(View.GONE);
            } else {
                no_data_layout.setVisibility(View.VISIBLE);
            }

        }
    }

    public double sumExpense(ArrayList<ExpenseBean> list) {
        double sum = 0;
        double sumIncome = 0;
        for (ExpenseBean j : list) {
            sum = sum + j.expense;

            if (j.flag == 2) {
                sumIncome = sumIncome + j.expense;
                Log.d(TAG, "sumExpense: " + sumIncome);
            }
        }
        return sum;
    }

    public double sumIncome(ArrayList<ExpenseBean> list) {
        double sum = 0;
        double sumIncome = 0;
        for (ExpenseBean j : list) {
            sum = sum + j.expense;

            if (j.flag == 1) {
                sumIncome = sumIncome + j.expense;
                Log.d(TAG, "sumIncome: " + sumIncome);
            }
        }
        return sum;
    }

    private void showFilterDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.dialog_for_filtering_expenses, null);

        cbDaily = v.findViewById(R.id.cbDaily);
        cbWeekly = v.findViewById(R.id.cbWeekly);
        cbMonthly = v.findViewById(R.id.cbMonthly);
        cbYearly = v.findViewById(R.id.cbYearly);
        cbAllTime = v.findViewById(R.id.cbAllTime);

        if (!TextUtils.isEmpty(filter_value)) {
            if (filter_value.equals("daily")) {
                cbDaily.setChecked(true);
                horizontalCalendar.getCalendarView().setVisibility(View.VISIBLE);
                sectionListView.setVisibility(View.VISIBLE);
            } else if (filter_value.equals("all_time")) {
                cbAllTime.setChecked(true);
                horizontalCalendar.getCalendarView().setVisibility(View.GONE);
            }
        }

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

                    horizontalCalendar.getCalendarView().setVisibility(View.VISIBLE);

                    String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
                    expenseBeanArrayList = database.getAllDailyExpenses(formattedDate);
                    if (expenseBeanArrayList != null) {
                        sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, getContext()));
                        sumExpense(expenseBeanArrayList);
                        tvWealth.setText("-$" + sumExpense(expenseBeanArrayList));
                        //tvDailyCashFlow.setText("$" + sumIncome(database.getAllIncome(2)));

                        editor.putString("filter", "daily");
                        editor.apply();

                        horizontalCalendar.getCalendarView().setVisibility(View.VISIBLE);

                        //startActivity(new Intent(getContext(), HomeActivity.class));
                        //getActivity().finish();

                    } else {
                        Toast.makeText(getContext(), "No Record Found", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(getContext(), HomeActivity.class));
                        //getActivity().finish();
                    }
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
                    editor.putString("filter", "weekly");
                    editor.apply();
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
                    editor.putString("filter", "monthly");
                    editor.apply();
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
                    editor.putString("filter", "yearly");
                    editor.apply();
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
                    editor.putString("filter", "all_time");
                    editor.apply();
                    settingAddapter();
                    horizontalCalendar.getCalendarView().setVisibility(View.GONE);
                    startActivity(new Intent(getContext(), HomeActivity.class));
                    getActivity().finish();
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

    public void getFilterStatus() {
        if (!TextUtils.isEmpty(filter_value)) {
            if (filter_value.equals("daily")) {
                cbDaily.setChecked(true);
                horizontalCalendar.getCalendarView().setVisibility(View.VISIBLE);
            } else if (filter_value.equals("weekly")) {
                cbWeekly.setChecked(true);
            } else if (filter_value.equals("monthly")) {
                cbMonthly.setChecked(true);
            } else if (filter_value.equals("yearly")) {
                cbYearly.setChecked(true);
            } else if (filter_value.equals("all_time")) {
                cbAllTime.setChecked(true);
                horizontalCalendar.getCalendarView().setVisibility(View.GONE);
            }
        }
    }

    private void getFilterSelection() {
        if (!TextUtils.isEmpty(filter_value)) {
            if (filter_value.equals("daily")) {
                horizontalCalendar.getCalendarView().setVisibility(View.VISIBLE);
                String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
                expenseBeanArrayList = database.getAllDailyExpenses(formattedDate);
                if (expenseBeanArrayList != null) {
                    sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, getContext()));
                    sumExpense(expenseBeanArrayList);
                    tvWealth.setText("-$" + sumExpense(expenseBeanArrayList));
                    tvDailyCashFlow.setText("$" + sumIncome(database.getAllIncome(2)));
                }
            }

            if (filter_value.equals("all_time")) {
                horizontalCalendar.getCalendarView().setVisibility(View.GONE);
                settingAddapter();
            }
        }
    }

}