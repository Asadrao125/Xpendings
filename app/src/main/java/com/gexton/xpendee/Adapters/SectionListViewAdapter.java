package com.gexton.xpendee.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.xpendee.R;
import com.gexton.xpendee.model.DateBean;
import com.gexton.xpendee.model.ExpenseBean;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
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
    String stringPath;

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
                    view = inflater.inflate(R.layout.item_expense_header, null);
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
                TextView tv_date = view.findViewById(R.id.tv_date);
                ImageView image_path = view.findViewById(R.id.image_path);
                RelativeLayout lay1 = view.findViewById(R.id.lay1);

                String val = String.valueOf(((ExpenseBean) list.get(i)).expense);

                tv_expense_amount.setText("PKR " + val);
                tv_catName.setText((list.get(i)).categoryName);
                tv_description.setText((list.get(i)).description);
                tv_image_path.setText((list.get(i)).imagePath);
                image_view.setImageResource((list.get(i)).categoryIcon);
                tv_date.setText((list.get(i)).currentDay);

                if (!TextUtils.isEmpty((list.get(i)).colorCode)) {
                    GradientDrawable background = (GradientDrawable) lay1.getBackground();
                    background.setColor(Color.parseColor((list.get(i)).colorCode));
                }

                //Getting image from phone using image path
                try {
                    if (TextUtils.isEmpty(list.get(i).imagePath)) {
                        image_path.setVisibility(View.GONE);
                    } else {
                        File file = new File(list.get(i).imagePath);
                        Picasso.get().load(file).into(image_path);
                        System.out.println("-- file path " + file.getAbsolutePath());
                        image_path.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
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

                break;
            case HEADER:
                TextView tv_date1 = view.findViewById(R.id.tv_date);
                tv_date1.setText(((String) dateList.get(i)));
                break;
        }
        return view;
    }
}