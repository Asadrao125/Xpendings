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
import android.widget.AdapterView;
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
import com.gexton.xpendee.util.AdUtil;
import com.gexton.xpendee.util.Database;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SpendingsOverviewActivity extends AppCompatActivity {
    Database database;
    ImageView imgBack;
    String currentDate;
    String filter_value;
    SharedPreferences prefs;
    ListView sectionListView;
    CheckBox cbDaily, cbAllTime;
    LinearLayout expense, income;
    SharedPreferences.Editor editor;
    String MY_PREFS_NAME = "MY_PREFS_NAME";
    TextView tvExpenseAmount, tvIncomeAmount;
    ArrayList<ExpenseBean> expenseBeanArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spendings_overview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.navy_blue, this.getTheme()));
        }

        AdView adView = findViewById(R.id.adView);
        AdUtil adUtil = new AdUtil(this);
        adUtil.loadBannerAd(adView);

        init();

        listeners();

        sectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), CategoryWiseTransactionActivity.class);
                intent.putExtra("category_name", expenseBeanArrayList.get(i).categoryName);
                startActivity(intent);
            }
        });

        loadCategories(1);

    }

    private void listeners() {
        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCategories(2);
                tvIncomeAmount.setTextColor(getResources().getColor(R.color.green));
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
    }

    private void init() {
        database = new Database(this);
        expense = findViewById(R.id.expense);
        income = findViewById(R.id.income);
        tvIncomeAmount = findViewById(R.id.tvIncomeAmount);
        tvExpenseAmount = findViewById(R.id.tvExpenseAmount);
        imgBack = findViewById(R.id.imgBack);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        filter_value = prefs.getString("filter", "");
        currentDate = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        cbDaily = findViewById(R.id.cbDaily);
        cbAllTime = findViewById(R.id.cbAllTime);
        sectionListView = findViewById(R.id.sectionListView);
    }

    private void loadCategories(int flag) {

        if (!TextUtils.isEmpty(filter_value) && filter_value.equals("daily")) {

            if (database.getAllDailyExpenses(currentDate) != null) {
                sectionListView.setVisibility(View.VISIBLE);
                expenseBeanArrayList.clear();
                expenseBeanArrayList = database.getAllDailyExpenses(currentDate);
                expenseBeanArrayList = removeDuplicates(expenseBeanArrayList);
                sectionListView.setAdapter(new TestAdapter(expenseBeanArrayList, getApplicationContext()));

                if (database.getAllIncome(1) != null) {
                    tvExpenseAmount.setText("-PKR " + sumExpense(database.getAllIncome(1)));
                }

                if (database.getAllIncome(2) != null) {
                    tvIncomeAmount.setText("PKR " + sumIncome(database.getAllIncome(2)));
                }
            } else {
                sectionListView.setVisibility(View.GONE);
            }
        } else {
            if (database.getAllIncome(flag) != null) {
                sectionListView.setVisibility(View.VISIBLE);
                expenseBeanArrayList.clear();
                expenseBeanArrayList = database.getAllIncome(flag);
                expenseBeanArrayList = removeDuplicates(expenseBeanArrayList);
                sectionListView.setAdapter(new TestAdapter(expenseBeanArrayList, getApplicationContext()));

                if (database.getAllIncome(1) != null) {
                    tvExpenseAmount.setText("-PKR " + sumExpense(database.getAllIncome(1)));
                }

                if (database.getAllIncome(2) != null) {
                    tvIncomeAmount.setText("PKR " + sumIncome(database.getAllIncome(2)));
                }
            } else {
                sectionListView.setVisibility(View.GONE);
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

    public static ArrayList<ExpenseBean> removeDuplicates(ArrayList<ExpenseBean> list) {
        ArrayList<ExpenseBean> newList = new ArrayList<ExpenseBean>();
        ArrayList<String> catNames = new ArrayList<>();
        for (ExpenseBean element : list) {
            String cat = element.categoryName;
            if (!catNames.contains(cat)) {
                catNames.add(cat);
                newList.add(element);
            }
        }
        return newList;
    }
}