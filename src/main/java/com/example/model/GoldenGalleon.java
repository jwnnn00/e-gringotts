package com.example.model;

import java.util.Date;

// GoldenGalleon class
public class GoldenGalleon extends Account<GoldenGalleon> {
    public GoldenGalleon(Long userId, String username, String fullName,String email, String password, Date dateOfBirth, String address, String phoneNumber,Card card, UserType userType, UserAvatar avatar, Currency currency) {
        super(userId, username, fullName, email, password, dateOfBirth, address, phoneNumber,card, userType, avatar,currency);

    }
}
