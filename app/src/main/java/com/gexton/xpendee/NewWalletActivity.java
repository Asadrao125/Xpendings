package com.gexton.xpendee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.xpendee.Fragments.HomeFragment;
import com.gexton.xpendee.model.WalletBean;
import com.google.gson.Gson;

public class NewWalletActivity extends AppCompatActivity {
    TextView tv_save;
    EditText edt_balance, edt_wallet_name;
    String currency = "PKR";
    String balance, wallet_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wallet);

        tv_save = findViewById(R.id.tv_save);
        edt_balance = findViewById(R.id.edt_balance);
        edt_wallet_name = findViewById(R.id.edt_wallet_name);

        settingDataInFields();

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                balance = edt_balance.getText().toString().trim();
                wallet_name = edt_wallet_name.getText().toString().trim();

                Double balance_new = Double.valueOf(balance);

                WalletBean walletBean = new WalletBean(balance_new, wallet_name, currency);
                Gson gson = new Gson();
                String json = gson.toJson(walletBean);

                SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                editor.putString("Wallet_Bean", json);
                editor.apply();
                Toast.makeText(NewWalletActivity.this, "Wallet Added", Toast.LENGTH_SHORT).show();

                edt_wallet_name.setText("");
                edt_balance.setText("");

                onBackPressed();
            }
        });

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

            /*HomeFragment homeFragment = new HomeFragment();
            homeFragment.wallet_complete.setVisibility(View.VISIBLE);
            homeFragment.layout_no_data_found.setVisibility(View.GONE);*/

        }
    }

}