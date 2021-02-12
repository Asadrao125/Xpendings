package com.gexton.xpendee;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.ContentUriUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.xpendee.Adapters.CategoriesAdapterForExpense;
import com.gexton.xpendee.model.CategoryBean;
import com.gexton.xpendee.model.ExpenseBean;
import com.gexton.xpendee.model.WalletBean;
import com.gexton.xpendee.util.Database;
import com.gexton.xpendee.util.RecyclerItemClickListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddIncomeActivity extends AppCompatActivity {
    ArrayList<CategoryBean> categoryBeanArrayList;
    CategoriesAdapterForExpense adapter = null;
    RecyclerView rvCategories;
    Database database;
    TextView tv_current_day, tv_date, tv_save, tv_reset;
    @SuppressLint("StaticFieldLeak")
    public static ImageView img_1, img_2, img_3, img_4, img_5, img_6;
    public static String img_path1, img_path2, img_path3, img_path4, img_path5, img_path6;
    String image_path, current_date, description, currency = "PKR", user_selected_date, catName, color_code;
    RelativeLayout current_day_layout, select_image_layout, no_data_layout;
    EditText edt_description, edt_balance;
    int categoryIcon;
    Double expense_amount;
    String colorHex;
    ImageView img_cross, img_camera;
    TextView tv_details, tv_categories;
    Calendar myCalendar;
    private ArrayList<Uri> photoPaths = new ArrayList<>();
    final int CUSTOM_REQUEST_CODE = 987;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.navy_blue, this.getTheme()));
        }

        database = new Database(AddIncomeActivity.this);

        rvCategories = findViewById(R.id.rvCategories);
        tv_current_day = findViewById(R.id.tv_current_day);
        img_1 = findViewById(R.id.img_1);
        img_2 = findViewById(R.id.img_2);
        img_3 = findViewById(R.id.img_3);
        img_4 = findViewById(R.id.img_4);
        img_5 = findViewById(R.id.img_5);
        img_6 = findViewById(R.id.img_6);
        current_day_layout = findViewById(R.id.current_day_layout);
        tv_date = findViewById(R.id.tv_date);
        edt_description = findViewById(R.id.edt_description);
        select_image_layout = findViewById(R.id.select_image_layout);
        edt_balance = findViewById(R.id.edt_balance);
        tv_save = findViewById(R.id.tv_save);
        tv_reset = findViewById(R.id.tv_reset);
        img_cross = findViewById(R.id.img_cross);
        tv_details = findViewById(R.id.tv_details);
        tv_categories = findViewById(R.id.tv_categories);
        no_data_layout = findViewById(R.id.no_data_layout);
        myCalendar = Calendar.getInstance();
        img_camera = findViewById(R.id.img_camera);

        img_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        tv_current_day.setText(dayOfTheWeek);

        tv_current_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
                tv_date.setText(formattedDate);
                user_selected_date = formattedDate;

            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd-MM-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                tv_date.setText(sdf.format(myCalendar.getTime()));
                user_selected_date = sdf.format(myCalendar.getTime());
            }
        };

        current_day_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(AddIncomeActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        select_image_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPhoto();
            }
        });

        img_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPhoto();
            }
        });

        //Getting current date
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        current_date = df.format(c);//current date end

        database = new Database(getApplicationContext());

        int numberOfColumns = 3;
        RecyclerView.LayoutManager mLayoutManagerRVBP = new GridLayoutManager(getApplicationContext(), numberOfColumns);
        rvCategories.setLayoutManager(mLayoutManagerRVBP);

        categoryBeanArrayList = new ArrayList<>();

        if (database.getAllCategories(2) != null) {
            categoryBeanArrayList = database.getAllCategories(2);
            adapter = new CategoriesAdapterForExpense(this, categoryBeanArrayList);
            rvCategories.setAdapter(adapter);
            rvCategories.setVisibility(View.VISIBLE);
            tv_categories.setVisibility(View.VISIBLE);
            no_data_layout.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv_details.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.rvCategories);
            tv_details.setLayoutParams(params);

        } else {
            rvCategories.setVisibility(View.GONE);
            tv_categories.setVisibility(View.GONE);
            no_data_layout.setVisibility(View.VISIBLE);
        }

        rvCategories.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                rvCategories, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                catName = categoryBeanArrayList.get(position).categoryName;
                categoryIcon = categoryBeanArrayList.get(position).categoryIcon;
                color_code = categoryBeanArrayList.get(position).categoryHashCode;
                colorHex = color_code;

                adapter.selectedPos = position;
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onItemLongClick(View view, int position) {
                System.out.println("-- item long press pos: " + position);
            }
        }));

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String expense = edt_balance.getText().toString().trim();
                description = edt_description.getText().toString().trim();

                if (TextUtils.isEmpty(expense)) {
                    Toast.makeText(AddIncomeActivity.this, "Please enter expence amount", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(currency)) {
                    Toast.makeText(AddIncomeActivity.this, "Please enter currency", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(catName)) {
                    Toast.makeText(AddIncomeActivity.this, "Please select category", Toast.LENGTH_SHORT).show();
                } else if (categoryIcon == 0) {
                    Toast.makeText(AddIncomeActivity.this, "Please select category", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(user_selected_date)) {
                    Toast.makeText(AddIncomeActivity.this, "Please enter date", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(description)) {
                    Toast.makeText(AddIncomeActivity.this, "Please enter description", Toast.LENGTH_SHORT).show();
                } else {

                    SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
                    String json = prefs1.getString("Wallet_Bean", "");
                    Gson gson = new Gson();
                    WalletBean walletBean = gson.fromJson(json, WalletBean.class);

                    Double balance = walletBean.balance;
                    String currency = walletBean.currency;
                    String walletName = walletBean.wallet_name;

                    expense_amount = Double.parseDouble(expense);
                    Double newBalance = balance + expense_amount;

                    //Toast.makeText(AddIncomeActivity.this, "" + currency + "\n" + expense_amount + "\n" + categoryIcon + "\n" + catName + "\n" + color_code, Toast.LENGTH_SHORT).show();
                    ExpenseBean expenseBean = new ExpenseBean(0, currency, expense_amount, categoryIcon,
                            catName, user_selected_date, description, image_path, colorHex, 2);
                    database.insertExpense(expenseBean);
                    Toast.makeText(AddIncomeActivity.this, "Income Added ! " /*+ database.insertExpense(expenseBean)*/, Toast.LENGTH_SHORT).show();

                    //Toast.makeText(AddIncomeActivity.this, "Your new wallet amount is " + newBalance, Toast.LENGTH_SHORT).show();
                    WalletBean newWalletBean = new WalletBean(newBalance, walletName, currency);

                    Gson newGson = new Gson();
                    String newJson = newGson.toJson(newWalletBean);

                    SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                    editor.putString("Wallet_Bean", newJson);
                    editor.apply();

                    onBackPressed();
                }
            }
        });

        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_balance.setText("");
                edt_description.setText("");
                tv_date.setText("");
                user_selected_date = "";
                adapter.selectedPos = 0;
                adapter.notifyDataSetChanged();

            }
        });

        img_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_1.setBackgroundResource(R.drawable.square_shape);
                img_2.setBackgroundResource(R.drawable.cirrcle_empty);
                img_3.setBackgroundResource(R.drawable.cirrcle_empty);
                img_4.setBackgroundResource(R.drawable.cirrcle_empty);
                img_5.setBackgroundResource(R.drawable.cirrcle_empty);
                img_6.setBackgroundResource(R.drawable.cirrcle_empty);
                image_path = img_path1;
            }
        });

        img_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_1.setBackgroundResource(R.drawable.cirrcle_empty);
                img_2.setBackgroundResource(R.drawable.square_shape);
                img_3.setBackgroundResource(R.drawable.cirrcle_empty);
                img_4.setBackgroundResource(R.drawable.cirrcle_empty);
                img_5.setBackgroundResource(R.drawable.cirrcle_empty);
                img_6.setBackgroundResource(R.drawable.cirrcle_empty);
                image_path = img_path2;
            }
        });

        img_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_1.setBackgroundResource(R.drawable.cirrcle_empty);
                img_2.setBackgroundResource(R.drawable.cirrcle_empty);
                img_3.setBackgroundResource(R.drawable.square_shape);
                img_4.setBackgroundResource(R.drawable.cirrcle_empty);
                img_5.setBackgroundResource(R.drawable.cirrcle_empty);
                img_6.setBackgroundResource(R.drawable.cirrcle_empty);
                image_path = img_path3;
            }
        });

        img_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_1.setBackgroundResource(R.drawable.cirrcle_empty);
                img_2.setBackgroundResource(R.drawable.cirrcle_empty);
                img_3.setBackgroundResource(R.drawable.cirrcle_empty);
                img_4.setBackgroundResource(R.drawable.square_shape);
                img_5.setBackgroundResource(R.drawable.cirrcle_empty);
                img_6.setBackgroundResource(R.drawable.cirrcle_empty);
                image_path = img_path4;
            }
        });

        img_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_1.setBackgroundResource(R.drawable.cirrcle_empty);
                img_2.setBackgroundResource(R.drawable.cirrcle_empty);
                img_3.setBackgroundResource(R.drawable.cirrcle_empty);
                img_4.setBackgroundResource(R.drawable.cirrcle_empty);
                img_5.setBackgroundResource(R.drawable.square_shape);
                img_6.setBackgroundResource(R.drawable.cirrcle_empty);
                image_path = img_path5;
            }
        });

        img_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_1.setBackgroundResource(R.drawable.cirrcle_empty);
                img_2.setBackgroundResource(R.drawable.cirrcle_empty);
                img_3.setBackgroundResource(R.drawable.cirrcle_empty);
                img_4.setBackgroundResource(R.drawable.cirrcle_empty);
                img_5.setBackgroundResource(R.drawable.cirrcle_empty);
                img_6.setBackgroundResource(R.drawable.square_shape);
                image_path = img_path6;
            }
        });

        fetchGalleryImages(AddIncomeActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CUSTOM_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
            if (dataList != null) {
                photoPaths = new ArrayList<Uri>();
                photoPaths.addAll(dataList);

                try {
                    image_path = ContentUriUtils.INSTANCE.getFilePath(AddIncomeActivity.this, photoPaths.get(0));
                    if (image_path != null) {
                        //Toast.makeText(AddIncomeActivity.this, image_path, Toast.LENGTH_SHORT).show();
                        File file = new File(image_path);
                        Picasso.get().load(file).into(img_1);
                        System.out.println("-- file path " + file.getAbsolutePath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void pickPhoto() {
        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setSelectedFiles(photoPaths) //this is optional
                .setActivityTheme(R.style.FilePickerTheme)
                .setActivityTitle("Please select media")
                .setImageSizeLimit(5)
                .setVideoSizeLimit(10)
                .setSpan(FilePickerConst.SPAN_TYPE.FOLDER_SPAN, 3)
                .setSpan(FilePickerConst.SPAN_TYPE.DETAIL_SPAN, 4)
                .enableVideoPicker(false)
                .enableCameraSupport(true)
                .showGifs(false)
                .showFolderView(true)
                .enableSelectAll(false)
                .enableImagePicker(true)
                .setCameraPlaceholder(R.drawable.ic_camera)
                .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .pickPhoto(this, CUSTOM_REQUEST_CODE);
    }

    public ArrayList<String> fetchGalleryImages(Activity context) {
        ArrayList<String> galleryImageUrls;
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};//get all columns of type images
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;//order data by date

        Cursor imagecursor = context.managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy + " DESC");//get all data in Cursor by sorting in DESC order

        galleryImageUrls = new ArrayList<String>();

        for (int i = 0; i < imagecursor.getCount(); i++) {
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);//get column index
            galleryImageUrls.add(imagecursor.getString(dataColumnIndex));//get Image from column index
        }


        if (galleryImageUrls.size() > 0 && galleryImageUrls != null) {
            //Getting 6 gallery images
            img_path1 = galleryImageUrls.get(0);
            img_path2 = galleryImageUrls.get(1);
            img_path3 = galleryImageUrls.get(2);
            img_path4 = galleryImageUrls.get(3);
            img_path5 = galleryImageUrls.get(4);
            img_path6 = galleryImageUrls.get(5);

            File file1 = new File(img_path1);
            Picasso.get().load(file1).into(img_1);

            File file2 = new File(img_path2);
            Picasso.get().load(file2).into(img_2);

            File file3 = new File(img_path3);
            Picasso.get().load(file3).into(img_3);

            File file4 = new File(img_path4);
            Picasso.get().load(file4).into(img_4);

            File file5 = new File(img_path5);
            Picasso.get().load(file5).into(img_5);

            File file6 = new File(img_path6);
            Picasso.get().load(file6).into(img_6);

            Log.e("fatch_in", "images: " + galleryImageUrls);
        }

        return galleryImageUrls;
    }

}