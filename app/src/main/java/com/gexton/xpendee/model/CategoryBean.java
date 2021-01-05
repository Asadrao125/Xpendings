package com.gexton.xpendee.model;

public class CategoryBean {

    public int id;
    public String categoryName ;
    public int categoryIcon;
    public String categoryHashCode;

    public int catFlag;//1=Expense, 2 = Income
    //int isPredefined;//0=predefined, 1 = user added

    public CategoryBean(int id, String categoryName, int categoryIcon, String categoryHashCode, int catFlag) {//, int isPredefined
        this.id = id;
        this.categoryName = categoryName;
        this.categoryIcon = categoryIcon;
        this.categoryHashCode = categoryHashCode;
        this.catFlag = catFlag;
        //this.isPredefined = isPredefined;
    }
}
