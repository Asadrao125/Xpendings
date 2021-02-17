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
import com.gexton.xpendee.util.Database;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VisiblistyCategoriesListAdapter extends RecyclerView.Adapter<VisiblistyCategoriesListAdapter.MyViewHolder> {

    Context context;
    CategoryBean categoryBean;
    ArrayList<CategoryBean> categoryBeanArrayList;
    Database database;

    public VisiblistyCategoriesListAdapter(Context context, ArrayList<CategoryBean> categoryBeanArrayList) {
        this.context = context;
        this.categoryBeanArrayList = categoryBeanArrayList;
        database = new Database(context);
    }

    @NonNull
    @Override
    public VisiblistyCategoriesListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.visiblisty_item_categories, parent, false);
        return new VisiblistyCategoriesListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisiblistyCategoriesListAdapter.MyViewHolder holder, int position) {

        categoryBean = categoryBeanArrayList.get(position);
        holder.tv_cat_name.setText(categoryBean.categoryName);
        holder.icon.setImageResource(categoryBean.categoryIcon);

        GradientDrawable background = (GradientDrawable) holder.layout.getBackground();
        background.setColor(Color.parseColor(categoryBean.categoryHashCode));

        holder.visiblity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.visiblity.getDrawable().getConstantState() == context.getResources().getDrawable(R.drawable.show_visiblity).getConstantState()) {
                    holder.visiblity.setImageResource(R.drawable.hide_visiblity);

                    String name = categoryBeanArrayList.get(position).categoryName;
                    String hex_code = categoryBeanArrayList.get(position).categoryHashCode;
                    int icon = categoryBeanArrayList.get(position).categoryIcon;
                    int flag = categoryBeanArrayList.get(position).catFlag;
                    int id = categoryBeanArrayList.get(position).id;
                    int visiblity = categoryBeanArrayList.get(position).visiblity;
                    categoryBean = new CategoryBean(id, name, icon, hex_code, flag, 0);
                    Log.d("insert", "onClick: " + name + "\n" + id + "\n" + flag + "\n" + visiblity);
                    database.updateCategory(categoryBean, id);

                } else {
                    holder.visiblity.setImageResource(R.drawable.show_visiblity);

                    String name = categoryBeanArrayList.get(position).categoryName;
                    String hex_code = categoryBeanArrayList.get(position).categoryHashCode;
                    int icon = categoryBeanArrayList.get(position).categoryIcon;
                    int flag = categoryBeanArrayList.get(position).catFlag;
                    int id = categoryBeanArrayList.get(position).id;
                    categoryBean = new CategoryBean(id, name, icon, hex_code, flag, 1);
                    Log.d("insert", "onClick: " + name + "\n" + id + "\n" + flag);
                    database.updateCategory(categoryBean, id);

                }
            }
        });

        if (categoryBeanArrayList.get(position).visiblity == 1) {
            holder.visiblity.setImageResource(R.drawable.show_visiblity);
        } else {
            holder.visiblity.setImageResource(R.drawable.hide_visiblity);
        }

    }

    @Override
    public int getItemCount() {
        return categoryBeanArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_cat_name;
        private RelativeLayout layout;
        private ImageView icon, visiblity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_cat_name = itemView.findViewById(R.id.tvCatName);
            layout = itemView.findViewById(R.id.layout_layout);
            icon = itemView.findViewById(R.id.image_view);
            visiblity = itemView.findViewById(R.id.visiblity);
        }
    }

}
