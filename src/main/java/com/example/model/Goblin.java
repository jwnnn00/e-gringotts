//package com.example.model;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.Date;
//import java.sql.*;
//
//
//public class Goblin extends Account<Goblin> {
//
//
//public Goblin(Long userId, String username, String fullName,String email, String password, Date dateOfBirth, String address, String phoneNumber,Card card, UserType userType, UserAvatar avatar, String currency,double balance) {
//    super(userId, username, fullName, email, password, dateOfBirth, address, phoneNumber,card, userType, avatar,currency,balance);
//        this.setUserType(UserType.valueOf("Goblin"));
//
//    }
//
//    public Goblin() {
//
//    }
//
//    private Connection getConnection() throws SQLException {
//        // Modify the connection URL, username, and password accordingly
//        String jdbcUrl = "jdbc:mysql://localhost:3306/gringottsbank";
//        String username = "root";
//        String password = "";
//
//        return DriverManager.getConnection(jdbcUrl, username, password);
//    }
//
//    // Method to get the total number of users
//    public int getTotalNumberOfUsers() {
//        int totalUsers = 0;
//        try (Connection connection = getConnection();
//             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM user");
//             ResultSet resultSet = statement.executeQuery()) {
//            if (resultSet.next()) {
//                totalUsers = resultSet.getInt(1);
//            }
//        } catch (SQLException e) {
//            // Handle SQLException
//            e.printStackTrace();
//        }
//        return totalUsers;
//    }
//
//    // Method to get the number of transactions per day
//    public int getNumberOfTransactionsPerDay(Date date) {
//        int transactionsPerDay = 0;
//        try (Connection connection = getConnection();
//             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM transactions WHERE transaction_date = ?");
//        ) {
//            statement.setDate(1, new java.sql.Date(date.getTime()));
//            try (ResultSet resultSet = statement.executeQuery()) {
//                if (resultSet.next()) {
//                    transactionsPerDay = resultSet.getInt(1);
//                }
//            }
//        } catch (SQLException e) {
//            // Handle SQLException
//            e.printStackTrace();
//        }
//        return transactionsPerDay;
//    }
//
//    public void editUserType(Long userId, String newUserType) {
//        try (Connection connection = getConnection();
//             PreparedStatement statement = connection.prepareStatement("UPDATE users SET user_type = ? WHERE user_id = ?")) {
//            statement.setString(1, newUserType);
//            statement.setLong(2, userId);
//            int rowsUpdated = statement.executeUpdate();
//            if (rowsUpdated > 0) {
//                System.out.println("User type updated successfully for user ID: " + userId);
//            } else {
//                System.out.println("User not found with ID: " + userId);
//            }
//        } catch (SQLException e) {
//            // Handle SQLException
//            e.printStackTrace();
//        }
//    }
//}
