package com.gexton.xpendee.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.gexton.xpendee.R;
import com.gexton.xpendee.ViewImageActivity;
import com.gexton.xpendee.model.ExpenseBean;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class SectionListViewAdapter extends BaseAdapter {
    ArrayList<ExpenseBean> list;
    Context context;
    private static final int ITEM = 0;
    LayoutInflater inflater;
    String current_date;

    public SectionListViewAdapter(ArrayList<ExpenseBean> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    view = inflater.inflate(R.layout.item_section_header, null);
                    break;
            }
        }

        switch (getItemViewType(i)) {
            case ITEM:
                ImageView image_view = view.findViewById(R.id.image_view);
                TextView tv_expense_amount = view.findViewById(R.id.tv_expense_amount);
                TextView tv_catName = view.findViewById(R.id.tv_catName);
                TextView tv_description = view.findViewById(R.id.tv_description);
                TextView tv_date = view.findViewById(R.id.tv_date);
                ImageView image_path = view.findViewById(R.id.image_path);
                RelativeLayout lay1 = view.findViewById(R.id.lay1);
                CardView cvImage = view.findViewById(R.id.cvImage);

                String val = String.valueOf(((ExpenseBean) list.get(i)).expense);

                tv_catName.setText((list.get(i)).categoryName);
                tv_description.setText((list.get(i)).description);
                image_view.setImageResource((list.get(i)).categoryIcon);

                //Setting tint to icon
                image_view.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

                // Directing to view image
                image_path.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (list.get(i).imagePath != null) {
                            Intent intent = new Intent(context, ViewImageActivity.class);
                            intent.putExtra("image_path", list.get(i).imagePath);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "No Image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //Getting current date
                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);
                SimpleDateFormat df = new SimpleDateFormat("dd MMMM", Locale.getDefault());
                current_date = df.format(c);

                DateTimeFormatter dtf = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
                    DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd MMMM", Locale.ENGLISH);
                    LocalDate ld = LocalDate.parse(list.get(i).currentDay, dtf);
                    String month_name = dtf2.format(ld);

                    if (current_date.equals(month_name)) {
                        tv_date.setText(month_name);
                    } else {
                        tv_date.setText(month_name);
                    }

                }

                if (list.get(i).flag == 1 /*Expense*/) {
                    tv_expense_amount.setTextColor(Color.parseColor("#FF0000"));
                    tv_expense_amount.setText("-PKR " + val);
                } else if (list.get(i).flag == 2 /*Income*/) {
                    tv_expense_amount.setTextColor(Color.parseColor("#24b554"));
                    tv_expense_amount.setText("+PKR " + val);
                }

                if (!TextUtils.isEmpty((list.get(i)).colorCode)) {
                    GradientDrawable background = (GradientDrawable) lay1.getBackground();
                    background.setColor(Color.parseColor((list.get(i)).colorCode));
                }

                //Getting image from phone using image path
                try {
                    if (TextUtils.isEmpty(list.get(i).imagePath)) {
                        image_path.setVisibility(View.GONE);
                        cvImage.setVisibility(View.GONE);
                    } else {
                        File file = new File(list.get(i).imagePath);
                        Picasso.get().load(file).into(image_path);
                        cvImage.setVisibility(View.VISIBLE);
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

        }
        return view;
    }
}