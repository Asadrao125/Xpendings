package com.gexton.xpendee.fragments;

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
import com.highsoft.highcharts.Common.HIChartsClasses.HICondition;
import com.highsoft.highcharts.Common.HIChartsClasses.HILabel;
import com.highsoft.highcharts.Common.HIChartsClasses.HILegend;
import com.highsoft.highcharts.Common.HIChartsClasses.HILine;
import com.highsoft.highcharts.Common.HIChartsClasses.HIOptions;
import com.highsoft.highcharts.Common.HIChartsClasses.HIPie;
import com.highsoft.highcharts.Common.HIChartsClasses.HIPlotOptions;
import com.highsoft.highcharts.Common.HIChartsClasses.HIResponsive;
import com.highsoft.highcharts.Common.HIChartsClasses.HIRules;
import com.highsoft.highcharts.Common.HIChartsClasses.HISeries;
import com.highsoft.highcharts.Common.HIChartsClasses.HISubtitle;
import com.highsoft.highcharts.Common.HIChartsClasses.HITitle;
import com.highsoft.highcharts.Common.HIChartsClasses.HIXAxis;
import com.highsoft.highcharts.Common.HIChartsClasses.HIYAxis;
import com.highsoft.highcharts.Core.HIChartView;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

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
    HIChartView chartView;
    ArrayList<ExpenseBean> listExpenseBean;

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
        chartView = (HIChartView) view.findViewById(R.id.hc);
        listExpenseBean = new ArrayList<>();

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

        settingHighChartLine();

        /*if (allCatList != null && totalExpense > 0) {
            settingPieChart(allCatList);
            settingBarChart(allCatList);
            cardView_barChart.setVisibility(View.VISIBLE);
            cardView_pieChart.setVisibility(View.VISIBLE);
        } else {
            cardView_barChart.setVisibility(View.GONE);
            cardView_pieChart.setVisibility(View.GONE);
        }*/

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

        settingHighChart();

        return view;
    }

    private void settingHighChartLine() {

        HIOptions options = new HIOptions();

        HITitle title = new HITitle();
        title.setText("Solar Employment Growth by Sector, 2010-2016");
        options.setTitle(title);

        HISubtitle subtitle = new HISubtitle();
        subtitle.setText("Source: thesolarfoundation.com");
        options.setSubtitle(subtitle);

        HIYAxis yaxis = new HIYAxis();
        yaxis.setTitle(new HITitle());
        yaxis.getTitle().setText("Number of Employees");
        options.setYAxis(new ArrayList<>(Collections.singletonList(yaxis)));

        HILegend legend = new HILegend();
        legend.setLayout("vertical");
        legend.setAlign("right");
        legend.setVerticalAlign("middle");
        options.setLegend(legend);

        HIPlotOptions plotoptions = new HIPlotOptions();
        plotoptions.setSeries(new HISeries());
        plotoptions.getSeries().setLabel(new HILabel());
        plotoptions.getSeries().getLabel().setConnectorAllowed(false);
        plotoptions.getSeries().setPointStart(2010);
        options.setPlotOptions(plotoptions);

        HILine line1 = new HILine();
        line1.setName("Installation");
        line1.setData(new ArrayList<>(Arrays.asList(43934, 52503, 57177, 69658, 97031, 119931, 137133, 154175)));

        HILine line2 = new HILine();
        line2.setName("Manufacturing");
        line2.setData(new ArrayList<>(Arrays.asList(24916, 24064, 29742, 29851, 32490, 30282, 38121, 40434)));

        HILine line3 = new HILine();
        line3.setName("Sales & Distribution");
        line3.setData(new ArrayList<>(Arrays.asList(11744, 17722, 16005, 19771, 20185, 24377, 32147, 39387)));

        HILine line4 = new HILine();
        line4.setName("Project Development");
        line4.setData(new ArrayList<>(Arrays.asList(null, null, 7988, 12169, 15112, 22452, 34400, 34227)));

        HILine line5 = new HILine();
        line5.setName("Other");
        line5.setData(new ArrayList<>(Arrays.asList(12908, 5948, 8105, 11248, 8989, 11816, 18274, 18111)));

        HIResponsive responsive = new HIResponsive();

        HIRules rules1 = new HIRules();
        rules1.setCondition(new HICondition());
        rules1.getCondition().setMaxWidth(500);
        HashMap<String, HashMap> chartLegend = new HashMap<>();
        HashMap<String, String> legendOptions = new HashMap<>();
        legendOptions.put("layout", "horizontal");
        legendOptions.put("align", "center");
        legendOptions.put("verticalAlign", "bottom");
        chartLegend.put("legend", legendOptions);
        rules1.setChartOptions(chartLegend);
        responsive.setRules(new ArrayList<>(Collections.singletonList(rules1)));
        options.setResponsive(responsive);

        options.setSeries(new ArrayList<>(Arrays.asList(line1, line2, line3, line4, line5)));

        chartView.setOptions(options);
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
       /* if (allCatList != null) {
            settingPieChart(allCatList);
            settingBarChart(allCatList);

            if (totalExpense > 0) {
                cardView_pieChart.setVisibility(View.VISIBLE);
                cardView_barChart.setVisibility(View.VISIBLE);
            } else {
                cardView_pieChart.setVisibility(View.GONE);
                cardView_barChart.setVisibility(View.GONE);
            }
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

    private void settingHighChart() {

        HIOptions options = new HIOptions();

        HITitle title = new HITitle();
        title.setText("Pie point CSS");
        options.setTitle(title);

        HIXAxis xAxis = new HIXAxis();
        /*String[] categoriesList = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        xAxis.setCategories(new ArrayList<>(Arrays.asList(categoriesList)));
        options.setXAxis(new ArrayList<HIXAxis>() {{
            add(xAxis);
        }});*/

        if (database.getAllExpenses() != null) {
            listExpenseBean = database.getAllExpenses();
            ArrayList<String> stringList = new ArrayList<>();
            stringList = database.getAllExpenseCategories();
            for (int i = 0; i < listExpenseBean.size(); i++) {
                xAxis.setCategories(stringList);
            }
            options.setXAxis(new ArrayList<HIXAxis>() {{
                add(xAxis);
            }});
        }

        HIPie series1 = new HIPie();
        series1.setAllowPointSelect(true);
        String[] keys = new String[]{"name", "y", "selected", "sliced"};
        series1.setKeys(new ArrayList<>(Arrays.asList(keys)));
        series1.setShowInLegend(true);
        Object[] object1 = new Object[]{"Apples", 29.9, false};
        Object[] object2 = new Object[]{"Pears", 71.5, false};
        Object[] object3 = new Object[]{"Oranges", 106.4, false};
        Object[] object4 = new Object[]{"Plums", 129.2, false};
        Object[] object5 = new Object[]{"Bananas", 144.0, false};
        Object[] object6 = new Object[]{"Peaches", 176.0, false};
        Object[] object7 = new Object[]{"Prunes", 135.6, true, true};
        Object[] object8 = new Object[]{"Avocados", 148.5, false};
        series1.setData(new ArrayList<>(Arrays.asList(object1, object2, object3, object4, object5, object6, object7, object8)));
        options.setSeries(new ArrayList<>(Arrays.asList(series1)));
        chartView.setOptions(options);

        ArrayList<ExpenseBean> listExpense = new ArrayList<>();
        listExpense = database.getAllIncome(1);

        Object[] object10 = new Object[0];
        if (listExpense != null) {
            for (int i = 0; i < listExpense.size(); i++) {
                object10 = new Object[]{listExpense.get(i).categoryName, listExpenseBean.get(i).expense, false};
            }
        }

        Log.d("lslsjkkdh", "settingHighChart: " + listExpense);
        series1.setData(new ArrayList<>(Arrays.asList(object10)));
        options.setSeries(new ArrayList<>(Arrays.asList(series1)));
        chartView.setOptions(options);
    }
}