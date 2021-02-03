package com.gexton.xpendee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.xpendee.Fragments.HomeFragment;
import com.gexton.xpendee.model.BudgetBean;
import com.gexton.xpendee.model.WalletBean;
import com.google.gson.Gson;

public class NewBudgetActivity extends AppCompatActivity {
    View view;
    ImageView imgBack;
    TextView btnSave;
    EditText edtBudget, edtBudgetName;
    String budgetName, currency = "PKR", budgetAmount, recurrance = "Monthly";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_budget);

        imgBack = view.findViewById(R.id.imgBack);
        btnSave = findViewById(R.id.btnSave);
        edtBudget = findViewById(R.id.edtBudget);
        edtBudgetName = findViewById(R.id.edtBudgetName);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
    }
}