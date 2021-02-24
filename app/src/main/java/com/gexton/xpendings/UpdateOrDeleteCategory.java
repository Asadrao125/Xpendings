package com.gexton.xpendings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.xpendings.adapters.ColorsAdapter;
import com.gexton.xpendings.adapters.ImageAdapter;
import com.gexton.xpendings.model.CategoryBean;
import com.gexton.xpendings.util.AdUtil;
import com.gexton.xpendings.util.Database;
import com.gexton.xpendings.util.RecyclerItemClickListener;
import com.google.android.gms.ads.AdView;

public class UpdateOrDeleteCategory extends AppCompatActivity {
    ImageView img_cross, img_tick, image_view_selected;
    RecyclerView recyclerView;
    RecyclerView rvColors;
    RelativeLayout layout_complete;
    EditText etName;
    Database database;
    String catName, colorCode;
    int iconID;
    TextView tv_delete;

    //Getting Intent data
    int id, resID, flag, pos;
    String category_name, color_code;
    ImageAdapter imageAdapter;

    int[] programImages = {
            R.drawable.beauty,
            R.drawable.bill,
            R.drawable.car,
            R.drawable.education,
            R.drawable.entertainment,
            R.drawable.family,
            R.drawable.food,
            R.drawable.gift,
            R.drawable.grocery,
            R.drawable.home,
            R.drawable.other,
            R.drawable.shopping,
            R.drawable.sport,
            R.drawable.transport,
            R.drawable.travel,
            R.drawable.work
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
        setContentView(R.layout.activity_update_or_delete_category);

        database = new Database(UpdateOrDeleteCategory.this);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        rvColors = findViewById(R.id.rvColors);
        rvColors.setHasFixedSize(true);

        etName = findViewById(R.id.etName);

        id = getIntent().getIntExtra("id", 10000);
        resID = getIntent().getIntExtra("resId", 10000);
        flag = getIntent().getIntExtra("flag", 10000);
        pos = getIntent().getIntExtra("position", 10000);

        category_name = getIntent().getStringExtra("category_name");
        color_code = getIntent().getStringExtra("color_code");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.parseColor(color_code));
        }

        catName = category_name;
        iconID = resID;
        colorCode = color_code;

        imageAdapter = new ImageAdapter(this, programImages);
        recyclerView.setAdapter(imageAdapter);
        img_cross = findViewById(R.id.img_cross);
        img_tick = findViewById(R.id.img_tick);
        image_view_selected = findViewById(R.id.image_view_selected);
        layout_complete = findViewById(R.id.layout_complete);
        tv_delete = findViewById(R.id.tv_delete);

        AdView adView = findViewById(R.id.adView);
        AdUtil adUtil = new AdUtil(this);
        adUtil.loadBannerAd(adView);

        img_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (id != 0 && !TextUtils.isEmpty(category_name) && !TextUtils.isEmpty(color_code) && resID != 0) {
            etName.setText(category_name);
            layout_complete.setBackgroundColor(Color.parseColor(color_code));
            image_view_selected.setImageResource(resID);
        }

        int numberOfColumns = 4;
        RecyclerView.LayoutManager mLayoutManagerRVBP = new GridLayoutManager(getApplicationContext(), numberOfColumns);
        recyclerView.setLayoutManager(mLayoutManagerRVBP);

        ColorsAdapter colorsAdapter = new ColorsAdapter(this, colorArray);
        rvColors.setAdapter(colorsAdapter);
        rvColors.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
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

        rvColors.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), rvColors, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String color_code = colorArray[position];
                layout_complete.setBackgroundColor(Color.parseColor(color_code));
                colorCode = color_code;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getWindow().setStatusBarColor(Color.parseColor(colorCode));
                }

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
                    Toast.makeText(getApplicationContext(), "Please enter name for the category", Toast.LENGTH_LONG).show();
                } else if (iconID == 0) {
                    Toast.makeText(getApplicationContext(), "Please select icon for the category", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(colorCode)) {
                    Toast.makeText(getApplicationContext(), "Please select color for the category", Toast.LENGTH_LONG).show();
                } else {

                    CategoryBean categoryBean = new CategoryBean(0, catName, iconID, colorCode, flag, 1);
                    database.updateCategory(categoryBean, id);
                    Toast.makeText(getApplicationContext(), "Updated Successfully !", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });

        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.deleteCategory(id);
                Toast.makeText(UpdateOrDeleteCategory.this, "Deleted", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
        builder1.setMessage("Are you sure you want to delete ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(UpdateOrDeleteCategory.this, "", Toast.LENGTH_SHORT).show();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}