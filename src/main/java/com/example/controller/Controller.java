package com.example.controller;

import com.example.Database;
import com.example.EmailSender;
import com.example.model.Account;
import com.example.model.AccountHolder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

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
    private Button button_forgotPassword;
    @FXML
    private Button button_needHelp;
    @FXML
    private PasswordField pf_password;
    @FXML
    private TextField tf_username;

    private Stage stage;
    private Scene scene;

    private Account<?> account;

    @FXML
    void login(ActionEvent event) {
        Account<?> userAccount = Database.getUserByUsername(tf_username.getText());
        account = userAccount;
        if (userAccount != null) {
            try {
                if (isBCryptHash(userAccount.getPassword())) {
                    if (BCrypt.checkpw(pf_password.getText(), userAccount.getPassword())) {
                        handleOTPVerification(event, userAccount);
                    } else {
                        showAlert("Login Failed", "Invalid username or password", "Please enter valid credentials.");
                    }
                } else {
                    if (userAccount.getPassword().equals(pf_password.getText())) {
                        handleOldFormatPassword(event, userAccount);
                    } else {
                        showAlert("Login Failed", "Invalid username or password", "Please enter valid credentials.");
                    }
                }
            } catch (IllegalArgumentException e) {
                showAlert("Login Failed", "Invalid password format", "Please reset your password.");
            }
        } else {
            showAlert("Login Failed", "Invalid username", "Please register before login.");
        }
    }

    private boolean isBCryptHash(String password) {
        return password != null && (password.startsWith("$2a$") || password.startsWith("$2b$"));
    }

    private void handleOTPVerification(ActionEvent event, Account<?> userAccount) {
        int maxAttempts = 3;
        int attemptCount = 0;

        if (verifyOTP()) {
            while (attemptCount < maxAttempts) {
                if (verifyPin()) {
                    AccountHolder.getInstance().setUser(userAccount);
                    sendLoginNotification(userAccount.getUsername(), userAccount.getEmail());
                    String userType = String.valueOf(userAccount.getUserType());
                    if (userType.equals("Goblin")) {
                        DBUtils.changeSceneWithData(event, "/pages/home3.fxml", "Goblin Home", userAccount);
                    } else {
                        DBUtils.changeSceneWithData(event, "/pages/home.fxml", "Home", userAccount);
                    }
                    break;
                } else {
                    attemptCount++;
                    int attemptsLeft = maxAttempts - attemptCount;
                    if (attemptsLeft > 0) {
                        DBUtils.showAlert("Invalid PIN", "The entered PIN is incorrect. You have " + attemptsLeft + " attempts left.");
                    } else {
                        DBUtils.showAlert("Maximum Attempts Reached", "You have exceeded the maximum number of PIN attempts.");
                    }
                }
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
        }
        return false;
    }

    private boolean verifyOTP() {
        try {
            String otp = EmailSender.generateOTP();
            LocalDateTime otpTime = EmailSender.generateOTPTime();

            String subject = "OTP Verification Code for Secure Login";
            String body = "Dear " + account.getUsername() + ",\n\n"
                    + "To enhance the security of your account, we require a One-Time Password (OTP) for your login.\n\n"
                    + "Your OTP is: " + otp + "\n\n"
                    + "Please enter this code within the next 10 minutes to complete your login process.\n\n"
                    + "If you did not request this OTP, please secure your account immediately by changing your password and contacting our support team.\n\n"
                    + "Thank you for your cooperation.\n\n"
                    + "Best regards,\n"
                    + "Gringotts Bank Support Team\n";

            EmailSender.sendEmail(account.getEmail(), subject, body);

            int maxAttempts = 3;
            int attemptCount = 0;
            Optional<String> otpResult;
            boolean validOTP = false;

            do {
                if (attemptCount > 0) {
                    DBUtils.showAlert("Invalid OTP", "The entered OTP is incorrect. You have " + (maxAttempts - attemptCount) + " attempts left.");
                }

                TextInputDialog otpDialog = new TextInputDialog();
                otpDialog.setTitle("OTP Verification");
                otpDialog.setHeaderText("Enter the OTP sent to your email");
                otpDialog.setContentText("OTP:");

                otpResult = otpDialog.showAndWait();
                if (otpResult.isPresent()) {
                    String enteredOTP = otpResult.get();
                    validOTP = isValidOTP(otp, enteredOTP, otpTime);
                    attemptCount++;
                } else {
                    DBUtils.showAlert("OTP Required", "Please enter the OTP sent to your email.");
                    otpResult = Optional.empty();
                }
            } while (attemptCount < maxAttempts && !validOTP && otpResult.isPresent());

            return validOTP;

        } catch (Exception e) {
            DBUtils.showAlert("OTP Error", "An error occurred while verifying the OTP: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void handleOldFormatPassword(ActionEvent event, Account<?> userAccount) {
        System.out.println("Login successful with old format!");

        String newHashedPassword = BCrypt.hashpw(pf_password.getText(), BCrypt.gensalt());
        userAccount.setPassword(newHashedPassword);
        Database.updateUserPassword(userAccount);

        System.out.println("Password re-hashed and updated.");
        AccountHolder.getInstance().setUser(userAccount);
        sendLoginNotification(userAccount.getUsername(), userAccount.getEmail());

        String userType = String.valueOf(userAccount.getUserType());
        if (userType.equals("Goblin")) {
            DBUtils.changeSceneWithData(event, "/pages/home3.fxml", "Goblin Home", userAccount);
        } else {
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
    void signUp(ActionEvent event) {
        DBUtils.changeScene(event, "/pages/register.fxml", null, null);
    }

    @FXML
    void forgotPassword(ActionEvent event) {
        DBUtils.changeScene(event, "/pages/forgot-password-view.fxml", "Forgot Password", null);
    }

    @FXML
    private void needHelp(ActionEvent event) {
        DBUtils.changeScene(event, "/pages/needHelp-view.fxml", "Need Help", null);
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

    private boolean isValidOTP(String otp, String enteredOTP, LocalDateTime otpTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutesElapsed = java.time.Duration.between(otpTime, now).toMinutes();

        return enteredOTP.equals(otp) && minutesElapsed <= 10;
    }
}