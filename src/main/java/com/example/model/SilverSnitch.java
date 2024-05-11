package com.example.model;

import java.util.Date;

// SilverSnitch class
public class SilverSnitch extends Account<SilverSnitch> {
    public SilverSnitch(Long userId, String username, String fullName,String email, String password, Date dateOfBirth, String address, String phoneNumber,Card card, UserType userType, UserAvatar avatar, Currency currency) {
        super(userId, username, fullName, email, password, dateOfBirth, address, phoneNumber,card, userType, avatar,currency);

    }

    public SilverSnitch() {

    }
}

