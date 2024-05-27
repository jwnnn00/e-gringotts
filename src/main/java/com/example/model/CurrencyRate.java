package com.example.model;

public class CurrencyRate {
    private String currency;
    private String toCurrency;
    private double exchangeRate;
    private double processingFee;

    public CurrencyRate(String currency, String toCurrency, double exchangeRate, double processingFee) {
        this.currency = currency;
        this.toCurrency = toCurrency;
        this.exchangeRate = exchangeRate;
        this.processingFee = processingFee;
    }

    public String getCurrency() {
        return currency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public double getProcessingFee() {
        return processingFee;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setProcessingFee(double processingFee) {
        this.processingFee = processingFee;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
