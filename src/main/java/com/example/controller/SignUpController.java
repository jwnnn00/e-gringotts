package com.example.controller;

import com.example.Database;
import com.example.EmailSender;
import com.example.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    @FXML
    public Button button_login;
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
    public Account<?> userAccount;


    @FXML
    void register(javafx.event.ActionEvent event) throws IOException {
        String username = tf_username.getText();
        String fullName = tf_fullName.getText();
        String email = tf_email.getText();
        if (!isValidEmail(email)) {
            showAlert("Invalid Email", "Please enter a valid email address.");
            return;
        }
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

        String otp = EmailSender.generateOTP();
        EmailSender.sendEmail(email, "OTP Verification", "Your OTP is: " + otp);

        // Prompt the user to enter the OTP
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("OTP Verification");
        dialog.setHeaderText("Enter the OTP sent to your email");
        dialog.setContentText("OTP:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String enteredOTP = result.get();
            if (otp.equals(enteredOTP)) {

                DBUtils.createAccount(event, username, fullName, email, password, dateOfBirth, address, phoneNumber, userType, userAvatar, currency);
                setUserAccount(Database.getUserByUsername(username));
                loadCardDetails();
                return;
                } else {
                    DBUtils.showAlert("Invalid OTP", "The entered OTP is incorrect. Please try again.");
                    return; // Stop login process if OTP is incorrect
                }
            } else {
                DBUtils.showAlert("OTP Required", "Please enter the OTP sent to your email.");
                return; // Stop login process if OTP is not entered
            }


    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {


        // Initialize the ChoiceBox with currency options
        ObservableList<Currency> currencyOptions = FXCollections.observableArrayList(Currency.KNUT, Currency.SICKLE, Currency.GALLEON);
        cb_currency.setItems(currencyOptions);


    }

    public void setUserAccount(Account<?> userAccount) {
        this.userAccount = userAccount;
        // Use userAccount data as needed in the home scene
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
    void login (javafx.event.ActionEvent event){
        DBUtils.changeScene(event,"/pages/login.fxml",null,null);
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
            imagePath = selectedFile.getAbsolutePath();
        }
    }

    private boolean isValidEmail(String email) {
        // Regular expression for validating email addresses
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    public void loadCardDetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pages/card.fxml"));
            Parent root = loader.load();

            CardController cardController = loader.getController();

            // Retrieve card details from the database
            // Assuming you have a method in the Database class to retrieve card details
            Card card = Database.getCardDetails(userAccount.getUserId());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");
            String formattedExpiryDate = dateFormat.format(card.getExpiryDate());
            String cardNum = card.getCardNum();
            StringBuilder formattedCardNum = new StringBuilder();
            for (int i = 0; i < cardNum.length(); i++) {
                if (i > 0 && i % 4 == 0) {
                    formattedCardNum.append(" "); // Insert space every 4 digits
                }
                formattedCardNum.append(cardNum.charAt(i));
            }

            // Load card image based on userType
            String cardImagePath;
            switch (userAccount.getUserType()) {
                case Silver_Snitch:
                    cardImagePath = "/img/silver_card.png";
                    break;
                case Golden_Galleon:
                    cardImagePath = "/img/golden_card.png";
                    break;
                case Platinum_Patronus:
                    cardImagePath = "/img/platinum_card.png";
                    cardController.initialize(cardImagePath, "$100.00", formattedCardNum.toString(), formattedExpiryDate, Integer.toString(card.getCVV()), card.getCardType().toString()+" Card", Color.WHITE);
                    break;
                default:
                    cardImagePath = "/path/to/silver_card.png";
                    break;
            }
            ImageView cardImage = new ImageView(getClass().getResource(cardImagePath).toExternalForm());


            cardController.initialize(cardImagePath, "$100.00", formattedCardNum.toString(), formattedExpiryDate, Integer.toString(card.getCVV()),card.getCardType().toString()+" Card", Color.BLACK);
            String otp = EmailSender.generateOTP();

            // Send OTP via email
            String recipientEmail = tf_email.getText(); // Assuming you have a text field for email
            String subject = "Verification Code for Registration";
            String body = "Your OTP code is: " + otp;

            EmailSender.sendEmail(recipientEmail, subject, body);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("User Details");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
