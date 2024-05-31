package com.example.controller;

import com.example.EmailSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

public class NeedHelpController {
    @FXML
    private Button send_button;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private TextField emailTextField;

    @FXML
    private Button login_button;

    @FXML
    private TextField nameTextField;
    private static final String RECEIVER_EMAIL = "gringotts0000@gmail.com";
    @FXML
    public void onLoginClicked(ActionEvent actionEvent) throws IOException {
        DBUtils.changeScene(actionEvent,"/pages/login.fxml",null,null);
    }

    @FXML
    void sendHelp(ActionEvent event) throws IOException {
        String comment = commentTextArea.getText();
        String name = nameTextField.getText();
        String email = emailTextField.getText();

        if (email.isEmpty() || name.isEmpty() || comment.isEmpty()) {
            showAlert("Incomplete Fields", "Please fill in all the required fields.");
            return;
        }else if(!isValidEmail(email)){
            showAlert("Invalid Email", "Please enter a valid email address.");
            return;
        }else{
            String subject = "Help Request from " + name;
            String body = "Name: " + name + "\n"
                        + "Email: " + email + "\n"
                        + "Comment: " + comment;
            EmailSender.sendEmail(RECEIVER_EMAIL, subject, body);
            showAlert("Inquiry Sent", "Your inquiry has been received, we'll get back to you ASAP.");
            clearTextFields();
            redirectToLoginPage(event); // Pass the ActionEvent here
        }
    }

    private boolean isValidEmail(String email) {
        // Regular expression for validating email addresses
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private void clearTextFields() {
        emailTextField.setText("");
        commentTextArea.setText("");
        nameTextField.setText("");
    }

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