package com.example;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transaction {
    private int transactionId;
    private Integer userId;
    private Integer toFromUserID;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balance;
    private LocalDateTime transactionDate;
    private Category category;

    public enum TransactionType {
        Debit, Credit
    }

    public enum Category {
        Food, Grocery, Shopping, Transportation, Entertainment, Utilities, Other
    }
    public Transaction(){
    }

    public Transaction(int transactionId, BigDecimal amount, LocalDateTime transactionDate) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }
    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getToFromUserID() {
        return toFromUserID;
    }

    public void setToFromUserID(Integer tofromUserID) {
        this.toFromUserID = tofromUserID;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
