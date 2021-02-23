package com.gexton.xpendee.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gexton.xpendee.R;
import com.gexton.xpendee.model.CategoryBean;
import com.gexton.xpendee.model.ExpenseBean;
import com.gexton.xpendee.util.Database;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoriesAdapterForSpendingOverview extends RecyclerView.Adapter<CategoriesAdapterForSpendingOverview.MyViewHolder> {
    Context context;
    CategoryBean categoryBean;
    ArrayList<CategoryBean> categoryBeanArrayList;
    Database database;

    public CategoriesAdapterForSpendingOverview(Context context, ArrayList<CategoryBean> categoryBeanArrayList) {
        this.context = context;
        this.categoryBeanArrayList = categoryBeanArrayList;
        database = new Database(context);
    }

    @NonNull
    @Override
    public CategoriesAdapterForSpendingOverview.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_for_spending_overview, parent, false);
        return new CategoriesAdapterForSpendingOverview.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapterForSpendingOverview.MyViewHolder holder, int position) {

        categoryBean = categoryBeanArrayList.get(position);
        holder.tv_cat_name.setText(categoryBean.categoryName);
        holder.icon.setImageResource(categoryBean.categoryIcon);

        GradientDrawable background = (GradientDrawable) holder.layout.getBackground();
        background.setColor(Color.parseColor(categoryBean.categoryHashCode));

        if (database.getExpenseByName(categoryBean.categoryName) != null) {
            if (database.getExpenseByName(categoryBean.categoryName).size() == 1) {
                holder.tvTransactionSize.setText(database.getExpenseByName(categoryBean.categoryName).size() + " transaction");
            } else {
                holder.tvTransactionSize.setText(database.getExpenseByName(categoryBean.categoryName).size() + " transactions");
            }
        } else {
            holder.tvTransactionSize.setText("0 transactions");
        }

        if (categoryBean.catFlag == 1) {
            ArrayList<ExpenseBean> list = new ArrayList<>();
            list = database.getAllIncome(1);
            double exp = 0;

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).categoryName.equals(categoryBean.categoryName)) {
                    exp = exp + list.get(i).expense;
                }
            }

            if (exp == 0) {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }

            holder.tvAmount.setText("-PKR " + exp);
            exp = 0;
        } else {
            ArrayList<ExpenseBean> list = new ArrayList<>();
            list = database.getAllIncome(2);
            double exp = 0;

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).categoryName.equals(categoryBean.categoryName)) {
                    exp = exp + list.get(i).expense;
                }
            }

            if (exp == 0) {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }

            holder.tvAmount.setTextColor(Color.GREEN);
            holder.tvAmount.setText("PKR " + exp);
            exp = 0;
        }

    }

    @Override
    public int getItemCount() {
        return categoryBeanArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_cat_name;
        private RelativeLayout layout;
        private ImageView icon;
        private TextView tvTransactionSize, tvAmount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_cat_name = itemView.findViewById(R.id.tvCatName);
            layout = itemView.findViewById(R.id.layout_layout);
            icon = itemView.findViewById(R.id.image_view);
            tvTransactionSize = itemView.findViewById(R.id.tvTransactionSize);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }

}
