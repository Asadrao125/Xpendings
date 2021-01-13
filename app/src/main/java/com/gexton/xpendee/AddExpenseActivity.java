package com.gexton.xpendee;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.ContentUriUtils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.xpendee.Adapters.CategoriesAdapterForExpense;
import com.gexton.xpendee.Adapters.CategoriesListAdapter;
import com.gexton.xpendee.model.CategoryBean;
import com.gexton.xpendee.model.ExpenseBean;
import com.gexton.xpendee.model.WalletBean;
import com.gexton.xpendee.util.Database;
import com.gexton.xpendee.util.RecyclerItemClickListener;
import com.google.gson.Gson;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.security.AccessController.getContext;

public class AddExpenseActivity extends AppCompatActivity {
    ArrayList<CategoryBean> categoryBeanArrayList;
    CategoriesAdapterForExpense adapter = null;
    RecyclerView rvCategories;
    Database database;
    TextView tv_current_day, tv_date, tv_save;
    ImageView img_1;
    String image_path, current_date, description, currency = "PKR", date, catName, color_code;
    RelativeLayout current_day_layout, select_image_layout;
    DatePickerDialog.OnDateSetListener mDateListener;
    EditText edt_description, edt_balance;
    int categoryIcon;
    Double expense_amount;
    String colorHex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        database = new Database(AddExpenseActivity.this);

        rvCategories = findViewById(R.id.rvCategories);
        tv_current_day = findViewById(R.id.tv_current_day);
        img_1 = findViewById(R.id.img_1);
        current_day_layout = findViewById(R.id.current_day_layout);
        tv_date = findViewById(R.id.tv_date);
        edt_description = findViewById(R.id.edt_description);
        select_image_layout = findViewById(R.id.select_image_layout);
        edt_balance = findViewById(R.id.edt_balance);
        tv_save = findViewById(R.id.tv_save);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        tv_current_day.setText(dayOfTheWeek);

        tv_current_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);
                SimpleDateFormat df = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());
                String formattedDate = df.format(c);

                tv_date.setText(formattedDate);

            }
        });

        current_day_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddExpenseActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateListener,
                        year, month, day
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                mDateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        Log.d("onDateSet", month + "/" + day + "/" + year);
                        tv_date.setText(new StringBuilder().append(day).append("-")
                                .append(month).append("-").append(year));
                        date = month + "/" + day + "/" + year;
                    }
                };
            }
        });

        select_image_layout.setOnClickListener(new View.OnClickListener() {
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

        if (database.getAllCategories(1) != null) {
            categoryBeanArrayList = database.getAllCategories(1);
            adapter = new CategoriesAdapterForExpense(this, categoryBeanArrayList);
            rvCategories.setAdapter(adapter);
        }

        rvCategories.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), rvCategories, new RecyclerItemClickListener.OnItemClickListener() {
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
                    Toast.makeText(AddExpenseActivity.this, "Please enter expence amount", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(currency)) {
                    Toast.makeText(AddExpenseActivity.this, "Please enter currency", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(catName)) {
                    Toast.makeText(AddExpenseActivity.this, "Please select category", Toast.LENGTH_SHORT).show();
                } else if (categoryIcon == 0) {
                    Toast.makeText(AddExpenseActivity.this, "Please select category", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(date)) {
                    Toast.makeText(AddExpenseActivity.this, "Please enter date", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(description)) {
                    Toast.makeText(AddExpenseActivity.this, "Please enter description", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(image_path)) {
                    Toast.makeText(AddExpenseActivity.this, "Please select image", Toast.LENGTH_SHORT).show();
                } else {

                    SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
                    String json = prefs1.getString("Wallet_Bean", "");
                    Gson gson = new Gson();
                    WalletBean walletBean = gson.fromJson(json, WalletBean.class);

                    Double balance = walletBean.balance;
                    String currency = walletBean.currency;
                    String walletName = walletBean.wallet_name;

                    expense_amount = Double.parseDouble(expense);
                    Double newBalance = balance - expense_amount;
                    if (walletBean.balance >= expense_amount) {
                        Toast.makeText(AddExpenseActivity.this, "" + currency + "\n" + expense_amount + "\n" + categoryIcon + "\n" + catName + "\n" + color_code, Toast.LENGTH_SHORT).show();
                        ExpenseBean expenseBean = new ExpenseBean(0, currency, expense_amount, categoryIcon,
                                catName, date, description, image_path, colorHex);
                        Toast.makeText(AddExpenseActivity.this, "Expense Added !" + database.insertExpense(expenseBean), Toast.LENGTH_SHORT).show();

                        Toast.makeText(AddExpenseActivity.this, "Your new wallet amount is " + newBalance, Toast.LENGTH_SHORT).show();
                        WalletBean newWalletBean = new WalletBean(newBalance, walletName, currency);

                        Gson newGson = new Gson();
                        String newJson = newGson.toJson(newWalletBean);

                        SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                        editor.putString("Wallet_Bean", newJson);
                        editor.apply();

                        onBackPressed();

                    } else {
                        Toast.makeText(AddExpenseActivity.this, "Sorry, increase your wallet to add this expense", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

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
                    image_path = ContentUriUtils.INSTANCE.getFilePath(AddExpenseActivity.this, photoPaths.get(0));
                    if (image_path != null) {
                        Toast.makeText(AddExpenseActivity.this, image_path, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ArrayList<Uri> photoPaths = new ArrayList<>();
    final int CUSTOM_REQUEST_CODE = 987;

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

}