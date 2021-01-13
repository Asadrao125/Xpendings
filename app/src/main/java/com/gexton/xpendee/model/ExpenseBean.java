package com.gexton.xpendee.model;

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

    public ExpenseBean(int id, String currency, Double expense, int categoryIcon, String categoryName, String currentDay, String description, String imagePath, String colorCode) {
        this.id = id;
        this.currency = currency;
        this.expense = expense;
        this.categoryIcon = categoryIcon;
        this.categoryName = categoryName;
        this.currentDay = currentDay;
        this.description = description;
        this.imagePath = imagePath;
        this.colorCode = colorCode;
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
                '}';
    }
}
