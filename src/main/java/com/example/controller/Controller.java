package com.example.controller;

import com.example.Database;
import com.example.EmailSender;

import com.example.model.Account;
import com.example.model.AccountHolder;
import com.example.model.UserType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Controller {



    @FXML
    private Button button_login;

    @FXML
    private Button button_register;



    @FXML
    private PasswordField pf_password;

    @FXML
    private TextField tf_username;

    private Stage stage;
    private Scene scene;
    private final Database db = new Database();



    @FXML
    void login(javafx.event.ActionEvent event) {
        // Fetch user details from the database
        Account<?> userAccount = Database.getUserByUsername(tf_username.getText());

        if (userAccount != null && BCrypt.checkpw(pf_password.getText(), userAccount.getPassword())) {
//            String otp = EmailSender.generateOTP();
//            EmailSender.sendEmail(userAccount.getEmail(), "OTP Verification", "Your OTP is: " + otp);
//
//            // Prompt the user to enter the OTP
//            TextInputDialog dialog = new TextInputDialog();
//            dialog.setTitle("OTP Verification");
//            dialog.setHeaderText("Enter the OTP sent to your email");
//            dialog.setContentText("OTP:");
//
//            Optional<String> result = dialog.showAndWait();
//            if (result.isPresent()) {
//                String enteredOTP = result.get();
//                if (otp.equals(enteredOTP)) {
                    AccountHolder.getInstance().setUser(userAccount);
            sendLoginNotification(userAccount.getUsername(), userAccount.getEmail());
                    // Pass the userAccount to the next scene
                    DBUtils.changeSceneWithData(event, "/pages/home.fxml", "User Page", userAccount);
                    return;
//                } else {
//                    DBUtils.showAlert("Invalid OTP", "The entered OTP is incorrect. Please try again.");
//                    return; // Stop login process if OTP is incorrect
//                }
//            } else {
//                DBUtils.showAlert("OTP Required", "Please enter the OTP sent to your email.");
//                return; // Stop login process if OTP is not entered
//            }

        } else {
            // Display error message for invalid credentials
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Invalid username or password");
            alert.setContentText("Please enter valid credentials.");
            alert.showAndWait();
        }
    }




    @FXML
    void signUp(javafx.event.ActionEvent event){
        DBUtils.changeScene(event,"/pages/register.fxml",null,null);
    }

    private void sendLoginNotification(String username, String userEmail) {
        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();

        // Format the date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        // Construct the email subject and body
        String subject = "Login Notification";
        String body = "User " + username + " logged in successfully at " + formattedDateTime + ".";

        // Send the email notification using EmailSender class
        EmailSender.sendEmail(userEmail, subject, body);
    }

}