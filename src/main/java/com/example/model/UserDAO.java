package com.example.model;

import com.example.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class UserDAO {
    public ObservableList<Account<?>> getUserList(String search, Long userIdToExclude) {
        ObservableList<Account<?>> list = FXCollections.observableArrayList();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE (phoneNumber LIKE ? OR fullName LIKE ?) AND userId <> ?")) {

            // Use wildcards to match partial strings
            statement.setString(1, "%" + search + "%");
            statement.setString(2, "%" + search + "%");
            statement.setLong(3, userIdToExclude);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Account<?> user = new Account<>();
                user.setUserId(Long.valueOf(resultSet.getObject("userId", Integer.class)));
                user.setUsername(resultSet.getString("username"));
                user.setFullName(resultSet.getString("fullName"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setDateOfBirth(resultSet.getDate("DOB"));
                user.setAddress(resultSet.getString("address"));
                user.setPhoneNumber(resultSet.getString("phoneNumber"));
                user.setUserType(UserType.valueOf(resultSet.getString("userType")));
                user.setAvatar(new UserAvatar(resultSet.getString("userType"),user.getUserId()));
                user.setCurrency((resultSet.getString("currency").toUpperCase()));

                list.add(user);
            }
        } catch (SQLException e) {
            // Handle error more gracefully
            System.err.println("Error loading data from database: " + e.getMessage());
        }
        return list;
    }

    public Boolean emailExists(String email) throws SQLException {
        String query = "SELECT email FROM user WHERE email = ?";
        try(Connection con = Database.getConnection()){
            try(PreparedStatement ps = con.prepareStatement(query)){
                ps.setString(1, email);
                try(ResultSet rs = ps.executeQuery()){
                    return rs.next();
                }
            }
        }
    }

    public boolean isPasswordIdentical(String email, String password) throws SQLException{
        String query = "SELECT password FROM user where email = ? AND password = ?";
        try(Connection con = Database.getConnection()){
            try(PreparedStatement ps = con.prepareStatement(query)){
                ps.setString(1, email);
                ps.setString(2, password);
                try(ResultSet rs = ps.executeQuery()){
                    return rs.next();
                }
            }
        }
    }

    public boolean updatePassword(String email, String password) throws SQLException{
        String query = "UPDATE user SET password = ? WHERE email = ?";
        int rowsUpdated;
        try(Connection con = Database.getConnection()){
            try(PreparedStatement ps = con.prepareStatement(query)){
                ps.setString(1, password);
                ps.setString(2, email);
                rowsUpdated = ps.executeUpdate();
            }
        }
        return rowsUpdated > 0;
    }
}
