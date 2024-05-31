package com.example.controller;

import com.example.Database;
import com.example.EmailSender;
import com.example.model.UserDAO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class ForgotPasswordController {

    @FXML
    private Button login_button;
    @FXML
    private Button button_reset;
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField setPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    private String email;
    private String password;

    public void initialize() {
    }

    @FXML
    public void onLoginClicked(ActionEvent actionEvent) throws IOException {
        DBUtils.changeScene(actionEvent,"/pages/login.fxml",null,null);
    }

    public void resetPasswordOnAction(ActionEvent actionEvent) throws SQLException {
        if (emailTextField.getText().isEmpty() || setPasswordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            showAlert("Incomplete Fields", "Please fill in all the required fields.");
            return;
        } else if (!setPasswordField.getText().equals(confirmPasswordField.getText())){
            showAlert("Password Mismatch", "Passwords do not match. Please try again.");
            return;
        }else if (!isStrongPassword(setPasswordField.getText())){
            // Check if password meets criteria
            showAlert("Weak Password", "Password must be at least 8 characters long and contain uppercase, lowercase, digit, and special character.");
            return;
        }else{
            System.out.println("Fields are valid. Continue to change password");
            email = emailTextField.getText();
            password = setPasswordField.getText();
            resetPassword(actionEvent);
        }
    }

    private void resetPassword(ActionEvent actionEvent) throws SQLException {  // Accept the ActionEvent here
        UserDAO userDAO = new UserDAO();

        if (userDAO.emailExists(email)) {
            // Generate OTP and send it to the user's email
            String otp = EmailSender.generateOTP();
            EmailSender.sendEmail(email, "OTP Verification for Password Reset", "Your OTP is: " + otp);

            // Prompt the user to enter the OTP
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("OTP Verification");
            dialog.setHeaderText("Enter the OTP sent to your email");
            dialog.setContentText("OTP:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String enteredOTP = result.get();
                if (otp.equals(enteredOTP)) {
                    // Check if the new password is not identical to the previous one
                    if (!userDAO.isPasswordIdentical(email, password)) {
                        if (userDAO.updatePassword(email, password)) {
                            showAlert("Password Change", "Password changed successfully, please proceed to login.");
                            clearTextFields();
                            redirectToLoginPage(actionEvent);  // Pass the ActionEvent here
                        } else {
                            showAlert("Password Change", "Password change failed, please try again.");
                        }
                    } else {
                        showAlert("Password Duplicated", "Password is identical with the previous entry, please enter a different password!");
                    }
                } else {
                    showAlert("Invalid OTP", "The entered OTP is incorrect. Please try again.");
                }
            } else {
                showAlert("OTP Required", "Please enter the OTP sent to your email.");
            }
        } else {
            showAlert("Email Not Found", "Email does not exist. Please try again!");
        }
    }

    // Method to clear text fields
    private void clearTextFields() {
        emailTextField.setText("");
        setPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    private boolean isStrongPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[!@#$%^&*()-_=+].*");
    }

    // Method to redirect to the login page
    private void redirectToLoginPage(ActionEvent actionEvent) {
        DBUtils.changeScene(actionEvent,"/pages/login.fxml",null,null);
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}