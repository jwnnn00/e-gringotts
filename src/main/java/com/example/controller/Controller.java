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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Controller {

    private final Database db = new Database();
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

    @FXML
    void login(javafx.event.ActionEvent event) {
        Account<?> userAccount = Database.getUserByUsername(tf_username.getText());

        if (userAccount != null) {
            //System.out.println("User found: " + userAccount.getUsername());
            //System.out.println("Stored password hash: " + userAccount.getPassword());

            try {
                if (isBCryptHash(userAccount.getPassword())) {
                    if (BCrypt.checkpw(pf_password.getText(), userAccount.getPassword())) {
                        //System.out.println("Password matches (BCrypt).");
                        handleOTPVerification(event, userAccount);
                    } else {
                        //System.out.println("Password does not match.");
                        showAlert("Login Failed", "Invalid username or password", "Please enter valid credentials.");
                    }
                } else {
                    if (userAccount.getPassword().equals(pf_password.getText())) {
                        //System.out.println("Password matches (plain text).");
                        handleOldFormatPassword(event, userAccount);
                    } else {
                        //System.out.println("Password does not match.");
                        showAlert("Login Failed", "Invalid username or password", "Please enter valid credentials.");
                    }
                }
            } catch (IllegalArgumentException e) {
                // Handle invalid salt version
                //System.out.println("Invalid salt version: " + e.getMessage());
                showAlert("Login Failed", "Invalid password format", "Please reset your password.");
            }
        } else {
            //System.out.println("No user found with username: " + tf_username.getText());
            showAlert("Login Failed", "Invalid username", "Please enter register before login.");
        }
    }

    private boolean isBCryptHash(String password) {
        return password != null && (password.startsWith("$2a$") || password.startsWith("$2b$"));
    }

    private void handleOTPVerification(javafx.event.ActionEvent event, Account<?> userAccount) {
        String otp = EmailSender.generateOTP();
        LocalDateTime otpTime = EmailSender.generateOTPTime();

        String subject = "OTP Verification Code for Secure Login";
        String body = "Dear " + userAccount.getUsername() + ",\n\n"
                + "To enhance the security of your account, we require a One-Time Password (OTP) for your login.\n\n"
                + "Your OTP is: " + otp + "\n\n"
                + "Please enter this code within the next 10 minutes to complete your login process.\n\n"
                + "If you did not request this OTP, please secure your account immediately by changing your password and contacting our support team.\n\n"
                + "Thank you for your cooperation.\n\n"
                + "Best regards,\n"
                + "Gringotts Bank Support Team\n";

        EmailSender.sendEmail(userAccount.getEmail(), subject, body);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("OTP Verification");
        dialog.setHeaderText("Enter the OTP sent to your email");
        dialog.setContentText("OTP:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String enteredOTP = result.get();
            if (isValidOTP(enteredOTP, otpTime)) {
                AccountHolder.getInstance().setUser(userAccount);
                sendLoginNotification(userAccount.getUsername(), userAccount.getEmail());
                String userType = String.valueOf(userAccount.getUserType());
                System.out.println(userType);
                if(userType.equals("Goblin")){
                    DBUtils.changeSceneWithData(event, "/pages/home3.fxml", "Goblin Home", userAccount);
                }else{
                    DBUtils.changeSceneWithData(event, "/pages/home.fxml", "Home", userAccount);
                }
            } else {
                showAlert("Invalid OTP", "The entered OTP is incorrect or has expired. Please try again.", "");
            }
        } else {
            showAlert("OTP Required", "Please enter the OTP sent to your email.", "");
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

        String userType = String.valueOf(userAccount.getUserType());
        if(userType.equals("Goblin")){
            DBUtils.changeSceneWithData(event, "/pages/home3.fxml", "Goblin Home", userAccount);
        }else{
            DBUtils.changeSceneWithData(event, "/pages/home.fxml", "Home", userAccount);
        }

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

    @FXML
    void forgotPassword(javafx.event.ActionEvent event) {
        DBUtils.changeScene(event, "/pages/forgot-password-view.fxml", "Forgot Password", null);
    }

    private void sendLoginNotification(String username, String userEmail) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        String subject = "Login Notification";
        String body = "Dear " + username + ",\n\n"
                + "We noticed a login to your account at " + formattedDateTime + ".\n\n"
                + "If this was you, no further action is required. If you did not log in at this time, please secure your account immediately by changing your password or contacting our support team.\n\n"
                + "For your reference, here are the details of the login:\n"
                + "Username: " + username + "\n"
                + "Date and Time: " + formattedDateTime + "\n\n"
                + "Thank you for your attention to this matter.\n\n"
                + "Best regards,\n"
                + "Gringotts Bank Support Team\n";

        EmailSender.sendEmail(userEmail, subject, body);
    }

    private boolean isValidOTP(String enteredOTP, LocalDateTime otpTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutesElapsed = java.time.Duration.between(otpTime, now).toMinutes();

        // Check if the OTP is correct and within the 10-minute window
        return enteredOTP.equals(enteredOTP) && minutesElapsed <= 10;
    }

}
