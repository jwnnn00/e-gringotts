package com.example.model;

public final class AccountHolder {
    private Account<?> user;
    private final static AccountHolder INSTANCE = new AccountHolder();

    private AccountHolder() {}

    public static AccountHolder getInstance() {
        return INSTANCE;
    }

    public void setUser(Account<?> u) {
        this.user = u;
    }

    public Account<?> getUser() {
        return this.user;
    }
}