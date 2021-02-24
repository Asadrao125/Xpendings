package com.gexton.xpendings.model;

import java.util.ArrayList;

public class CategoryBean {

    public int id;
    public String categoryName;
    public int categoryIcon;
    public String categoryHashCode;
    public int catFlag;//1=Expense, 2 = Income
    public int visiblity; //1 = Visible, 0 = Invisible, 2 = Nothing
    //int isPredefined;//0=predefined, 1 = user added

    public ArrayList<ExpenseBean> listExpenseBean;

    public CategoryBean(int id, String categoryName, int categoryIcon, String categoryHashCode, int catFlag, int visiblisty) {//, int isPredefined
        this.id = id;
        this.categoryName = categoryName;
        this.categoryIcon = categoryIcon;
        this.categoryHashCode = categoryHashCode;
        this.catFlag = catFlag;
        this.visiblity = visiblisty;
        //this.isPredefined = isPredefined;
    }

    @Override
    public String toString() {
        return "CategoryBean{" +
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", categoryIcon=" + categoryIcon +
                ", categoryHashCode='" + categoryHashCode + '\'' +
                ", catFlag=" + catFlag +
                ", visiblity=" + visiblity +
                ", listExpenseBean=" + listExpenseBean +
                '}';
    }
}