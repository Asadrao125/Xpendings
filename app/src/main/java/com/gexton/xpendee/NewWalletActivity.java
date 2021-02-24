package com.gexton.xpendee;

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

import com.gexton.xpendee.model.WalletBean;
import com.gexton.xpendee.util.AdUtil;
import com.gexton.xpendee.util.Database;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;

public class NewWalletActivity extends AppCompatActivity {
    TextView tv_save, tvAllExpenses;
    EditText edt_balance, edt_wallet_name;
    String currency = "PKR";
    String balance, wallet_name;
    ImageView imgBack;
    double balance_new = 0.0;
    Database database;
    RelativeLayout wallet_name_layout, category_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wallet);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.navy_blue, this.getTheme()));
        }

        tv_save = findViewById(R.id.tv_save);
        edt_balance = findViewById(R.id.edt_balance);
        edt_wallet_name = findViewById(R.id.edt_wallet_name);
        imgBack = findViewById(R.id.imgBack);
        wallet_name_layout = findViewById(R.id.wallet_name_layout);
        tvAllExpenses = findViewById(R.id.tvAllExpenses);
        category_layout = findViewById(R.id.category_layout);
        database = new Database(getApplicationContext());

        AdView adView = findViewById(R.id.adView);
        AdUtil adUtil = new AdUtil(this);
        adUtil.loadBannerAd(adView);

        settingDataInFields();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                balance = edt_balance.getText().toString().trim();
                wallet_name = edt_wallet_name.getText().toString().trim();

                if (TextUtils.isEmpty(wallet_name)) {
                    Toast.makeText(NewWalletActivity.this, "Please enter wallet name", Toast.LENGTH_SHORT).show();
                } else {


                    if (!TextUtils.isEmpty(balance)) {
                        balance_new = Double.valueOf(balance);
                    }

                    WalletBean walletBean = new WalletBean(balance_new, wallet_name, currency);
                    Gson gson = new Gson();
                    String json = gson.toJson(walletBean);

                    SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                    editor.putString("Wallet_Bean", json);
                    editor.apply();
                    Toast.makeText(NewWalletActivity.this, "Wallet Added", Toast.LENGTH_SHORT).show();

                    edt_wallet_name.setText("");
                    edt_balance.setText("");

                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                }

            }
        });

        category_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), VisiblistyActivityForCategories.class));
            }
        });

        if (database.getAllCategories(1) != null && database.getAllCategories(2) != null && database.getAllVisibleCategories(1) != null) {
            int allCatSize = database.getAllCategories(1).size() + database.getAllCategories(2).size();
            int visibleCatSize = database.getAllVisibleCategories(1).size();

            if (visibleCatSize == allCatSize) {
                tvAllExpenses.setText("All Expenses");
            } else {
                tvAllExpenses.setText("" + database.getAllVisibleCategories(1).size());
            }
        }
    }

    private void settingDataInFields() {
        // Converting GSON object into String
        SharedPreferences prefs1 = this.getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        String json = prefs1.getString("Wallet_Bean", "");
        Gson gson = new Gson();
        WalletBean walletBean = gson.fromJson(json, WalletBean.class);
        if (walletBean != null) {
            edt_wallet_name.setText(walletBean.wallet_name);
            edt_balance.setText("" + walletBean.balance);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (database.getAllCategories(1) != null && database.getAllCategories(2) != null && database.getAllVisibleCategories(1) != null) {
            int allCatSize = database.getAllCategories(1).size() + database.getAllCategories(2).size();
            int visibleCatSize = database.getAllVisibleCategories(1).size();

            if (visibleCatSize == allCatSize) {
                tvAllExpenses.setText("All Expenses");
            } else {
                tvAllExpenses.setText("" + database.getAllVisibleCategories(1).size());
            }
        }
    }
}