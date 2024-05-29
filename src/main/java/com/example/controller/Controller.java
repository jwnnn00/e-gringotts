package com.example.controller;

import com.example.Database;
import com.example.EmailSender;
import com.example.model.Account;
import com.example.model.AccountHolder;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

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
    private Account<?> account;

    @FXML
    void login(javafx.event.ActionEvent event) {
        Account<?> userAccount = Database.getUserByUsername(tf_username.getText());
account=userAccount;
        if (userAccount != null) {
            if (BCrypt.checkpw(pf_password.getText(), userAccount.getPassword())) {
                handleOTPVerification(event, userAccount);

            } else if (userAccount.getPassword().equals(pf_password.getText())) {
                handleOldFormatPassword(event, userAccount);
            } else {
                showAlert("Login Failed", "Invalid username or password", "Please enter valid credentials.");
            }
        } else {
            showAlert("Login Failed", "Invalid username or password", "Please enter valid credentials.");
        }
    }

    private void handleOTPVerification(javafx.event.ActionEvent event, Account<?> userAccount) {
        // OTP verification logic here
        // If OTP is verified, then proceed to PIN verification
        if (verifyOTP()) {
            if (verifyPin()) {
                AccountHolder.getInstance().setUser(userAccount);
                sendLoginNotification(userAccount.getUsername(), userAccount.getEmail());
                DBUtils.changeSceneWithData(event, "/pages/home.fxml", "User Page", userAccount);
            } else {
                DBUtils.showAlert("Invalid PIN", "The entered PIN is incorrect. Please try again.");
            }
        } else {
            DBUtils.showAlert("Invalid OTP", "The entered OTP is incorrect. Please try again.");
        }
    }

    private boolean verifyPin() {
        TextInputDialog pinDialog = new TextInputDialog();
        pinDialog.setTitle("PIN Verification");
        pinDialog.setHeaderText("Enter your PIN");
        pinDialog.setContentText("PIN:");

        Optional<String> pinResult = pinDialog.showAndWait();
        if (pinResult.isPresent()) {
            String enteredPin = pinResult.get();
            String storedPin = Database.getUserPin(tf_username.getText());
            return enteredPin.equals(storedPin);
        } else {
            DBUtils.showAlert("PIN Required", "Please enter your PIN.");
            return false;
        }
    }

    private boolean verifyOTP() {
        try {
            System.out.println(account.getEmail());
            // Generate and send OTP
            String otp = EmailSender.generateOTP();
            EmailSender.sendEmail(account.getEmail(), "OTP Verification", "Your OTP is: " + otp);

            // Prompt the user to enter the OTP
            TextInputDialog otpDialog = new TextInputDialog();
            otpDialog.setTitle("OTP Verification");
            otpDialog.setHeaderText("Enter the OTP sent to your email");
            otpDialog.setContentText("OTP:");

            Optional<String> otpResult = otpDialog.showAndWait();
            if (otpResult.isPresent()) {
                String enteredOTP = otpResult.get();
                if (otp.equals(enteredOTP)) {
                    return true;
                } else {
                    DBUtils.showAlert("Invalid OTP", "The entered OTP is incorrect. Please try again.");
                    return false;
                }
            } else {
                DBUtils.showAlert("OTP Required", "Please enter the OTP sent to your email.");
                return false;
            }
        } catch (Exception e) {
            DBUtils.showAlert("OTP Error", "An error occurred while verifying the OTP: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

    }

    private void handleOldFormatPassword(javafx.event.ActionEvent event, Account<?> userAccount) {
        System.out.println("Login successful with old format!");

        String newHashedPassword = BCrypt.hashpw(pf_password.getText(), BCrypt.gensalt());
        userAccount.setPassword(newHashedPassword);
        Database.updateUserPassword(userAccount);

        System.out.println("Password re-hashed and updated.");
        AccountHolder.getInstance().setUser(userAccount);
        sendLoginNotification(userAccount.getUsername(), userAccount.getEmail());
        DBUtils.changeSceneWithData(event, "/pages/home.fxml", "User Page", userAccount);
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void signUp(javafx.event.ActionEvent event) {
        DBUtils.changeScene(event, "/pages/register.fxml", null, null);
    }

    private void sendLoginNotification(String username, String userEmail) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        String subject = "Login Notification";
        String body = "User " + username + " logged in successfully at " + formattedDateTime + ".";

        EmailSender.sendEmail(userEmail, subject, body);
    }
}
