package com.gexton.xpendee;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import com.gexton.xpendee.util.Database;
import com.gexton.xpendee.util.RecyclerItemClickListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {
    ArrayList<CategoryBean> categoryBeanArrayList;
    CategoriesAdapterForExpense adapter = null;
    RecyclerView rvCategories;
    Database database;
    TextView tv_current_day, tv_date, tv_save;
    int ACTION_REQUEST_GALLERY = 999;
    ImageView img_1;
    String image_path, current_date, description, currency = "PKR", date, catName;
    RelativeLayout current_day_layout, select_image_layout;
    DatePickerDialog.OnDateSetListener mDateListener;
    EditText edt_description, edt_balance;
    int categoryIcon;
    Double expense_amount;

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
                Intent intent = new Intent(
                        Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                Intent chooser = Intent.createChooser(intent,
                        "Choose a Picture");
                startActivityForResult(chooser,
                        ACTION_REQUEST_GALLERY);
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
                Toast.makeText(AddExpenseActivity.this, "" + catName, Toast.LENGTH_SHORT).show();

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

                    expense_amount = Double.parseDouble(expense);
                    Toast.makeText(AddExpenseActivity.this, "" + currency + "\n" + expense_amount + "\n" + categoryIcon + "\n" + catName, Toast.LENGTH_SHORT).show();
                    ExpenseBean expenseBean = new ExpenseBean(0, currency, expense_amount, categoryIcon, catName, date, description, image_path);
                    Toast.makeText(AddExpenseActivity.this, "Expense Added !" + database.insertExpense(expenseBean), Toast.LENGTH_SHORT).show();
                    onBackPressed();

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            Uri selectedImageUri;
            selectedImageUri = data == null ? null : data.getData();
            img_1.setImageURI(selectedImageUri);

            image_path = selectedImageUri.getPath();
            Toast.makeText(this, "Image Path: " + image_path, Toast.LENGTH_SHORT).show();

        }
    }

}