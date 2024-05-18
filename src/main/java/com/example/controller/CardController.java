package com.example.controller;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
public class CardController {

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



    public void initialize(String cardImagePath, String balance, String cardNum, String exp, String cvv, String cardType, Color textColor) {
        i_cardType.setImage(new javafx.scene.image.Image(cardImagePath));
        t_balance.setText(balance);
        t_cardNum.setText(cardNum);
        t_exp.setText(exp);
        t_cvv.setText(cvv);
        t_cardType.setText(cardType);
        t_cardType.setFill(textColor); // Set text color
    }

}
