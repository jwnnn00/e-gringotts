package com.example.model;

public class CurrencyRate {
    private String currency;
    private double toKnut;
    private double toSickle;
    private double toGalleon;

    public CurrencyRate(String currency, double toKnut, double toSickle, double toGalleon) {
        this.currency = currency;
        this.toKnut = toKnut;
        this.toSickle = toSickle;
        this.toGalleon = toGalleon;
    }

    public String getCurrency() {
        return currency;
    }

    public double getToKnut() {
        return toKnut;
    }

    public double getToSickle() {
        return toSickle;
    }

    public double getToGalleon() {
        return toGalleon;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setToGalleon(double toGalleon) {
        this.toGalleon = toGalleon;
    }

    public void setToKnut(double toKnut) {
        this.toKnut = toKnut;
    }

    public void setToSickle(double toSickle) {
        this.toSickle = toSickle;
    }
}
