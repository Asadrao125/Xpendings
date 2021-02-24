package com.gexton.xpendings.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gexton.xpendings.R;
import com.gexton.xpendings.model.ExpenseBean;
import com.gexton.xpendings.util.Database;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;

public class TestAdapter extends BaseAdapter {

    ArrayList<ExpenseBean> list;
    Context context;
    private static final int ITEM = 0;
    LayoutInflater inflater;
    Database database;

    public TestAdapter(ArrayList<ExpenseBean> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        database = new Database(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof ExpenseBean) {
            return ITEM;
        } else {
            return ITEM;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            switch (getItemViewType(i)) {
                case ITEM:
                    view = inflater.inflate(R.layout.item_for_spending_overview, null);
                    break;
            }
        }

        switch (getItemViewType(i)) {
            case ITEM:
                TextView tv_cat_name = view.findViewById(R.id.tvCatName);
                RelativeLayout layout = view.findViewById(R.id.layout_layout);
                ImageView icon = view.findViewById(R.id.image_view);
                TextView tvTransactionSize = view.findViewById(R.id.tvTransactionSize);
                TextView tvAmount = view.findViewById(R.id.tvAmount);

                tv_cat_name.setText((list.get(i)).categoryName);
                icon.setImageResource((list.get(i)).categoryIcon);

                //Setting tint to icon
                icon.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

                //Setting circle background color
                if (!TextUtils.isEmpty((list.get(i)).colorCode)) {
                    GradientDrawable background = (GradientDrawable) layout.getBackground();
                    background.setColor(Color.parseColor((list.get(i)).colorCode));
                }

                double amount = 0;
                if (database.getExpenseByName((list.get(i).categoryName)) != null) {
                    for (int j = 0; j < database.getExpenseByName((list.get(i).categoryName)).size(); j++) {
                        amount = amount + database.getExpenseByName((list.get(i).categoryName)).get(j).expense;
                    }
                }
                if (list.get(i).flag == 1) {
                    tvAmount.setTextColor(Color.RED);
                    tvAmount.setText("-PKR " + amount);
                } else {
                    tvAmount.setTextColor(context.getResources().getColor(R.color.green));
                    tvAmount.setText("PKR " + amount);
                }

                //Getting transaction size for each category
                if (database.getExpenseByName((list.get(i)).categoryName) != null) {
                    if (database.getExpenseByName((list.get(i)).categoryName).size() == 1) {
                        tvTransactionSize.setText(database.getExpenseByName((list.get(i)).categoryName).size() + " transaction");
                    } else {
                        tvTransactionSize.setText(database.getExpenseByName((list.get(i)).categoryName).size() + " transactions");
                    }
                } else {
                    tvTransactionSize.setText("0 transactions");
                }

                break;

        }
        return view;
    }
}
