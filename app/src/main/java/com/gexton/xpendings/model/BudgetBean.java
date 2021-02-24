package com.gexton.xpendings.model;

public class BudgetBean {
    public Double amount;
    public String budgetName, currency;
    public String recurrance;

    public BudgetBean(Double amount, String budgetName, String currency, String recurrance) {
        this.amount = amount;
        this.budgetName = budgetName;
        this.currency = currency;
        this.recurrance = recurrance;
    }
}
