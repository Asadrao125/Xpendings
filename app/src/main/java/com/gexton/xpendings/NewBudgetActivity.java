package com.gexton.xpendings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.xpendings.model.BudgetBean;
import com.google.gson.Gson;

public class NewBudgetActivity extends AppCompatActivity {
    ImageView imgBack;
    TextView btnSave;
    EditText edtBudget, edtBudgetName;
    String budgetName, currency = "PKR", budgetAmount, recurrance = "Monthly";
    RelativeLayout layout_budget_for, layout_recurrance, layout_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_budget);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.navy_blue, this.getTheme()));
        }

        imgBack = findViewById(R.id.imgBack);
        btnSave = findViewById(R.id.btnSave);
        edtBudget = findViewById(R.id.edtBudget);
        edtBudgetName = findViewById(R.id.edtBudgetName);
        layout_budget_for = findViewById(R.id.layout_budget_for);
        layout_recurrance = findViewById(R.id.layout_recurrance);
        layout_date = findViewById(R.id.layout_date);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        layout_budget_for.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AllExpensesCategoryActivity.class));
            }
        });

        layout_recurrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        layout_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                budgetAmount = edtBudget.getText().toString().trim();
                budgetName = edtBudgetName.getText().toString().trim();

                if (TextUtils.isEmpty(budgetAmount)) {
                    Toast.makeText(NewBudgetActivity.this, "Please enter budget", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(budgetName)) {
                    Toast.makeText(NewBudgetActivity.this, "Please enter budget name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(currency)) {
                    Toast.makeText(NewBudgetActivity.this, "Please enter currency", Toast.LENGTH_SHORT).show();
                } else {
                    double bdgt_amt = Double.parseDouble(budgetAmount);

                    BudgetBean budgetBean = new BudgetBean(bdgt_amt, budgetName, currency, recurrance);

                    Gson gson = new Gson();
                    String json = gson.toJson(budgetBean);

                    SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                    editor.putString("Budget_Bean", json);
                    editor.apply();
                    Toast.makeText(NewBudgetActivity.this, "Budget Added", Toast.LENGTH_SHORT).show();

                    edtBudget.setText("");
                    edtBudgetName.setText("");

                    onBackPressed();

                }
            }
        });

        settingDataInFields();

    }

    private void settingDataInFields() {
        // Converting GSON object into String
        SharedPreferences prefs1 = this.getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        String json = prefs1.getString("Budget_Bean", "");
        Gson gson = new Gson();
        BudgetBean budgetBean = gson.fromJson(json, BudgetBean.class);
        if (budgetBean != null) {
            edtBudgetName.setText(budgetBean.budgetName);
            edtBudget.setText("" + budgetBean.amount);
        }
    }

}