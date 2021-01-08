package com.gexton.xpendee.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.xpendee.Adapters.CategoriesListAdapter;
import com.gexton.xpendee.Adapters.SectionListViewAdapter;
import com.gexton.xpendee.NewWalletActivity;
import com.gexton.xpendee.R;
import com.gexton.xpendee.model.CategoryBean;
import com.gexton.xpendee.model.DateBean;
import com.gexton.xpendee.model.ExpenseBean;
import com.gexton.xpendee.model.WalletBean;
import com.gexton.xpendee.util.Database;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    String MY_PREFS_NAME = "Xpendee", json;
    CircleImageView profile_image;
    Button btn_add_cash_wallet, btn_edit_wallet;
    TextView tv_balance, tv_currency, tv_my_wallet_name, tv_name;
    public LinearLayout layout_no_data_found;
    public RelativeLayout wallet_complete;
    ListView sectionListView;
    RelativeLayout listviewLayout;
    ArrayList<ExpenseBean> expenseBeanArrayList;
    Database database;
    ArrayList<String> dateBeanArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tv_name = view.findViewById(R.id.tv_name);
        profile_image = view.findViewById(R.id.profile_image);
        btn_add_cash_wallet = view.findViewById(R.id.btn_add_cash_wallet);
        tv_balance = view.findViewById(R.id.tv_balance);
        tv_currency = view.findViewById(R.id.tv_currency);
        tv_my_wallet_name = view.findViewById(R.id.tv_my_wallet_name);
        btn_edit_wallet = view.findViewById(R.id.btn_edit_wallet);
        layout_no_data_found = view.findViewById(R.id.layout_no_data_found);
        wallet_complete = view.findViewById(R.id.wallet_complete);
        sectionListView = view.findViewById(R.id.sectionListView);
        listviewLayout = view.findViewById(R.id.listviewLayout);
        database = new Database(getContext());
        expenseBeanArrayList = new ArrayList<>();
        dateBeanArrayList = new ArrayList<>();

        Log.d("tag_dates", "onCreateView: " + database.getAllExpensesDates());
        Log.d("tag_expense", "onCreateView: " + database.getAllExpenses());

        /*ArrayList<ExpenseBean> list = new ArrayList<>();
        list.add(new String(String.valueOf(database.getAllExpensesDates())));
        list.add((new ExpenseBean(0, "PKR", 120.0, 12345, "Name", "08-01-2021"
                , "Description", "Image Path")));
        list.add((new ExpenseBean(0, "PKR", 120.0, 12345, "Name", "08-01-2021"
                , "Description", "Image Path")));
        list.add(new String(String.valueOf(database.getAllExpensesDates())));
        list.add((new ExpenseBean(0, "PKR", 120.0, 12345, "Name", "08-01-2021"
                , "Description", "Image Path")));
        list.add((new ExpenseBean(0, "PKR", 120.0, 12345, "Name", "08-01-2021"
                , "Description", "Image Path")));*/

        ArrayList<ExpenseBean> expenseBeanArrayList = new ArrayList<>();
        expenseBeanArrayList.add(new ExpenseBean(0, "", 250.0, R.mipmap.shopping, "Shopping",
                "Description", "08-01-2021", "Image_Path"));
        expenseBeanArrayList.add(new ExpenseBean(0, "", 250.0, R.mipmap.shopping, "Shopping",
                "Description", "08-01-2021", "Image_Path"));
        expenseBeanArrayList.add(new ExpenseBean(0, "", 250.0, R.mipmap.shopping, "Shopping",
                "Description", "08-01-2021", "Image_Path"));
        expenseBeanArrayList.add(new ExpenseBean(0, "", 250.0, R.mipmap.shopping, "Shopping",
                "Description", "08-01-2021", "Image_Path"));
        expenseBeanArrayList.add(new ExpenseBean(0, "", 250.0, R.mipmap.shopping, "Shopping",
                "Description", "08-01-2021", "Image_Path"));


       /* if (database.getAllExpenses() != null) {
            expenseBeanArrayList = database.getAllExpenses();
        }

        if (database.getAllExpensesDates() != null) {
            dateBeanArrayList = database.getAllExpensesDates();
        }*/

        sectionListView.setAdapter(new SectionListViewAdapter(expenseBeanArrayList, dateBeanArrayList, getContext()));

        converSionAndSettingData();

        btn_edit_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NewWalletActivity.class));
            }
        });

        btn_add_cash_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NewWalletActivity.class));
            }
        });

        SharedPreferences prefs = getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString("name", "No name defined");
        String image = prefs.getString("image", "No name defined");

        tv_name.setText(name);
        Picasso.get().load(image).into(profile_image);

        return view;
    }

    public void converSionAndSettingData() {
        // Converting GSON object into String
        SharedPreferences prefs1 = getContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        json = prefs1.getString("Wallet_Bean", "");
        Gson gson = new Gson();
        WalletBean walletBean = gson.fromJson(json, WalletBean.class);
        if (walletBean != null) {
            tv_my_wallet_name.setText(walletBean.wallet_name);
            tv_balance.setText("" + walletBean.balance);
            tv_currency.setText(walletBean.currency);
            wallet_complete.setVisibility(View.VISIBLE);
            layout_no_data_found.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) listviewLayout.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.wallet_complete);

        } else {
            layout_no_data_found.setVisibility(View.VISIBLE);
            wallet_complete.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) listviewLayout.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.layout2);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        converSionAndSettingData();
    }

}