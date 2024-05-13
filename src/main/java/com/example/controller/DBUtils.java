package com.example.controller;

import com.example.Database;
import com.example.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Date;

import static com.example.Database.getConnection;

public class DBUtils {
    static Database db = new Database();
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/gringottsbank";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static final String INSERT_USER_QUERY = "INSERT INTO user (username, fullName, email, password, DOB, address, phoneNumber, userType, avatarImagePath, currency) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_CARD_QUERY = "INSERT INTO card (userId, cardNum, cvv, expiryDate, cardType) VALUES (?, ?, ?, ?, ?)";

    public static void changeScene(ActionEvent event, String fxmlFile,String title,String username){
        Parent root = null;
        if( username!= null){
            try{
                FXMLLoader loader= new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                LoggedInController loggedInControler =loader.getController();

            }catch(IOException e){
                e.printStackTrace();
            }
        }else{
            try{
                root=FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        Stage stage= (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root,694,400));
        stage.show();
    }

    public static void createAccount(ActionEvent event ,String username, String fullName, String email, String password, java.sql.Date dateOfBirth, String address, String phoneNumber, UserType userType, UserAvatar userAvatar, Currency currency) {


        if (db.checkUsernameExists(username)) {
            System.err.println("Failed to create account: Username already exists.");
            return;
        }

        Account<?> account = new Account<>();
        account.setUsername(username);
        account.setFullName(fullName);
        account.setEmail(email);
        account.setPassword(password);
        account.setDateOfBirth(dateOfBirth);
        account.setAddress(address);
        account.setPhoneNumber(phoneNumber);
        account.setUserType(userType);
        account.setAvatar(userAvatar);
        account.setCurrency(currency);

        // Assuming 'db' is an instance of Database class
        db.createAccount(account);
        Long userId = account.getUserId();


        userAvatar.setUserId(userId);

        changeScene(event, "/pages/logged-in.fxml", "Welcome", username);


    }

    public static void loginUser(ActionEvent event, String username, String password){

        if (db.login(username, password)) {
            // Login successful, perform actions like navigating to another scene
            showAlert("Login Successful", "Welcome back, " + username + "!");
            // Example: change to another scene
            // changeScene("loggedIn.fxml");
        } else {
            // Login failed, show error message
            showAlert("Login Failed", "Invalid username or password. Please try again.");
        }
    }
    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }




}
