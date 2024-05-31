package com.example;

import com.example.controller.DBUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;


public class TransactionDAO {

    public ObservableList<Transaction> getTransactions() {
        ObservableList<Transaction> list = FXCollections.observableArrayList();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM transaction")) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(resultSet.getInt("transactionId"));
                transaction.setUserId(resultSet.getObject("userId", Integer.class));
                transaction.setToFromUserID(resultSet.getObject("tofrom_userID", Integer.class));
                transaction.setTransactionType(Transaction.TransactionType.valueOf(resultSet.getString("transactionType")));
                transaction.setAmount(resultSet.getBigDecimal("amount"));
                transaction.setBalance(resultSet.getBigDecimal("balance"));
                transaction.setTransactionDate(resultSet.getTimestamp("transactionDate").toLocalDateTime());
                transaction.setCategory(Transaction.Category.valueOf(resultSet.getString("category")));
                //transaction.setPaymentMethod(String.valueOf(Transaction.PaymentMethod.mapPaymentMethod(resultSet.getString("paymentMethod"))));

                list.add(transaction);
            }
        } catch (SQLException e) {
            // Handle error more gracefully
            System.err.println("Error loading data from database: " + e.getMessage());
        }
        return list;
    }
    public ObservableList<Transaction> getTransactionsByUsername(String username) {
        int userID = Math.toIntExact(Database.getUserByUsername(username).getUserId());
        //System.out.println(userID);
        ObservableList<Transaction> list = FXCollections.observableArrayList();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM transaction WHERE userID = ? ORDER BY transactionDate DESC")) {

            statement.setInt(1, userID);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(resultSet.getInt("transactionId"));
                transaction.setUserId(resultSet.getObject("userId", Integer.class));
                transaction.setToFromUserID(resultSet.getObject("tofrom_userID", Integer.class));
                transaction.setTransactionType(Transaction.TransactionType.valueOf(resultSet.getString("transactionType")));
                transaction.setAmount(resultSet.getBigDecimal("amount"));
                transaction.setBalance(resultSet.getBigDecimal("balance"));
                transaction.setTransactionDate(resultSet.getTimestamp("transactionDate").toLocalDateTime());
                transaction.setCategory(Transaction.Category.valueOf(resultSet.getString("category")));
                //transaction.setPaymentMethod(String.valueOf(Transaction.PaymentMethod.mapPaymentMethod(resultSet.getString("paymentMethod"))));

                list.add(transaction);
            }
        } catch (SQLException e) {
            // Handle error more gracefully
            System.err.println("Error loading data from database: " + e.getMessage());
        }
        return list;
    }

    public ObservableList<Transaction> getTransactionsByDateRange(String username, LocalDate startDate, LocalDate endDate) {
        ObservableList<Transaction> list = FXCollections.observableArrayList();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM transaction WHERE userId = ? AND transactionDate >= ? AND transactionDate <= ? ORDER BY transactionDate DESC")) {

            statement.setInt(1, getUserIdByUsername(username));
            statement.setDate(2, Date.valueOf(startDate));
            statement.setDate(3, Date.valueOf(endDate));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setTransactionId(resultSet.getInt("transactionId"));
                    transaction.setUserId(resultSet.getObject("userId", Integer.class));
                    transaction.setToFromUserID(resultSet.getObject("tofrom_userID", Integer.class));
                    transaction.setTransactionType(Transaction.TransactionType.valueOf(resultSet.getString("transactionType")));
                    transaction.setAmount(resultSet.getBigDecimal("amount"));
                    transaction.setBalance(resultSet.getBigDecimal("balance"));
                    transaction.setTransactionDate(resultSet.getTimestamp("transactionDate").toLocalDateTime());
                    transaction.setCategory(Transaction.Category.valueOf(resultSet.getString("category")));
                    // transaction.setPaymentMethod(Transaction.PaymentMethod.mapPaymentMethod(resultSet.getString("paymentMethod")));

                    list.add(transaction);
                }
            }
        } catch (SQLException e) {
            // Handle error more gracefully
            System.err.println("Error loading data from database: " + e.getMessage());
        }
        return list;
    }

    public ObservableList<Transaction> getTransactionsByAmountRange(String username, BigDecimal min_amount, BigDecimal max_amount) {
        ObservableList<Transaction> list = FXCollections.observableArrayList();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM transaction WHERE userId = ? AND amount BETWEEN ? AND ? ORDER BY transactionDate DESC")) {

            statement.setInt(1, getUserIdByUsername(username));
            statement.setBigDecimal(2, min_amount);
            statement.setBigDecimal(3, max_amount);

            try(ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setTransactionId(resultSet.getInt("transactionId"));
                    transaction.setUserId(resultSet.getObject("userId", Integer.class));
                    transaction.setToFromUserID(resultSet.getObject("tofrom_userID", Integer.class));
                    transaction.setTransactionType(Transaction.TransactionType.valueOf(resultSet.getString("transactionType")));
                    transaction.setAmount(resultSet.getBigDecimal("amount"));
                    transaction.setBalance(resultSet.getBigDecimal("balance"));
                    transaction.setTransactionDate(resultSet.getTimestamp("transactionDate").toLocalDateTime());
                    transaction.setCategory(Transaction.Category.valueOf(resultSet.getString("category")));
                    //transaction.setPaymentMethod(Transaction.PaymentMethod.mapPaymentMethod(resultSet.getString("paymentMethod")).toString());

                    list.add(transaction);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public ObservableList<Transaction> getTransactionsByCategory(String username, String category) {
        ObservableList<Transaction> list = FXCollections.observableArrayList();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM transaction WHERE userId = ? AND category = ? ORDER BY transactionDate DESC")) {

            statement.setInt(1, getUserIdByUsername(username));
            statement.setString(2, category);

            try(ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setTransactionId(resultSet.getInt("transactionId"));
                    transaction.setUserId(resultSet.getObject("userId", Integer.class));
                    transaction.setToFromUserID(resultSet.getObject("tofrom_userID", Integer.class));
                    transaction.setTransactionType(Transaction.TransactionType.valueOf(resultSet.getString("transactionType")));
                    transaction.setAmount(resultSet.getBigDecimal("amount"));
                    transaction.setBalance(resultSet.getBigDecimal("balance"));
                    transaction.setTransactionDate(resultSet.getTimestamp("transactionDate").toLocalDateTime());
                    transaction.setCategory(Transaction.Category.valueOf(resultSet.getString("category")));
                    //transaction.setPaymentMethod(String.valueOf(Transaction.PaymentMethod.mapPaymentMethod(resultSet.getString("paymentMethod"))));

                    list.add(transaction);
                }
            }

        } catch (SQLException e) {
            // Handle error more gracefully
            System.err.println("Error loading data from database: " + e.getMessage());
        }
        return list;
    }

    public String getUserFullName(int userId) {
        String fullName = null;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE userId = ?")) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                fullName = resultSet.getString("fullName");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return fullName;
    }

    public int getUserIdByUsername(String username) {
        int userId = -1;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT userId FROM user WHERE username = ?")) {

            statement.setString(1, username);
            try(ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    userId = resultSet.getInt("userId");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error while retrieving user ID: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return userId;
    }
}