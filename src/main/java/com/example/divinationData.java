package com.example;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class divinationData {
    private static final String URL = "jdbc:mysql://localhost:3306/gringottsbank";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static class DatabaseManager {
        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
    }

    private static void recordTransaction(Connection conn, int transactionId, int userID, int tofrom_userID, String transactionType, double amount, LocalDate date, String category) throws SQLException {
        String sql = "INSERT INTO Transaction (transactionId, userId, tofrom_userID, transactionType, amount, transactionDate, category) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1,transactionId);
            stmt.setInt(2, userID);
            stmt.setInt(3,tofrom_userID);
            stmt.setString(4, transactionType);
            stmt.setDouble(5, amount);
            stmt.setDate(7, Date.valueOf(date));
            stmt.setString(8, category);
            stmt.executeUpdate();
        }
    }

    public static double calculateTotalExpenditures(Connection conn, int userID) throws SQLException {
        String sql = "SELECT SUM(amount) AS total_amount FROM Transaction WHERE userId = ?";
        double totalExpenditures = 0.0;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                totalExpenditures = rs.getDouble("total_amount");
            }
        }

        return totalExpenditures;
    }

    public static Map<String, Double> calculateExpenditures(Connection conn, int userID) throws SQLException {
        String sql = "SELECT category, SUM(amount) AS total_amount FROM Transaction WHERE userId = ? GROUP BY category";
        Map<String, Double> expenditures = new HashMap<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String category = rs.getString("category");
                double totalAmount = rs.getDouble("total_amount");
                expenditures.put(category, totalAmount);
            }
        }
        return expenditures;
    }

    // Filter transactions by category from MySQL
    public static List<Transaction> filterTransactionsByCategory(int userId, String category) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transaction WHERE userId = ? AND category LIKE ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, "%" + category + "%"); // Use LIKE for case-insensitive match

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getInt("transactionId"),
                        rs.getBigDecimal("amount"),
                        rs.getDate("transactionDate").toLocalDate().atStartOfDay()
                );
                transactions.add(transaction);
            }
        }
        return transactions;
    }


    // Filter transactions by date range from MySQL
    public static List<Transaction> filterTransactionsByDateRange(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transaction WHERE userId = ? AND transactionDate BETWEEN ? AND ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getInt("transactionId"),
                        rs.getBigDecimal("amount"),
                        rs.getDate("transactionDate").toLocalDate().atStartOfDay()
                );
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    // Filter transactions by payment method from MySQL
    public static List<Transaction> filterTransactionsByPaymentMethod(int userId, String transactionType) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transaction WHERE userId = ? AND transactionType = ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, transactionType);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getInt("transactionId"),
                        rs.getBigDecimal("amount"),
                        rs.getDate("transactionDate").toLocalDate().atStartOfDay()
                );
                transactions.add(transaction);
            }
        }
        return transactions;
    }

}
