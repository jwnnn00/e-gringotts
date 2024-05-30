package com.example.controller;

import com.example.Database;
import com.example.model.Account;
import com.example.model.AccountHolder;
import com.example.model.Card;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.image.Image;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class UserProfileController extends HomeController implements Initializable {

    @FXML
    private Button button_logoutUP;
    @FXML
    private Text t_username, t_fullName, t_email, t_phoneNumber, t_address, t_dob, t_currency;
    @FXML
    private  Text t_usernameB, t_userType;

    @FXML
    private ImageView i_userAvatar;
    @FXML
    private ImageView i_cardType;

    @FXML
    private Text t_balance;

    @FXML
    private Text t_cardNum;

    @FXML
    private Text t_exp;

    @FXML
    private Text t_cvv;
    @FXML
    private Text t_cardType;
    CardController cardController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AccountHolder holder = AccountHolder.getInstance();
        Account<?> loggedInAccount = holder.getUser();
        String username = loggedInAccount.getUsername();
        t_username.setText(username);
        t_usernameB.setText(username);
        t_userType.setText(loggedInAccount.getUserType().toString());
        t_fullName.setText(loggedInAccount.getFullName());
        t_email.setText(loggedInAccount.getEmail());
        t_phoneNumber.setText(loggedInAccount.getPhoneNumber());
        t_address.setText(loggedInAccount.getAddress());
        t_dob.setText(loggedInAccount.getDateOfBirth().toString());
        t_currency.setText(loggedInAccount.getCurrency().toString());
        UIUtils.initializeUI(button_userProfile, user_avatar, loggedInAccount, username);

        // Retrieve the avatar image path from the account
        String avatarPath = loggedInAccount.getAvatar().getImagePath();
        //System.out.println(avatarPath);
        // Load the avatar image into the ImageView
        if (avatarPath != null && !avatarPath.isEmpty()) {
            Image image = new Image("file:///" + avatarPath);
            i_userAvatar.setImage(image);
        } else {
            URL avatarUrl = getClass().getResource("/img/USER_ICON.png");
            if (avatarUrl != null) {
                Image image = new Image(avatarUrl.toExternalForm());
                i_userAvatar.setImage(image);
            } else {
                System.err.println("Resource not found: /img/USER_ICON.png");
            }
        }

        if (loggedInAccount != null) {
            CardController cardController = new CardController();

            // Retrieve card details from the database
            // Assuming you have a method in the Database class to retrieve card details
            Card card = Database.getCardDetails(loggedInAccount.getUserId());
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
            String cardImagePath = "";
            switch (loggedInAccount.getUserType()) {
                case Silver_Snitch:
                    cardImagePath = "/img/silver_card.png";
                    t_cardType.setFill(Color.BLACK);
                    break;
                case Golden_Galleon:
                    cardImagePath = "/img/gold_card.png";
                    t_cardType.setFill(Color.BLACK);
                    break;
                case Platinum_Patronus:
                    cardImagePath = "/img/platinum_card.png";
                    t_cardType.setFill(Color.WHITE);
                    t_cardNum.setFill(Color.WHITE);
                    t_cvv.setFill(Color.WHITE);
                    t_balance.setFill(Color.WHITE);
                    t_exp.setFill(Color.WHITE);
                    break;
                default:
                    cardImagePath = "/img/silver_card.png";
                    t_cardType.setFill(Color.BLACK);
                    break;
            }

            URL cardImageUrl = getClass().getResource(cardImagePath);
            if (cardImageUrl != null) {
                i_cardType.setImage(new Image(cardImageUrl.toExternalForm()));
            } else {
                System.err.println("Resource not found: " + cardImagePath);
            }

            t_balance.setText("$" + loggedInAccount.getBalance());
            t_cardNum.setText(formattedCardNum.toString());
            t_exp.setText(formattedExpiryDate);
            t_cvv.setText(String.valueOf(card.getCVV()));
            t_cardType.setText(card.getCardType().toString());
        } else {
            // Handle the case where user data is not found
            System.out.println("User data not found.");
        }
    }
}
