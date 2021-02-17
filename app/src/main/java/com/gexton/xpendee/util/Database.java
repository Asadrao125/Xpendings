package com.gexton.xpendee.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;


import com.gexton.xpendee.model.CategoryBean;
import com.gexton.xpendee.model.ExpenseBean;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Database {

    String DB_PATH = "data/data/com.gexton.xpendee/databases/";
    String DB_NAME = "spendee_app_db.sqlite";
    Context activity;
    SQLiteDatabase sqLiteDatabase;

    public Database(Context activity) {
        this.activity = activity;
    }

    public void createDatabase() {
        boolean dBExist = false;

        try {
            dBExist = checkDatabase();
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (dBExist) {

        } else {
            try {

                sqLiteDatabase = activity.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
                sqLiteDatabase.close();
                copyDatabaseTable();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
    }

    private void copyDatabaseTable() throws IOException {

        //open your local database as input stream
        InputStream myInput = activity.getAssets().open(DB_NAME);

        //path to the created empty database
        String outFileName = DB_PATH + DB_NAME;

        //open the empty database as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    private boolean checkDatabase() {

        SQLiteDatabase checkDB = null;
        String myPath = DB_PATH + DB_NAME;
        try {
            try {
                checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            //no database exists...
        }


        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    public void open() {
        sqLiteDatabase = activity.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
    }

    public void close() {
        sqLiteDatabase.close();
    }

    //============================start custom methods / Crud for category table ====================================

    /*
    * CREATE TABLE "category" (
	"id"	INTEGER,
	"category_name"	TEXT,
	"color_code"	TEXT,
	"icon_id"	INTEGER,
	"flag"	INTEGER,
	"visiblity"	INTEGER,
	PRIMARY KEY("id" AUTOINCREMENT));
    */

    public long insertCategory(CategoryBean categoryBean) {
        long rowId = -1;
        try {
            open();
            ContentValues cv = new ContentValues();
            cv.put("category_name", categoryBean.categoryName);
            cv.put("color_code", categoryBean.categoryHashCode);
            cv.put("icon_id", categoryBean.categoryIcon);
            cv.put("flag", categoryBean.catFlag);
            cv.put("visiblity", categoryBean.visiblity);

            rowId = sqLiteDatabase.insert("category", null, cv);

            close();
        } catch (SQLiteException e) {
            Toast.makeText(activity, "Database exception", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        System.out.println("-- Record inserted rowId : " + rowId);
        return rowId;
    }//end insertCategory

    public ArrayList<CategoryBean> getAllCategories(int flag_value) {
        open();
        ArrayList<CategoryBean> categoryBeans = new ArrayList<>();
        CategoryBean temp;
        String query1 = "select * from category WHERE flag = '" + flag_value + "'";

        System.out.println("--query in getAllAttendance : " + query1);
        Cursor cursor = sqLiteDatabase.rawQuery(query1, null);

        if (cursor.moveToFirst()) {
            do {

                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String catName = cursor.getString(cursor.getColumnIndex("category_name"));
                String colorCode = cursor.getString(cursor.getColumnIndex("color_code"));
                int iconId = cursor.getInt(cursor.getColumnIndex("icon_id"));
                int flag = cursor.getInt(cursor.getColumnIndex("flag"));
                int visiblity = cursor.getInt(cursor.getColumnIndex("visiblity"));

                temp = new CategoryBean(id, catName, iconId, colorCode, flag, visiblity);

                categoryBeans.add(temp);
                temp = null;
            }
            while (cursor.moveToNext());
            close();
            return categoryBeans;
        }
        close();
        return null;
    }//======end getAllCategories()===========

    public void deleteCategory(int id) {
        open();
        String query = "delete from category WHERE id = '" + id + "'";
        sqLiteDatabase.execSQL(query);
        close();

    }//deleteCategory

    public void updateCategory(CategoryBean categoryBean, int id) {
        open();
        ContentValues dataToUpdate = new ContentValues();
        dataToUpdate.put("category_name", categoryBean.categoryName);
        dataToUpdate.put("color_code", categoryBean.categoryHashCode);
        dataToUpdate.put("icon_id", categoryBean.categoryIcon);
        dataToUpdate.put("flag", categoryBean.catFlag);
        dataToUpdate.put("visiblity", categoryBean.visiblity);

        //String where = "id" + "=" + "'"+categoryBean.id+"'" + " AND dateof" + "=" + "'"+dateof+"'" + " AND type" + "=" + "'checking'"; // id is string
        String where = "id" + "=" + "'" + id + "'"; //One WHERE clause

        try {
            int rows = sqLiteDatabase.update("category", dataToUpdate, where, null);
            System.out.println("-- rows updated: " + rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
        close();
    }//end updateCategory

    public ArrayList<CategoryBean> getAllCategoriesVisiblity(int flag_value, int visiblityyyy) {
        open();
        ArrayList<CategoryBean> categoryBeans = new ArrayList<>();
        CategoryBean temp;
        String query1 = "select * from category WHERE flag = '" + flag_value + "' AND visiblity = '" + visiblityyyy + "'";

        System.out.println("--query in getAllAttendance : " + query1);
        Cursor cursor = sqLiteDatabase.rawQuery(query1, null);

        if (cursor.moveToFirst()) {
            do {

                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String catName = cursor.getString(cursor.getColumnIndex("category_name"));
                String colorCode = cursor.getString(cursor.getColumnIndex("color_code"));
                int iconId = cursor.getInt(cursor.getColumnIndex("icon_id"));
                int flag = cursor.getInt(cursor.getColumnIndex("flag"));
                int visiblity = cursor.getInt(cursor.getColumnIndex("visiblity"));

                temp = new CategoryBean(id, catName, iconId, colorCode, flag, visiblity);

                categoryBeans.add(temp);
                temp = null;
            }
            while (cursor.moveToNext());
            close();
            return categoryBeans;
        }
        close();
        return null;
    }//======end getAllCategoriesVisiblity()===========

    public ArrayList<CategoryBean> getAllVisibleCategories(int visiblityyyy) {
        open();
        ArrayList<CategoryBean> categoryBeans = new ArrayList<>();
        CategoryBean temp;
        String query1 = "select * from category WHERE visiblity = '" + visiblityyyy + "'";

        System.out.println("--query in getAllAttendance : " + query1);
        Cursor cursor = sqLiteDatabase.rawQuery(query1, null);

        if (cursor.moveToFirst()) {
            do {

                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String catName = cursor.getString(cursor.getColumnIndex("category_name"));
                String colorCode = cursor.getString(cursor.getColumnIndex("color_code"));
                int iconId = cursor.getInt(cursor.getColumnIndex("icon_id"));
                int flag = cursor.getInt(cursor.getColumnIndex("flag"));
                int visiblity = cursor.getInt(cursor.getColumnIndex("visiblity"));

                temp = new CategoryBean(id, catName, iconId, colorCode, flag, visiblity);

                categoryBeans.add(temp);
                temp = null;
            }
            while (cursor.moveToNext());
            close();
            return categoryBeans;
        }
        close();
        return null;
    }//======end getAllCategoriesVisiblity()===========


    //============================start custom methods / Crud for Expense table ====================================

    /*  CREATE TABLE "expense"
    ("id"	INTEGER,
	"expense_currency"	TEXT,
	"expense_amount"	REAL,
	"expense_category_icon"	INTEGER,
	"expense_category_name"	TEXT,
	"current_day"	TEXT,
	"expense_description"	TEXT,
	"image_path"	TEXT,
	"color_code"	TEXT,
	"flag"	INTEGER,
	PRIMARY KEY("id" AUTOINCREMENT)); */

    public long insertExpense(ExpenseBean expenseBean) {
        long rowId = -1;
        try {
            open();
            ContentValues cv = new ContentValues();
            cv.put("expense_currency", expenseBean.currency);
            cv.put("expense_amount", expenseBean.expense);
            cv.put("expense_category_icon", expenseBean.categoryIcon);
            cv.put("expense_category_name", expenseBean.categoryName);
            cv.put("current_day", expenseBean.currentDay);
            cv.put("expense_description", expenseBean.description);
            cv.put("image_path", expenseBean.imagePath);
            cv.put("color_code", expenseBean.colorCode);
            cv.put("flag", expenseBean.flag);

            rowId = sqLiteDatabase.insert("expense", null, cv);

            close();

        } catch (SQLiteException e) {
            Toast.makeText(activity, "Database exception", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        System.out.println("-- Record inserted rowId : " + rowId);

        return rowId;
    }//insertExpense

    public ArrayList<ExpenseBean> getAllExpenses() {
        open();
        ArrayList<ExpenseBean> expenseBean = new ArrayList<>();
        ExpenseBean temp;
        //String query1 = "select * from expense  Order By current_day DESC";
        String query1 = "select * from expense  Order By current_day ASC";

        System.out.println("--query in getAllAttendance : " + query1);
        Cursor cursor = sqLiteDatabase.rawQuery(query1, null);

        if (cursor.moveToFirst()) {
            do {

                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String currency = cursor.getString(cursor.getColumnIndex("expense_currency"));
                Double amount = cursor.getDouble(cursor.getColumnIndex("expense_amount"));
                int icon = cursor.getInt(cursor.getColumnIndex("expense_category_icon"));
                String category_name = cursor.getString(cursor.getColumnIndex("expense_category_name"));
                String current_day = cursor.getString(cursor.getColumnIndex("current_day"));
                String expense_description = cursor.getString(cursor.getColumnIndex("expense_description"));
                String image_path = cursor.getString(cursor.getColumnIndex("image_path"));
                String color_code = cursor.getString(cursor.getColumnIndex("color_code"));
                int flag = cursor.getInt(cursor.getColumnIndex("flag"));

                temp = new ExpenseBean(id, currency, amount, icon, category_name, current_day, expense_description, image_path, color_code, flag);

                expenseBean.add(temp);
                temp = null;
            }
            while (cursor.moveToNext());
            close();
            return expenseBean;
        }
        close();
        return null;
    }//======end getAllExpenses()===========

    public ArrayList<String> getAllExpensesDates() {
        open();
        ArrayList<String> list = new ArrayList<>();
        String temp;
        String query1 = "select current_day from expense";

        System.out.println("--query in getAllAttendance : " + query1);
        Cursor cursor = sqLiteDatabase.rawQuery(query1, null);

        if (cursor.moveToFirst()) {
            do {
                String current_day = cursor.getString(cursor.getColumnIndex("current_day"));
                temp = current_day;
                list.add(temp);
                temp = null;
            }
            while (cursor.moveToNext());
            close();
            return list;
        }
        close();
        return null;
    }//======end getAllExpensesDates()===========

    public ArrayList<String> getAllExpenseCategories() {
        open();
        ArrayList<String> list = new ArrayList<>();
        String temp;
        String query1 = "select expense_category_name from expense";

        System.out.println("--query in getAllExpenseCategories : " + query1);
        Cursor cursor = sqLiteDatabase.rawQuery(query1, null);

        if (cursor.moveToFirst()) {
            do {
                String current_day = cursor.getString(cursor.getColumnIndex("expense_category_name"));
                temp = current_day;
                list.add(temp);
                temp = null;
            }
            while (cursor.moveToNext());
            close();
            return list;
        }
        close();
        return null;
    }//======end getAllExpenseCategories()===========

    public ArrayList<Double> getExpenses() {
        open();
        ArrayList<Double> list = new ArrayList<>();
        Double temp;
        String query1 = "select expense_amount from expense";

        System.out.println("--query in getAllExpenseCategories : " + query1);
        Cursor cursor = sqLiteDatabase.rawQuery(query1, null);

        if (cursor.moveToFirst()) {
            do {
                Double expense = cursor.getDouble(cursor.getColumnIndex("expense_amount"));
                temp = expense;
                list.add(temp);
                temp = null;
            }
            while (cursor.moveToNext());
            close();
            return list;
        }
        close();
        return null;
    }//======end getExpenses()===========

    public ArrayList<ExpenseBean> getExpenseByName(String catName) {
        open();
        ArrayList<ExpenseBean> expenseBean = new ArrayList<>();
        ExpenseBean temp;
        String query1 = "select * from expense WHERE expense_category_name = '" + catName + "'";

        System.out.println("--query in getAllAttendance : " + query1);
        Cursor cursor = sqLiteDatabase.rawQuery(query1, null);

        if (cursor.moveToFirst()) {
            do {

                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String currency = cursor.getString(cursor.getColumnIndex("expense_currency"));
                Double amount = cursor.getDouble(cursor.getColumnIndex("expense_amount"));
                int icon = cursor.getInt(cursor.getColumnIndex("expense_category_icon"));
                String category_name = cursor.getString(cursor.getColumnIndex("expense_category_name"));
                String current_day = cursor.getString(cursor.getColumnIndex("current_day"));
                String expense_description = cursor.getString(cursor.getColumnIndex("expense_description"));
                String image_path = cursor.getString(cursor.getColumnIndex("image_path"));
                String color_code = cursor.getString(cursor.getColumnIndex("color_code"));
                int flag = cursor.getInt(cursor.getColumnIndex("flag"));

                temp = new ExpenseBean(id, currency, amount, icon, category_name, current_day, expense_description, image_path, color_code, flag);

                expenseBean.add(temp);
                temp = null;
            }
            while (cursor.moveToNext());
            close();
            return expenseBean;
        }
        close();
        return null;

    }//======end getExpenseByName()===========

    public void updateExpense(ExpenseBean expenseBean, int id) {
        open();
        ContentValues dataToUpdate = new ContentValues();
        dataToUpdate.put("expense_category_name", expenseBean.categoryName);
        dataToUpdate.put("color_code", expenseBean.colorCode);
        dataToUpdate.put("expense_category_icon", expenseBean.categoryIcon);
        dataToUpdate.put("flag", expenseBean.flag);
        dataToUpdate.put("image_path", expenseBean.imagePath);
        dataToUpdate.put("expense_description", expenseBean.description);
        dataToUpdate.put("current_day", expenseBean.currentDay);
        dataToUpdate.put("expense_amount", expenseBean.expense);
        dataToUpdate.put("expense_currency", expenseBean.currency);

        //String where = "id" + "=" + "'"+categoryBean.id+"'" + " AND dateof" + "=" + "'"+dateof+"'" + " AND type" + "=" + "'checking'"; // id is string
        String where = "id" + "=" + "'" + id + "'"; //One WHERE clause

        try {
            int rows = sqLiteDatabase.update("expense", dataToUpdate, where, null);
            System.out.println("-- rows updated: " + rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
        close();
    }//end updateExpense

    public void deleteExpense(int id) {
        open();
        String query = "delete from expense WHERE id = '" + id + "'";
        sqLiteDatabase.execSQL(query);
        close();

    }//deleteExpense

    public ArrayList<ExpenseBean> getAllExpensesFlag(/*int flag_value*/) {
        open();
        ArrayList<ExpenseBean> expenseBean = new ArrayList<>();
        ExpenseBean temp;
        //String query1 = "select * from expense WHERE flag = '" + flag_value + "'";
        String query1 = "select * from expense";

        System.out.println("--query in getAllExpensesFlag : " + query1);
        Cursor cursor = sqLiteDatabase.rawQuery(query1, null);

        if (cursor.moveToFirst()) {
            do {

                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String currency = cursor.getString(cursor.getColumnIndex("expense_currency"));
                Double amount = cursor.getDouble(cursor.getColumnIndex("expense_amount"));
                int icon = cursor.getInt(cursor.getColumnIndex("expense_category_icon"));
                String category_name = cursor.getString(cursor.getColumnIndex("expense_category_name"));
                String current_day = cursor.getString(cursor.getColumnIndex("current_day"));
                String expense_description = cursor.getString(cursor.getColumnIndex("expense_description"));
                String image_path = cursor.getString(cursor.getColumnIndex("image_path"));
                String color_code = cursor.getString(cursor.getColumnIndex("color_code"));
                int flag = cursor.getInt(cursor.getColumnIndex("flag"));

                temp = new ExpenseBean(id, currency, amount, icon, category_name, current_day, expense_description, image_path, color_code, flag);

                expenseBean.add(temp);
                temp = null;
            }
            while (cursor.moveToNext());
            close();
            return expenseBean;
        }
        close();
        return null;
    }//======end getAllExpensesFlag()===========

    public ArrayList<ExpenseBean> getAllIncome(int flag_value) {
        open();
        ArrayList<ExpenseBean> expenseBean = new ArrayList<>();
        ExpenseBean temp;
        String query1 = "select * from expense WHERE flag = '" + flag_value + "'";
        //String query1 = "select * from expense";

        System.out.println("--query in getAllExpensesFlag : " + query1);
        Cursor cursor = sqLiteDatabase.rawQuery(query1, null);

        if (cursor.moveToFirst()) {
            do {

                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String currency = cursor.getString(cursor.getColumnIndex("expense_currency"));
                Double amount = cursor.getDouble(cursor.getColumnIndex("expense_amount"));
                int icon = cursor.getInt(cursor.getColumnIndex("expense_category_icon"));
                String category_name = cursor.getString(cursor.getColumnIndex("expense_category_name"));
                String current_day = cursor.getString(cursor.getColumnIndex("current_day"));
                String expense_description = cursor.getString(cursor.getColumnIndex("expense_description"));
                String image_path = cursor.getString(cursor.getColumnIndex("image_path"));
                String color_code = cursor.getString(cursor.getColumnIndex("color_code"));
                int flag = cursor.getInt(cursor.getColumnIndex("flag"));

                temp = new ExpenseBean(id, currency, amount, icon, category_name, current_day, expense_description, image_path, color_code, flag);

                expenseBean.add(temp);
                temp = null;
            }
            while (cursor.moveToNext());
            close();
            return expenseBean;
        }
        close();
        return null;
    }//======end getAllIncome()===========

    public ArrayList<ExpenseBean> getAllDailyExpenses(String date) {
        open();
        ArrayList<ExpenseBean> expenseBean = new ArrayList<>();
        ExpenseBean temp;
        String query1 = "select * from expense WHERE current_day = '" + date + "'";

        System.out.println("--query in getAllDailyExpenses : " + query1);
        Cursor cursor = sqLiteDatabase.rawQuery(query1, null);

        if (cursor.moveToFirst()) {
            do {

                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String currency = cursor.getString(cursor.getColumnIndex("expense_currency"));
                Double amount = cursor.getDouble(cursor.getColumnIndex("expense_amount"));
                int icon = cursor.getInt(cursor.getColumnIndex("expense_category_icon"));
                String category_name = cursor.getString(cursor.getColumnIndex("expense_category_name"));
                String current_day = cursor.getString(cursor.getColumnIndex("current_day"));
                String expense_description = cursor.getString(cursor.getColumnIndex("expense_description"));
                String image_path = cursor.getString(cursor.getColumnIndex("image_path"));
                String color_code = cursor.getString(cursor.getColumnIndex("color_code"));
                int flag = cursor.getInt(cursor.getColumnIndex("flag"));

                temp = new ExpenseBean(id, currency, amount, icon, category_name, current_day, expense_description, image_path, color_code, flag);

                expenseBean.add(temp);
                temp = null;
            }
            while (cursor.moveToNext());
            close();
            return expenseBean;
        }
        close();
        return null;
    }//======end getAllDailyExpenses()===========

}