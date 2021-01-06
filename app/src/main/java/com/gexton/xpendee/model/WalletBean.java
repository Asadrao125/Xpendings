package com.gexton.xpendee.model;

public class WalletBean {
    public Double balance;
    public String wallet_name, currency;

    public WalletBean(Double balance, String wallet_name, String currency) {
        this.balance = balance;
        this.wallet_name = wallet_name;
        this.currency = currency;
    }
}
