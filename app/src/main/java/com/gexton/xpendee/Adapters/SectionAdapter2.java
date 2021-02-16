package com.gexton.xpendee.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gexton.xpendee.R;
import com.gexton.xpendee.model.ExpenseBean;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class SectionAdapter2 extends ArrayAdapter<ExpenseBean> {

    Context mContext;
    List<ExpenseBean> list;
    List<ExpenseBean> mHospitalBeansAll;
    int mLayoutResourceId;

    public SectionAdapter2(Context context, int resource, List<ExpenseBean> beanList) {
        super(context, resource, beanList);
        this.mContext = context;
        this.mLayoutResourceId = resource;
        this.list = new ArrayList<>(beanList);
        this.mHospitalBeansAll = new ArrayList<>(beanList);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Nullable
    @Override
    public ExpenseBean getItem(int position) {
        return list.get(position);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(mLayoutResourceId, parent, false);
            }
            ExpenseBean department = getItem(i);
            TextView name = (TextView) convertView.findViewById(R.id.tv_catName);
            TextView tv_expense_amount = (TextView) convertView.findViewById(R.id.tv_expense_amount);
            TextView tv_description = (TextView) convertView.findViewById(R.id.tv_description);
            ImageView image_path = (ImageView) convertView.findViewById(R.id.image_path);
            TextView tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            RelativeLayout lay1 = convertView.findViewById(R.id.lay1);
            ImageView image_view = convertView.findViewById(R.id.image_view);

            name.setText(department.categoryName);
            tv_expense_amount.setText("PKR -" + department.expense);
            tv_description.setText(department.description);
            image_path.setVisibility(View.GONE);
            tv_date.setText(department.currentDay);
            image_view.setImageResource((list.get(i)).categoryIcon);

            if (!TextUtils.isEmpty((list.get(i)).colorCode)) {
                GradientDrawable background = (GradientDrawable) lay1.getBackground();
                background.setColor(Color.parseColor((list.get(i)).colorCode));
            }

            if (i > 0) {
                if ((list.get(i)).currentDay.equals((list.get(i - 1)).currentDay)) {
                    tv_date.setVisibility(View.GONE);
                } else {
                    tv_date.setVisibility(View.VISIBLE);
                }
            } else {
                tv_date.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                return ((ExpenseBean) resultValue).categoryName;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<ExpenseBean> departmentsSuggestion = new ArrayList<>();
                if (constraint != null) {
                    for (ExpenseBean department : mHospitalBeansAll) {
                        if (department.categoryName.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            departmentsSuggestion.add(department);
                        }
                    }
                    filterResults.values = departmentsSuggestion;
                    filterResults.count = departmentsSuggestion.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list.clear();
                if (results != null && results.count > 0) {
                    for (Object object : (List<?>) results.values) {
                        if (object instanceof ExpenseBean) {
                            list.add((ExpenseBean) object);
                        }
                    }
                    notifyDataSetChanged();
                } else if (constraint == null) {
                    list.addAll(mHospitalBeansAll);
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}
