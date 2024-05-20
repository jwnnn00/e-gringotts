package com.example;

import java.time.LocalDate;

public class Transaction {

    private int transactionId;
    private int userId;
    private int tofrom_userID;
    private String transactionType;
    private double amount;
    private double balance;
    private LocalDate transactionDate;
    private String category;

    public Transaction(int transactionId, double amount, LocalDate transactionDate) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public int getUserId() {
        return this.userId;
    }

    public int getTofrom_userID() {
        return this.tofrom_userID;
    }

    public String getTransactionType(){
        return this.transactionType;
    }

    public double getAmount() {
        return this.amount;
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public LocalDate getTransactionDate() {
        return this.transactionDate;
    }

    public String getCategory() {
        return this.category;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "Transaction ID=" + transactionId +
                ", amount=" + amount +
                ", transactionDate=" + transactionDate +
                "}";
    }
}
