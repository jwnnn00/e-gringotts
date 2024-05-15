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
}
