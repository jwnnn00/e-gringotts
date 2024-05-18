package com.example;

import java.time.LocalDate;
import java.util.Objects;

public class Transaction {
    private Long userId;
    private double amount;
    private double balance;
    private LocalDate date;
    private String category;
    private String paymentMethod;

    public Transaction(Long userId, double amount, LocalDate date, String category, String paymentMethod) {
        this.userId = userId;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.paymentMethod = paymentMethod;
    }

    public Long getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(that.amount, amount) == 0 &&
                userId.equals(that.userId) &&
                date.equals(that.date) &&
                category.equals(that.category) &&
                paymentMethod.equals(that.paymentMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, amount, date, category, paymentMethod);

    }
    public double getBalance(){
        return balance;
    }

    public void setBalance(double balance){
        this.balance=balance;
    }
}
