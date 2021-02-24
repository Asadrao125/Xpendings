package com.gexton.xpendings.model;

public class ExpenseBean {
    public int id;
    public String currency;
    public Double expense;
    public int categoryIcon;
    public String categoryName;
    public String currentDay;
    public String description;
    public String imagePath;
    public String colorCode;
    public int flag; //1 for expense , 2 for income

    public ExpenseBean(int id, String currency, Double expense, int categoryIcon, String categoryName, String currentDay, String description, String imagePath, String colorCode, int flag) {
        this.id = id;
        this.currency = currency;
        this.expense = expense;
        this.categoryIcon = categoryIcon;
        this.categoryName = categoryName;
        this.currentDay = currentDay;
        this.description = description;
        this.imagePath = imagePath;
        this.colorCode = colorCode;
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "ExpenseBean{" +
                "id=" + id +
                ", currency='" + currency + '\'' +
                ", expense=" + expense +
                ", categoryIcon=" + categoryIcon +
                ", categoryName='" + categoryName + '\'' +
                ", currentDay='" + currentDay + '\'' +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", colorCode='" + colorCode + '\'' +
                ", flag=" + flag +
                '}';
    }
}