package com.example.controller;

import com.example.model.Currency;
import com.example.model.UserAvatar;
import com.example.model.UserType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    @FXML
    private Button button_register;

    @FXML
    private TextField tf_username;

    @FXML
    private TextField tf_fullName;

    @FXML
    private TextField tf_email;

    @FXML
    private PasswordField pf_password;

    @FXML
    private PasswordField pf_confirmPassword;

    @FXML
    private DatePicker dob;

    @FXML
    private TextField tf_phoneNumber;

    @FXML
    private TextField tf_address;

    @FXML
    private Button button_uploadPic;
    private File selectedFile;

    @FXML
    private ChoiceBox<Currency> cb_currency;

    private String imagePath;

    @FXML
    void register(javafx.event.ActionEvent event){
        String username = tf_username.getText();
        String fullName = tf_fullName.getText();
        String email = tf_email.getText();
        String password = pf_password.getText();
        String confirmPassword = pf_confirmPassword.getText();
        if (!password.equals(confirmPassword)) {
            showAlert("Password Mismatch", "Passwords do not match. Please try again.");
            return;
        }

        // Check if password meets criteria
        if (!isStrongPassword(password)) {
            showAlert("Weak Password", "Password must be at least 8 characters long and contain uppercase, lowercase, digit, and special character.");
            return;
        }
        java.sql.Date dateOfBirth = Date.valueOf(dob.getValue());
        String address = tf_address.getText();
        String phoneNumber = tf_phoneNumber.getText();
        UserType userType = UserType.Silver_Snitch;
        UserAvatar userAvatar = new UserAvatar(imagePath, 0l);
        Currency currency = (Currency) cb_currency.getValue();



    DBUtils.createAccount(event, username, fullName, email, password, dateOfBirth, address, phoneNumber, userType, userAvatar, currency);

    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the ChoiceBox with currency options
        ObservableList<Currency> currencyOptions = FXCollections.observableArrayList(Currency.KNUT, Currency.SICKLE, Currency.GALLEON);
        cb_currency.setItems(currencyOptions);
    }
    private boolean isStrongPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[!@#$%^&*()-_=+].*");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void uploadPicture(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image File");
        // Set the file extension filters if needed
        // fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));

        // Show open file dialog
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            // Assuming imagePath is a TextField to display the selected file path
            imagePath=selectedFile.getAbsolutePath();
        }
    }

}
