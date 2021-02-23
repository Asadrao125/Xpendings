package com.gexton.xpendee;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.ContentUriUtils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.gexton.xpendee.adapters.CategoriesAdapterForExpense;
import com.gexton.xpendee.model.CategoryBean;
import com.gexton.xpendee.model.ExpenseBean;
import com.gexton.xpendee.model.WalletBean;
import com.gexton.xpendee.util.Database;
import com.gexton.xpendee.util.RecyclerItemClickListener;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {
    int flag = 0;
    int categoryIcon;
    Database database;
    View transpareView;
    Calendar myCalendar;
    Double expense_amount;
    ViewFlipper vfCategories;
    TextView tvExpense, tvIncome;
    ImageView imageview_Category;
    ImageView imgTick, imgSetting;
    SharedPreferences prefs1, prefs;
    LinearLayout parentLayoutCategories;
    final int CUSTOM_REQUEST_CODE = 987;
    public ImageView img_back, img_camera;
    EditText edt_description, edt_balance;
    TextView tv_add_category, tv_expense;
    CategoriesAdapterForExpense adapter = null;
    String MY_PREFS_NAME = "MY_PREFS_NAME", expense;
    CategoriesAdapterForExpense adapterIncome = null;
    ArrayList<CategoryBean> categoryBeanArrayListIncome;
    ArrayList<CategoryBean> categoryBeanArrayListExpense;
    RecyclerView rvCategoriesExpense, rvCategoriesIncome;
    RelativeLayout layout_complete, galery_images_layout;
    private ArrayList<Uri> photoPaths = new ArrayList<>();
    public static ImageView img_1, img_2, img_3, img_4, img_5, img_6;
    ImageView img_calendar, img_title_add_expense, img_select_imagee;
    RelativeLayout current_day_layout, select_image_layout, no_data_layout, enable_storage_layout;
    @SuppressLint("StaticFieldLeak")
    TextView tv_current_day, tv_date, tv_save, tv_reset, tv_categories, tv_details;
    public static String img_path1, img_path2, img_path3, img_path4, img_path5, img_path6;
    String image_path, current_date, description, currency = "PKR", catName, color_code, user_selected_date, colorHex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.navy_blue, this.getTheme()));
        }

        initialise();

        String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        user_selected_date = formattedDate;

        ClickListeners();

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        tv_current_day.setText(dayOfTheWeek);

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
                new DatePickerDialog(AddExpenseActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //Getting current date
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        current_date = df.format(c);//current date end

        // Income Recycler View
        RecyclerView.LayoutManager mLayoutManagerRVBP = new GridLayoutManager(getApplicationContext(), 3);
        rvCategoriesIncome.setLayoutManager(mLayoutManagerRVBP);

        if (database.getAllCategoriesVisiblity(2, 1) != null) {
            categoryBeanArrayListIncome = database.getAllCategoriesVisiblity(2, 1);
            adapterIncome = new CategoriesAdapterForExpense(this, categoryBeanArrayListIncome);
            rvCategoriesIncome.setAdapter(adapterIncome);
        }

        rvCategoriesIncome.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                rvCategoriesIncome, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (database.getAllCategoriesVisiblity(2, 1) != null) {
                    categoryBeanArrayListIncome = database.getAllCategoriesVisiblity(2, 1);
                    catName = categoryBeanArrayListIncome.get(position).categoryName;
                    categoryIcon = categoryBeanArrayListIncome.get(position).categoryIcon;
                    color_code = categoryBeanArrayListIncome.get(position).categoryHashCode;
                    colorHex = color_code;
                    transpareView.setVisibility(View.GONE);
                    flag = categoryBeanArrayListIncome.get(position).catFlag;
                    parentLayoutCategories.setVisibility(View.GONE);
                    imageview_Category.setImageResource(categoryIcon);
                    imageview_Category.setColorFilter(Color.parseColor(colorHex), android.graphics.PorterDuff.Mode.MULTIPLY);
                    layout_complete.setBackgroundColor(Color.parseColor(colorHex));
                    img_calendar.setColorFilter(Color.parseColor(colorHex), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_title_add_expense.setColorFilter(Color.parseColor(colorHex), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_select_imagee.setColorFilter(Color.parseColor(colorHex), android.graphics.PorterDuff.Mode.MULTIPLY);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getWindow().setStatusBarColor(Color.parseColor(colorHex));
                    }
                    tv_add_category.setText("Add " + categoryBeanArrayListIncome.get(position).categoryName);
                    tv_expense.setText("Add " + categoryBeanArrayListIncome.get(position).categoryName);
                }
                adapterIncome.selectedPos = position;
                adapterIncome.notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                System.out.println("-- item long press pos: " + position);
            }
        }));

        // Expense Recycler View
        RecyclerView.LayoutManager mLayoutManagerRVBP2 = new GridLayoutManager(getApplicationContext(), 3);
        rvCategoriesExpense.setLayoutManager(mLayoutManagerRVBP2);

        if (database.getAllCategoriesVisiblity(1, 1) != null) {
            categoryBeanArrayListExpense = database.getAllCategoriesVisiblity(1, 1);
            adapter = new CategoriesAdapterForExpense(this, categoryBeanArrayListExpense);
            rvCategoriesExpense.setAdapter(adapter);
        }

        rvCategoriesExpense.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                rvCategoriesExpense, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                categoryBeanArrayListExpense = database.getAllCategoriesVisiblity(1, 1);
                if (database.getAllCategoriesVisiblity(1, 1) != null) {
                    catName = categoryBeanArrayListExpense.get(position).categoryName;
                    categoryIcon = categoryBeanArrayListExpense.get(position).categoryIcon;
                    color_code = categoryBeanArrayListExpense.get(position).categoryHashCode;
                    colorHex = color_code;
                    transpareView.setVisibility(View.GONE);
                    flag = categoryBeanArrayListExpense.get(position).catFlag;
                    parentLayoutCategories.setVisibility(View.GONE);
                    layout_complete.setBackgroundColor(Color.parseColor(colorHex));
                    imageview_Category.setImageResource(categoryIcon);
                    imageview_Category.setColorFilter(Color.parseColor(colorHex), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_calendar.setColorFilter(Color.parseColor(colorHex), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_title_add_expense.setColorFilter(Color.parseColor(colorHex), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_select_imagee.setColorFilter(Color.parseColor(colorHex), android.graphics.PorterDuff.Mode.MULTIPLY);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getWindow().setStatusBarColor(Color.parseColor(colorHex));
                    }

                    tv_add_category.setText("Add " + categoryBeanArrayListExpense.get(position).categoryName);
                    tv_expense.setText("Add " + categoryBeanArrayListExpense.get(position).categoryName);

                }
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

                expense = edt_balance.getText().toString().trim();
                description = edt_description.getText().toString().trim();

                if (TextUtils.isEmpty(expense)) {
                    Toast.makeText(AddExpenseActivity.this, "Please enter expence amount", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(currency)) {
                    Toast.makeText(AddExpenseActivity.this, "Please enter currency", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(catName)) {
                    Toast.makeText(AddExpenseActivity.this, "Please select category", Toast.LENGTH_SHORT).show();
                } else if (categoryIcon == 0) {
                    Toast.makeText(AddExpenseActivity.this, "Please select category", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(user_selected_date)) {
                    Toast.makeText(AddExpenseActivity.this, "Please enter date", Toast.LENGTH_SHORT).show();
                } else {
                    if (flag == 1) {
                        prefs1 = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                        if (!prefs1.getString("Wallet_Bean", "").equals("")) {
                            String json = prefs1.getString("Wallet_Bean", "");
                            Gson gson = new Gson();
                            WalletBean walletBean = gson.fromJson(json, WalletBean.class);

                            expense_amount = Double.parseDouble(expense);

                            Double balance = walletBean.balance;
                            String currency = walletBean.currency;
                            String walletName = walletBean.wallet_name;

                            Double newBalance = balance - expense_amount;
                            WalletBean newWalletBean = new WalletBean(newBalance, walletName, currency);

                            Gson newGson = new Gson();
                            String newJson = newGson.toJson(newWalletBean);

                            SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                            editor.putString("Wallet_Bean", newJson);
                            editor.apply();

                            ExpenseBean expenseBean = new ExpenseBean(0, currency, expense_amount, categoryIcon,
                                    catName, user_selected_date, description, image_path, colorHex, 1);
                            database.insertExpense(expenseBean);

                            onBackPressed();
                        }
                    }

                    if (flag == 2) {

                        prefs = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

                        String json = prefs.getString("Wallet_Bean", "");
                        Gson gson = new Gson();
                        WalletBean walletBean = gson.fromJson(json, WalletBean.class);

                        Double balance = walletBean.balance;
                        String currency = walletBean.currency;
                        String walletName = walletBean.wallet_name;

                        expense_amount = Double.parseDouble(expense);
                        Double newBalance = balance + expense_amount;

                        WalletBean newWalletBean = new WalletBean(newBalance, walletName, currency);
                        Gson newGson = new Gson();
                        String newJson = newGson.toJson(newWalletBean);

                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("Wallet_Bean", newJson);
                        editor.apply();

                        ExpenseBean expenseBean = new ExpenseBean(0, currency, expense_amount, categoryIcon,
                                catName, user_selected_date, description, image_path, colorHex, 2);
                        database.insertExpense(expenseBean);

                        onBackPressed();
                    }

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

        boolean isPermissionGranted = ActivityCompat.checkSelfPermission(AddExpenseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        if (isPermissionGranted) {
            fetchGalleryImages(AddExpenseActivity.this);
            select_image_layout.setVisibility(View.VISIBLE);
            galery_images_layout.setVisibility(View.VISIBLE);
            //enable_storage_layout.setVisibility(View.GONE);
        } else {
            select_image_layout.setVisibility(View.GONE);
            galery_images_layout.setVisibility(View.GONE);
            //Toast.makeText(this, "Enable Storage Permission To Use Images", Toast.LENGTH_SHORT).show();
            //enable_storage_layout.setVisibility(View.VISIBLE);
            permissionCheck();
        }

    }

    private void initialise() {
        database = new Database(AddExpenseActivity.this);
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
        img_back = findViewById(R.id.img_back);
        tv_categories = findViewById(R.id.tv_categories);
        no_data_layout = findViewById(R.id.no_data_layout);
        tv_details = findViewById(R.id.tv_details);
        myCalendar = Calendar.getInstance();
        img_camera = findViewById(R.id.img_camera);
        database = new Database(getApplicationContext());
        vfCategories = findViewById(R.id.vfCategories);
        tvExpense = findViewById(R.id.tvExpense);
        tvIncome = findViewById(R.id.tvIncome);
        rvCategoriesExpense = findViewById(R.id.rvCategoriesExpense);
        rvCategoriesIncome = findViewById(R.id.rvCategoriesIncome);
        imgTick = findViewById(R.id.imgTick);
        imgSetting = findViewById(R.id.imgSetting);
        parentLayoutCategories = findViewById(R.id.parentLayoutCategories);
        transpareView = findViewById(R.id.transpareView);
        tv_add_category = findViewById(R.id.tv_add_category);
        tv_expense = findViewById(R.id.tv_expense);
        categoryBeanArrayListIncome = new ArrayList<>();
        categoryBeanArrayListExpense = new ArrayList<>();
        imageview_Category = findViewById(R.id.imageview_Category);
        layout_complete = findViewById(R.id.layout_complete);
        img_calendar = findViewById(R.id.img_calendar);
        img_title_add_expense = findViewById(R.id.img_title_add_expense);
        img_select_imagee = findViewById(R.id.img_select_imagee);
        galery_images_layout = findViewById(R.id.galery_images_layout);
        enable_storage_layout = findViewById(R.id.enable_storage_layout);
    }

    private void ClickListeners() {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        img_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPhoto();
            }
        });

        select_image_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPhoto();
            }
        });

        tv_current_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
                tv_date.setText(formattedDate);
                user_selected_date = formattedDate;
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

        tvExpense.setTextColor(Color.BLACK);

        tvExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vfCategories.setDisplayedChild(0);
                tvExpense.setTextColor(Color.BLACK);
                tvIncome.setTextColor(Color.LTGRAY);
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvExpense.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.curve_for_transaction_category));
                } else {
                    tvExpense.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.curve_for_transaction_category));
                }
                tvIncome.setBackgroundResource(0);
                tv_add_category.setText("Add Expense");
                tv_expense.setText("Add Expense");
            }
        });

        tvIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vfCategories.setDisplayedChild(1);
                tvIncome.setTextColor(Color.BLACK);
                tvExpense.setTextColor(Color.LTGRAY);
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvIncome.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.curve_for_transaction_category));
                } else {
                    tvIncome.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.curve_for_transaction_category));
                }
                tvExpense.setBackgroundResource(0);
                tv_add_category.setText("Add Income");
                tv_expense.setText("Add Income");
            }
        });

        imgTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentLayoutCategories.setVisibility(View.GONE);
                transpareView.setVisibility(View.GONE);
            }
        });

        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentLayoutCategories.setVisibility(View.GONE);
                transpareView.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(), VisiblistyActivityForCategories.class));
            }
        });

        transpareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (parentLayoutCategories.isShown()) {
                    parentLayoutCategories.setVisibility(View.GONE);
                    transpareView.setVisibility(View.GONE);
                }*/
            }
        });

        /*parentLayoutCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentLayoutCategories.setVisibility(View.GONE);
                transpareView.setVisibility(View.GONE);
            }
        });*/

        imageview_Category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentLayoutCategories.setVisibility(View.VISIBLE);
                transpareView.setVisibility(View.VISIBLE);
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
                /*.setActivityTheme(R.style.FilePickerTheme)*/
                .setActivityTheme(R.style.ThemeOverlay_AppCompat_Dark)
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

        Log.e("fatch in", "images");
        return galleryImageUrls;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Income
        if (database.getAllCategoriesVisiblity(2, 1) != null) {
            categoryBeanArrayListIncome = database.getAllCategoriesVisiblity(2, 1);
            adapterIncome = new CategoriesAdapterForExpense(this, categoryBeanArrayListIncome);
            rvCategoriesIncome.setAdapter(adapterIncome);
        }

        // Expense
        if (database.getAllCategoriesVisiblity(1, 1) != null) {
            categoryBeanArrayListExpense = database.getAllCategoriesVisiblity(1, 1);
            adapter = new CategoriesAdapterForExpense(this, categoryBeanArrayListExpense);
            rvCategoriesExpense.setAdapter(adapter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        permissionCheck();
    }

    public void permissionCheck() {

        boolean shouldShowRequestPersmissionRational = ActivityCompat.shouldShowRequestPermissionRationale(AddExpenseActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        System.out.println("-- shouldShowRequestPersmissionRational: " + shouldShowRequestPersmissionRational);

        boolean isPermissionGranted = ActivityCompat.checkSelfPermission(AddExpenseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        System.out.println("-- isPermissionGranted: " + isPermissionGranted);

        Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Log.d("TAG", "onPermissionsChecked: Permission Granted");
                            fetchGalleryImages(AddExpenseActivity.this);
                            select_image_layout.setVisibility(View.VISIBLE);
                            galery_images_layout.setVisibility(View.VISIBLE);
                            //enable_storage_layout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

}