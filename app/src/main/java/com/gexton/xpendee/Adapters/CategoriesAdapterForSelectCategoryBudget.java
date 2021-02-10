package com.gexton.xpendee.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.xpendee.NewBudgetActivity;
import com.gexton.xpendee.R;
import com.gexton.xpendee.model.BudgetBean;
import com.gexton.xpendee.model.CategoryBean;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.MODE_PRIVATE;

public class CategoriesAdapterForSelectCategoryBudget extends RecyclerView.Adapter<CategoriesAdapterForSelectCategoryBudget.MyViewHolder> {
    Context context;
    CategoryBean categoryBean;
    ArrayList<CategoryBean> categoryBeanArrayList;
    public int selectedPos = -1;

    public CategoriesAdapterForSelectCategoryBudget(Context context, ArrayList<CategoryBean> categoryBeanArrayList) {
        this.context = context;
        this.categoryBeanArrayList = categoryBeanArrayList;
    }

    @NonNull
    @Override
    public CategoriesAdapterForSelectCategoryBudget.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_select_category_for_budget, parent, false);
        return new CategoriesAdapterForSelectCategoryBudget.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapterForSelectCategoryBudget.MyViewHolder holder, int position) {
        categoryBean = categoryBeanArrayList.get(position);
        holder.tv_cat_name.setText(categoryBean.categoryName);
        holder.icon.setImageResource(categoryBean.categoryIcon);

        GradientDrawable background = (GradientDrawable) holder.layout.getBackground();
        background.setColor(Color.parseColor(categoryBean.categoryHashCode));

        holder.rvItem.setBackgroundColor(selectedPos == position ? Color.parseColor("#E0E0E0") : Color.parseColor("#FFFFFF"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.cbCategory.isChecked()) {
                    holder.cbCategory.setChecked(false);
                } else {
                    holder.cbCategory.setChecked(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryBeanArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_cat_name;
        private RelativeLayout layout;
        private ImageView icon;
        RelativeLayout rvItem;
        CheckBox cbCategory;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_cat_name = itemView.findViewById(R.id.tvCatName);
            layout = itemView.findViewById(R.id.layout_layout);
            icon = itemView.findViewById(R.id.image_view);
            rvItem = itemView.findViewById(R.id.rvItem);
            cbCategory = itemView.findViewById(R.id.cbCategory);
        }
    }
}