package com.gexton.xpendee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.xpendee.adapters.CategoriesAdapterForSpendingOverview;
import com.gexton.xpendee.adapters.CategoriesListAdapter;
import com.gexton.xpendee.adapters.SectionListViewAdapter;
import com.gexton.xpendee.adapters.TestAdapter;
import com.gexton.xpendee.model.CategoryBean;
import com.gexton.xpendee.model.ExpenseBean;
import com.gexton.xpendee.util.Database;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SpendingsOverviewActivity extends AppCompatActivity {
    PieChart pieChart;
    Database database;
    String currentDate;
    String filter_value;
    View transparentView;
    SharedPreferences prefs;
    RecyclerView rvCategories;
    CheckBox cbDaily, cbAllTime;
    LinearLayout expense, income;
    Calendar defaultSelectedDate;
    ImageView imgBack, imgCalendar;
    SharedPreferences.Editor editor;
    HorizontalCalendar horizontalCalendar;
    String MY_PREFS_NAME = "MY_PREFS_NAME";
    TextView tvExpenseAmount, tvIncomeAmount;
    ArrayList<CategoryBean> categoryBeanArrayList;
    CategoriesAdapterForSpendingOverview adapter = null;
    RelativeLayout filter_layout, daily_layout, all_time_layout;
    ArrayList<ExpenseBean> expenseBeanArrayList = new ArrayList<>();
    ListView sectionListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spendings_overview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.navy_blue, this.getTheme()));
        }

        init();

        listeners();

        loadCategories(1);

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                String dateNew = DateFormat.format("dd-MM-yyyy", date).toString();
                if (database.getAllDailyExpenses(dateNew) != null) {
                    sectionListView.setVisibility(View.VISIBLE);
                    expenseBeanArrayList = database.getAllDailyExpenses(currentDate);
                    sectionListView.setAdapter(new TestAdapter(expenseBeanArrayList, getApplicationContext()));

                    if (database.getAllIncome(1) != null) {
                        tvExpenseAmount.setText("-PKR " + sumExpense(database.getAllDailyExpenses(currentDate)));
                    }

                    if (database.getAllIncome(2) != null) {
                        tvIncomeAmount.setText("PKR " + sumIncome(database.getAllDailyExpenses(currentDate)));
                    }
                } else {
                    tvExpenseAmount.setText("-PKR 0");
                    tvIncomeAmount.setText("PKR 0");
                    sectionListView.setVisibility(View.GONE);
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

    }

    private void listeners() {
        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCategories(2);
                tvIncomeAmount.setTextColor(Color.GREEN);
                tvExpenseAmount.setTextColor(Color.GRAY);
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    income.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.curve_for_transaction_category));
                } else {
                    income.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.curve_for_transaction_category));
                }
                expense.setBackgroundResource(0);
            }
        });

        expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvCategories.setVisibility(View.VISIBLE);
                loadCategories(1);
                tvExpenseAmount.setTextColor(Color.RED);
                tvIncomeAmount.setTextColor(Color.GRAY);
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    expense.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.curve_for_transaction_category));
                } else {
                    expense.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.curve_for_transaction_category));
                }
                income.setBackgroundResource(0);

            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imgCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filter_layout.isShown()) {
                    filter_layout.setVisibility(View.GONE);
                    transparentView.setVisibility(View.GONE);
                } else {
                    filter_layout.setVisibility(View.VISIBLE);
                    transparentView.setVisibility(View.VISIBLE);
                }
            }
        });

        transparentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transparentView.setVisibility(View.GONE);
                filter_layout.setVisibility(View.GONE);
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
                    horizontalCalendar.getCalendarView().setVisibility(View.VISIBLE);
                }
                filter_layout.setVisibility(View.GONE);
                transparentView.setVisibility(View.GONE);
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
                    horizontalCalendar.getCalendarView().setVisibility(View.GONE);
                }
                transparentView.setVisibility(View.GONE);
                filter_layout.setVisibility(View.GONE);
            }
        });
    }

    private void init() {
        rvCategories = findViewById(R.id.rvCategories);
        database = new Database(this);
        categoryBeanArrayList = new ArrayList<>();
        expense = findViewById(R.id.expense);
        income = findViewById(R.id.income);
        tvIncomeAmount = findViewById(R.id.tvIncomeAmount);
        tvExpenseAmount = findViewById(R.id.tvExpenseAmount);
        imgBack = findViewById(R.id.imgBack);
        pieChart = findViewById(R.id.pieChart);
        imgCalendar = findViewById(R.id.imgCalendar);
        transparentView = findViewById(R.id.transparentView);
        filter_layout = findViewById(R.id.filter_layout);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        filter_value = prefs.getString("filter", "");
        currentDate = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        defaultSelectedDate = Calendar.getInstance();
        cbDaily = findViewById(R.id.cbDaily);
        cbAllTime = findViewById(R.id.cbAllTime);
        daily_layout = findViewById(R.id.daily_layout);
        all_time_layout = findViewById(R.id.all_time_layout);
        sectionListView = findViewById(R.id.sectionListView);

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -2);

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 2);

        horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
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

    private void loadCategories(int flag) {

        if (!TextUtils.isEmpty(filter_value) && filter_value.equals("daily")) {
            expenseBeanArrayList.clear();
            expenseBeanArrayList = database.getAllDailyExpenses(currentDate);
            sectionListView.setAdapter(new TestAdapter(expenseBeanArrayList, getApplicationContext()));

            if (database.getAllIncome(1) != null) {
                tvExpenseAmount.setText("-PKR " + sumExpense(database.getAllDailyExpenses(currentDate)));
            }

            if (database.getAllIncome(2) != null) {
                tvIncomeAmount.setText("PKR " + sumIncome(database.getAllDailyExpenses(currentDate)));
            }

        } else {
            expenseBeanArrayList.clear();
            expenseBeanArrayList = database.getAllIncome(flag);
            sectionListView.setAdapter(new TestAdapter(expenseBeanArrayList, getApplicationContext()));

            if (database.getAllIncome(1) != null) {
                tvExpenseAmount.setText("-PKR " + sumExpense(expenseBeanArrayList));
            }

            if (database.getAllIncome(2) != null) {
                tvIncomeAmount.setText("PKR " + sumIncome(expenseBeanArrayList));
            }

        }

    }

    public double sumExpense(ArrayList<ExpenseBean> list) {
        double sum = 0;
        for (ExpenseBean j : list) {
            if (j.flag == 1) {
                sum = sum + j.expense;
                Log.d("sum_expense", "sumExpense: " + sum);
            }
        }
        return sum;
    }

    public double sumIncome(ArrayList<ExpenseBean> list) {
        double sum = 0;
        for (ExpenseBean j : list) {
            if (j.flag == 2) {
                sum = sum + j.expense;
                Log.d("sum_expense", "sumExpense: " + sum);
            }
        }
        return sum;
    }

    public void settingPieChart() {
        Drawable user_icon = getDrawable(R.drawable.beauty);
        pieChart = findViewById(R.id.pieChart);
        ArrayList<PieEntry> list = new ArrayList<>();
        list.add(new PieEntry(500, user_icon));

        PieDataSet pieDataSet = new PieDataSet(list, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(14f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Pie Chart");
        pieChart.animate();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!TextUtils.isEmpty(filter_value) && filter_value.equals("daily")) {
            if (database.getAllDailyExpenses(currentDate) != null) {
                horizontalCalendar.getCalendarView().setVisibility(View.VISIBLE);

                if (database.getAllIncome(1) != null) {
                    tvExpenseAmount.setText("-PKR " + sumExpense(database.getAllDailyExpenses(currentDate)));
                }

                if (database.getAllIncome(2) != null) {
                    tvIncomeAmount.setText("PKR " + sumIncome(database.getAllDailyExpenses(currentDate)));
                }

            }
            cbDaily.setChecked(true);
        } else if (!TextUtils.isEmpty(filter_value) && filter_value.equals("all_time")) {
            horizontalCalendar.getCalendarView().setVisibility(View.GONE);
            if (database.getAllExpenses() != null) {
                if (database.getAllIncome(1) != null) {
                    tvExpenseAmount.setText("-PKR " + sumExpense(database.getAllExpenses()));
                }

                if (database.getAllIncome(2) != null) {
                    tvIncomeAmount.setText("PKR " + sumIncome(database.getAllExpenses()));
                }

            }
            cbAllTime.setChecked(true);
        } else {
            cbAllTime.setChecked(true);
        }
    }

}