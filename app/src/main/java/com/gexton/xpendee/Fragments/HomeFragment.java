package com.gexton.xpendee.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gexton.xpendee.BuildConfig;
import com.gexton.xpendee.NewWalletActivity;
import com.gexton.xpendee.R;
import com.gexton.xpendee.model.CategoryBean;
import com.gexton.xpendee.model.ExpenseBean;
import com.gexton.xpendee.model.WalletBean;
import com.gexton.xpendee.util.Database;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
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
    RelativeLayout static_layout_wallet;
    ArrayList<ExpenseBean> expenseBeanArrayList;
    Database database;
    ArrayList<String> dateBeanArrayList;
    PieChart pieChart;
    BarChart barChart;
    ArrayList<PieEntry> list = new ArrayList<>();
    ArrayList<BarEntry> listBarEntry = new ArrayList<>();
    ArrayList<CategoryBean> allCatList;
    CardView cardView_pieChart, cardView_barChart;
    ImageView imgShare;
    float totalExpense = 0;
    float totalExpense2 = 0;

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
        barChart = view.findViewById(R.id.barChart);
        cardView_pieChart = view.findViewById(R.id.card_view_pieChart);
        cardView_barChart = view.findViewById(R.id.card_view_barChart);
        imgShare = view.findViewById(R.id.imgShare);

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

        if (allCatList != null && totalExpense > 0) {
            settingPieChart(allCatList);
            settingBarChart(allCatList);
            cardView_barChart.setVisibility(View.VISIBLE);
            cardView_pieChart.setVisibility(View.VISIBLE);
        } else {
            cardView_barChart.setVisibility(View.GONE);
            cardView_pieChart.setVisibility(View.GONE);
        }

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

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIntent();
            }
        });

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
        } else {
            layout_no_data_found.setVisibility(View.VISIBLE);
            wallet_complete.setVisibility(View.GONE);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        converSionAndSettingData();
        if (allCatList != null) {
            settingPieChart(allCatList);
            settingBarChart(allCatList);

            if (totalExpense > 0) {
                cardView_pieChart.setVisibility(View.VISIBLE);
                cardView_barChart.setVisibility(View.VISIBLE);
            } else {
                cardView_pieChart.setVisibility(View.GONE);
                cardView_barChart.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Dexter.withContext(getContext()).withPermissions(
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
        }*/
    }

    private void settingPieChart(ArrayList<CategoryBean> categoryBeans) {

        list.clear();
        totalExpense = 0;

        for (int i = 0; i < categoryBeans.size(); i++) {
            CategoryBean categoryBean = categoryBeans.get(i);
            ArrayList<ExpenseBean> expenseBeansInThisCat = categoryBean.listExpenseBean;
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
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("All Expenses");
        pieChart.animateX(500);
        pieChart.animateY(500);
        pieChart.animate();

    }

    public void settingBarChart(ArrayList<CategoryBean> categoryBeans) {
        listBarEntry.clear();
        totalExpense2 = 0;
        for (int i = 0; i < categoryBeans.size(); i++) {
            CategoryBean categoryBean = categoryBeans.get(i);
            ArrayList<ExpenseBean> expenseBeansInThisCat = categoryBean.listExpenseBean;
            if (expenseBeansInThisCat != null) {
                for (int j = 0; j < expenseBeansInThisCat.size(); j++) {
                    totalExpense2 = (float) (totalExpense2 + expenseBeansInThisCat.get(j).expense);
                }//end inner loop
            }

            listBarEntry.add(new BarEntry(i, totalExpense2));

        }//end outer loop

        BarDataSet barDataSet = new BarDataSet(listBarEntry, "All Expenses");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("");
        barChart.animateY(1000);
        barChart.setPinchZoom(false);
    }

    private void shareIntent() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Xpendee");
            String shareMessage = "Let me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Please select"));
        } catch (Exception e) {
            e.toString();
        }
    }
}