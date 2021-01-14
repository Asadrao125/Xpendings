package com.gexton.xpendee.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.PermissionRequest;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    String MY_PREFS_NAME = "Xpendee", json;
    CircleImageView profile_image;
    Button btn_add_cash_wallet, btn_edit_wallet;
    TextView tv_balance, tv_currency, tv_my_wallet_name, tv_name;
    public LinearLayout layout_no_data_found;
    public RelativeLayout wallet_complete;
    RelativeLayout static_layout_wallet;
    ArrayList<ExpenseBean> expenseBeanArrayList;
    Database database;
    ArrayList<String> dateBeanArrayList;
    PieChart pieChart;
    ArrayList<PieEntry> list = new ArrayList<>();
    ArrayList<CategoryBean> allCatList;

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
        static_layout_wallet = view.findViewById(R.id.static_layout_wallet);
        database = new Database(getContext());
        expenseBeanArrayList = new ArrayList<>();
        dateBeanArrayList = new ArrayList<>();
        pieChart = view.findViewById(R.id.pieChart);

        //Gettiing all categories list
        allCatList = database.getAllCategories(1);
        if (allCatList != null) {
            for (int i = 0; i < allCatList.size(); i++) {

                CategoryBean cb = allCatList.get(i);

                ArrayList<ExpenseBean> expenseBeansListInThisCat = database.getExpenseByName(cb.categoryName);
                Log.d("list_by_category_name", "onCreateView: " + expenseBeansListInThisCat);

                cb.listExpenseBean = expenseBeansListInThisCat;

                Log.d("list_list_list", "onCreateView: " + cb.listExpenseBean);

            }
        }

        if (allCatList != null) {
            settingPieChart(allCatList);
        }

        Log.d("expense_categories", "onCreateView: " + database.getAllExpenseCategories());
        Log.d("expenses", "onCreateView: " + database.getExpenses());

        Log.d("tag_dates", "onCreateView: " + database.getAllExpensesDates());
        Log.d("tag_expense", "onCreateView: " + database.getAllExpenses());

        Log.d("expense_bean_list", "onCreateView: " + database.getAllExpenses());

        converSionAndSettingData();

        btn_edit_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NewWalletActivity.class));
            }
        });

        static_layout_wallet.setOnClickListener(new View.OnClickListener() {
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

    private void settingPieChart(ArrayList<CategoryBean> categoryBeans) {

        list.clear();
        for (int i = 0; i < allCatList.size(); i++) {
            CategoryBean categoryBean = allCatList.get(i);
            ArrayList<ExpenseBean> expenseBeansInThisCat = categoryBean.listExpenseBean;
            float totalExpense = 0;
            if (expenseBeansInThisCat != null) {
                for (int j = 0; j < expenseBeansInThisCat.size(); j++) {
                    totalExpense = (float) (totalExpense + expenseBeansInThisCat.get(j).expense);
                }//end inner loop
            }

            list.add(new PieEntry(totalExpense, categoryBean.categoryName));
        }//end outer loop

        PieDataSet pieDataSet = new PieDataSet(list, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(14f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("All Expenses");
        pieChart.animateX(500);
        pieChart.animateY(500);
        pieChart.animate();

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
        } else {
            layout_no_data_found.setVisibility(View.VISIBLE);
            wallet_complete.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        converSionAndSettingData();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Dexter.withContext(getContext()).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                Toast.makeText(getContext(), "All Permissions Are Granted", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> list, PermissionToken permissionToken) {

                        }

                    }).check();
        }
    }
}