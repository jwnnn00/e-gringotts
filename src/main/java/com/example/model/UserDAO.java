package com.example.model;

import com.example.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public ObservableList<Account<?>> getUserList(String search) {
        ObservableList<Account<?>> list = FXCollections.observableArrayList();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE phoneNumber LIKE ? or fullName LIKE ?")) {

            // Use wildcards to match partial strings
            statement.setString(1, "%" + search + "%");
            statement.setString(2, "%" + search + "%");

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

}
