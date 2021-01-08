package com.gexton.xpendee.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gexton.xpendee.R;
import com.gexton.xpendee.model.CategoryBean;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoriesAdapterForExpense extends RecyclerView.Adapter<CategoriesAdapterForExpense.MyViewHolder> {
    Context context;
    CategoryBean categoryBean;
    ArrayList<CategoryBean> categoryBeanArrayList;
    public int selectedPos = -1;

    public CategoriesAdapterForExpense(Context context, ArrayList<CategoryBean> categoryBeanArrayList) {
        this.context = context;
        this.categoryBeanArrayList = categoryBeanArrayList;
    }

    @NonNull
    @Override
    public CategoriesAdapterForExpense.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_image_for_add_expence, parent, false);
        return new CategoriesAdapterForExpense.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapterForExpense.MyViewHolder holder, int position) {
        categoryBean = categoryBeanArrayList.get(position);
        holder.tv_cat_name.setText(categoryBean.categoryName);
        holder.icon.setImageResource(categoryBean.categoryIcon);

        GradientDrawable background = (GradientDrawable) holder.layout.getBackground();
        background.setColor(Color.parseColor(categoryBean.categoryHashCode));


        holder.rvItem.setBackgroundColor(selectedPos == position ? Color.parseColor("#E0E0E0") : Color.parseColor("#FFFFFF"));
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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_cat_name = itemView.findViewById(R.id.tvCatName);
            layout = itemView.findViewById(R.id.layout_layout);
            icon = itemView.findViewById(R.id.image_view);
            rvItem  = itemView.findViewById(R.id.rvItem);
        }
    }

}