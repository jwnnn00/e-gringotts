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
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

import static com.example.Database.getConnection;

public class DBUtils {
    static Database db = new Database();
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/gringottsbank";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static final String INSERT_USER_QUERY = "INSERT INTO user (username, fullName, email, password, DOB, address, phoneNumber, userType, avatarImagePath, currency,pin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
    private static final String INSERT_CARD_QUERY = "INSERT INTO card (userId, cardNum, cvv, expiryDate, cardType) VALUES (?, ?, ?, ?, ?)";

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 1200, 625));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void changeSceneWithData(ActionEvent event, String fxmlPath, String title, Account<?> userAccount) {
        try {
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Access controller of the loaded FXML
            HomeController controller = loader.getController();

            // Pass data to the controller
            controller.initializeLoggedInPage(userAccount);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void changeSceneWithData(Node node, String fxmlPath, String title, Account<?> userAccount) {
        try {
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Access controller of the loaded FXML
            HomeController controller = loader.getController();

            // Pass data to the controller
            controller.setUserAccount(userAccount);

            // Get the stage from the provided node
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public static void clearSession(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource("/pages/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 625));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createAccount(ActionEvent event, String username, String fullName, String email, String password, java.sql.Date dateOfBirth, String address, String phoneNumber, UserType userType, UserAvatar userAvatar, String currency,String pin) {
        if (db.checkUsernameExists(username)) {
            System.err.println("Failed to create account: Username already exists.");
            return;
        }

//        // Generate a random salt
//        String salt = PasswordUtils.generateSalt();
//
//        // Combine the password and salt
//        String saltedPassword = password + salt;
//
//        // Hash the salted password
//        String hashedPassword = PasswordUtils.hashPassword(saltedPassword, salt);

        // Hash the password using BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());


        Account<?> account = new Account<>();
        account.setUsername(username);
        account.setFullName(fullName);
        account.setEmail(email);
        account.setPassword(hashedPassword); // Store the hashed password
//        account.setPassword(password);
        account.setDateOfBirth(dateOfBirth);
        account.setAddress(address);
        account.setPhoneNumber(phoneNumber);
        account.setUserType(userType);
        account.setAvatar(userAvatar);
        account.setCurrency(currency);
        account.setBalance(2000);
        account.setPin(pin);

        db.createAccount(account);
        Long userId = account.getUserId();
        userAvatar.setUserId(userId);

        changeScene(event, "/pages/login.fxml", "Welcome", username);
    }



    static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }





}
