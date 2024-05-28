package com.example;

import java.util.*;

public class CurrencyConverter {
    private Map<String, Map<String, Double>> graph;
    private Map<String, Double> processingFees;

    public CurrencyConverter(List<String[]> currencies) {
        graph = new HashMap<>();
        processingFees = new HashMap<>();
        for (String[] currency : currencies) {
            String currency1 = currency[0];
            String currency2 = currency[1];
            double rate = Double.parseDouble(currency[2]);
            double fee = Double.parseDouble(currency[3]);

            graph.putIfAbsent(currency1, new HashMap<>());
            graph.putIfAbsent(currency2, new HashMap<>());

            graph.get(currency1).put(currency2, 1 / rate);
            graph.get(currency2).put(currency1, rate);

            processingFees.put(currency1 + currency2, fee);
            processingFees.put(currency2 + currency1, fee);
        }
    }

    public double convert(String fromCurrency, String toCurrency, double amount) {
        Set<String> visited = new HashSet<>();
        Queue<String[]> queue = new LinkedList<>();
        queue.add(new String[]{fromCurrency, "1"});

        while (!queue.isEmpty()) {
            String[] curr = queue.poll();
            visited.add(curr[0]);
            if (curr[0].equals(toCurrency)) {
                return amount * Double.parseDouble(curr[1]);
            }
            for (Map.Entry<String, Double> entry : graph.get(curr[0]).entrySet()) {
                if (!visited.contains(entry.getKey())) {
                    queue.add(new String[]{entry.getKey(), String.valueOf(Double.parseDouble(curr[1]) * entry.getValue())});
                }
            }
        }
        return -1; // Conversion not possible
    }

    public double calculateProcessingFee(String fromCurrency, String toCurrency, double amount) {
        String key = (fromCurrency.compareTo(toCurrency) < 0) ?
                fromCurrency + toCurrency :
                toCurrency+ fromCurrency;

        double fee = processingFees.getOrDefault(key, 0.0);

        // Check if there's an intermediate currency
        if (fee == 0.0) {
            for (Map.Entry<String, Double> entry : graph.get(fromCurrency).entrySet()) {
                double fee1 = processingFees.getOrDefault(fromCurrency + entry.getKey(), 0.0);
                double fee2 = processingFees.getOrDefault(entry.getKey() + toCurrency, 0.0);
                if (fee1 != 0.0 && fee2 != 0.0) {
                    fee = fee1 + fee2;
                    break;
                }
            }
        }

        return amount * fee;
    }

    public void updateBalance(String userAccount, double amount) {

    }

    public double[] exchangeCurrency(String fromCurrency, String toCurrency, double valueExchange) {
        double exchangeAmount = convert(fromCurrency, toCurrency, valueExchange);
        double processingFee = calculateProcessingFee(fromCurrency, toCurrency, valueExchange);

        if(fromCurrency.equals(toCurrency)){processingFee = 0;}

        exchangeAmount = Math.round(exchangeAmount * 100.0) / 100.0;
        processingFee = Math.round(processingFee * 100.0) / 100.0;

        return new double[]{exchangeAmount, processingFee};
    }
}
