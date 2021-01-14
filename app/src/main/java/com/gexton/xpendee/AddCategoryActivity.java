package com.gexton.xpendee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gexton.xpendee.Adapters.ColorsAdapter;
import com.gexton.xpendee.Adapters.ImageAdapter;
import com.gexton.xpendee.model.CategoryBean;
import com.gexton.xpendee.util.Database;
import com.gexton.xpendee.util.RecyclerItemClickListener;

import java.util.ArrayList;

public class AddCategoryActivity extends AppCompatActivity {
    ImageView img_cross, img_tick, image_view_selected;
    RecyclerView recyclerView;
    RecyclerView rvColors;
    RelativeLayout layout_complete;
    EditText etName;
    Database database;
    String catName, colorCode;
    int iconID;
    String fragment_value;

    int[] programImages = {
            R.mipmap.beauty,
            R.mipmap.bill,
            R.mipmap.car,
            R.mipmap.education,
            R.mipmap.entertainment,
            R.mipmap.family,
            R.mipmap.food,
            R.mipmap.gift,
            R.mipmap.grocery,
            R.mipmap.home,
            R.mipmap.other,
            R.mipmap.shopping,
            R.mipmap.sport,
            R.mipmap.transport,
            R.mipmap.travel,
            R.mipmap.work
    };

    String[] colorArray = {
            "#3D5AFE",
            "#2979FF",
            "#651FFF",
            "#D500F9",
            "#F50057",
            "#FF1744",
            "#00B0FF",
            "#00E5FF",
            "#1DE9B6",
            "#6495ED",
            "#CCCCFF",
            "#40E0D0",
            "#9FE2BF",
            "#DE3163",
            "#FF7F50",
            "#FFBF00",
            "#CD5C5C"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        database = new Database(AddCategoryActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.navy_blue, this.getTheme()));
        }

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        rvColors = findViewById(R.id.rvColors);
        rvColors.setHasFixedSize(true);

        etName = findViewById(R.id.etName);

        if (!TextUtils.isEmpty(getIntent().getStringExtra("farment_value"))) {
            fragment_value = getIntent().getStringExtra("farment_value");
        }

        ImageAdapter imageAdapter = new ImageAdapter(this, programImages);
        recyclerView.setAdapter(imageAdapter);
        img_cross = findViewById(R.id.img_cross);
        img_tick = findViewById(R.id.img_tick);
        image_view_selected = findViewById(R.id.image_view_selected);
        layout_complete = findViewById(R.id.layout_complete);

        img_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        int numberOfColumns = 3;
        RecyclerView.LayoutManager mLayoutManagerRVBP = new GridLayoutManager(AddCategoryActivity.this, numberOfColumns);
        recyclerView.setLayoutManager(mLayoutManagerRVBP);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(AddCategoryActivity.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int drawableID = programImages[position];
                image_view_selected.setImageResource(drawableID);
                iconID = drawableID;
            }

            @Override
            public void onItemLongClick(View view, int position) {
                System.out.println("-- item long press pos: " + position);
            }
        }));

        ColorsAdapter colorsAdapter = new ColorsAdapter(this, colorArray);
        rvColors.setAdapter(colorsAdapter);
        rvColors.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rvColors.addOnItemTouchListener(new RecyclerItemClickListener(AddCategoryActivity.this, rvColors, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String color_code = colorArray[position];
                //layout_complete.setBackgroundColor(Color.parseColor(color_code));

                layout_complete.setBackground(new ColorDrawable(Color.parseColor(color_code)));
                colorCode = color_code;

            }

            @Override
            public void onItemLongClick(View view, int position) {
                System.out.println("-- item long press pos: " + position);
            }
        }));

        img_tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                catName = etName.getText().toString().trim();

                //if(catName.isEmpty()) ye null ko check nahi karega
                if (TextUtils.isEmpty(catName)) {
                    Toast.makeText(AddCategoryActivity.this, "Please enter name for the category", Toast.LENGTH_LONG).show();
                } else if (iconID == 0) {
                    Toast.makeText(AddCategoryActivity.this, "Please select icon for the category", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(colorCode)) {
                    Toast.makeText(AddCategoryActivity.this, "Please select color for the category", Toast.LENGTH_LONG).show();
                } else {
                    if (fragment_value.equals("EXPENCE")) {
                        CategoryBean categoryBean = new CategoryBean(0, catName, iconID, colorCode, 1);
                        database.insertCategory(categoryBean);
                        Toast.makeText(AddCategoryActivity.this, "Expence Added !", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else if (fragment_value.equals("INCOME")) {
                        CategoryBean categoryBean = new CategoryBean(0, catName, iconID, colorCode, 2);
                        database.insertCategory(categoryBean);
                        Toast.makeText(AddCategoryActivity.this, "Income Added !", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }
            }
        });
    }
}