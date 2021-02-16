package com.gexton.xpendee;

import androidx.appcompat.app.AppCompatActivity;
import devs.mulham.horizontalcalendar.HorizontalCalendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gexton.xpendee.Adapters.SectionAdapter2;
import com.gexton.xpendee.Adapters.SectionListViewAdapter;
import com.gexton.xpendee.model.ExpenseBean;
import com.gexton.xpendee.util.Database;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class SearchActivity extends AppCompatActivity {
    EditText edtSearch;
    ListView listView;
    Database database;
    RelativeLayout no_data_layout;
    ImageView imgBack;
    public static ArrayList<ExpenseBean> expenseBeanArrayList;
    ArrayList<ExpenseBean> filteredList = new ArrayList<>();
    SectionAdapter2 sectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.navy_blue, this.getTheme()));
        }

        edtSearch = findViewById(R.id.edtSearch);
        listView = findViewById(R.id.listView);
        expenseBeanArrayList = new ArrayList<>();
        database = new Database(getApplicationContext());
        no_data_layout = findViewById(R.id.no_data_layout);
        imgBack = findViewById(R.id.imgBack);
        edtSearch.requestFocus();
        sectionAdapter = new SectionAdapter2(SearchActivity.this, R.layout.item_section_header, expenseBeanArrayList);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //filter(s.toString());
                //sectionAdapter = new SectionAdapter2(SearchActivity.this, R.layout.item_section_header, expenseBeanArrayList);
                sectionAdapter.getFilter().filter(s.toString());
                listView.setAdapter(sectionAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //filter(s.toString());
                sectionAdapter.getFilter().filter(s.toString());
                listView.setAdapter(sectionAdapter);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), UpdateOrDeleteExpense.class);
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

    }

    private void filter(String text) {
        expenseBeanArrayList.clear();
        for (ExpenseBean item : database.getAllExpenses()) {
            if (edtSearch.getText().toString().toLowerCase().contains(text.toLowerCase())) {
                expenseBeanArrayList.add(item);
            }
        }
        listView.setAdapter(new SectionAdapter2(SearchActivity.this, R.layout.item_section_header, expenseBeanArrayList));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //gettingTransactions();
    }

    private void gettingTransactions() {
        expenseBeanArrayList.clear();
        if (database.getAllExpenses() != null) {
            expenseBeanArrayList = database.getAllExpenses();
            listView.setAdapter(new SectionAdapter2(SearchActivity.this, R.layout.item_section_header, expenseBeanArrayList));
            listView.setVisibility(View.VISIBLE);
        } else {
            no_data_layout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }
}