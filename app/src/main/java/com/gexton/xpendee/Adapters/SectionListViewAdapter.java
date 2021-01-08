package com.gexton.xpendee.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gexton.xpendee.R;
import com.gexton.xpendee.model.DateBean;
import com.gexton.xpendee.model.ExpenseBean;

import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SectionListViewAdapter extends BaseAdapter {
    ArrayList<ExpenseBean> list;
    ArrayList<String> dateList;
    Context context;
    private static final int ITEM = 0;
    private static final int HEADER = 1;
    LayoutInflater inflater;

    public SectionListViewAdapter(ArrayList<ExpenseBean> list, ArrayList<String> dateList, Context context) {
        this.list = list;
        this.dateList = dateList;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof ExpenseBean) {
            return ITEM;
        } else {
            return HEADER;
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            switch (getItemViewType(i)) {
                case ITEM:
                    view = inflater.inflate(R.layout.item_section_header, null);
                    break;
                case HEADER:
                    view = inflater.inflate(R.layout.item_section_header, null);
                    break;
            }
        }

        switch (getItemViewType(i)) {
            case ITEM:
                ImageView image_view = view.findViewById(R.id.image_view);
                TextView tv_image_path = view.findViewById(R.id.tv_image_path);
                TextView tv_expense_amount = view.findViewById(R.id.tv_expense_amount);
                TextView tv_catName = view.findViewById(R.id.tv_catName);
                TextView tv_description = view.findViewById(R.id.tv_description);

                tv_expense_amount.setText(((ExpenseBean) list.get(i)).currentDay);
                tv_catName.setText(((ExpenseBean) list.get(i)).categoryName);
                tv_description.setText(((ExpenseBean) list.get(i)).description);
                tv_image_path.setText(((ExpenseBean) list.get(i)).imagePath);
                image_view.setImageResource(((ExpenseBean) list.get(i)).categoryIcon);

                break;
            case HEADER:
                TextView tv_date = view.findViewById(R.id.tv_date);
                tv_date.setText(((String) dateList.get(i)));
                break;
        }
        return view;
    }
}