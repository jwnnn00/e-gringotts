package com.example.model;

import java.util.Date;

public class Card {
    private String cardNum;
    private int cVV;
    private Date expiryDate;
    private Long userId;
    private CardType cardType;
// You can replace Object with appropriate type



    // Constructor
    public Card(String cardNum, int cVV, Date expiryDate, Long userId, CardType cardType) {
        this.cardNum = cardNum;
        this.cVV = cVV;
        this.expiryDate = expiryDate;
        this.userId = userId;
        this.cardType=cardType;
    }

    public Card() {

    }

    public String getCardNum() {
        return cardNum;
    }

    public Long getUserId() {
        return userId;
    }




    public int getCVV() {
        return cVV;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setUserId(long userId) {
        this.userId=userId;
    }
    public enum CardType {Debit,Credit};

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public CardType getCardType() {
        return cardType;
    }
}

