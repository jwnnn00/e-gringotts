package com.example.model;

import java.util.Date;

// PlatinumPatronus class
public class PlatinumPatronus extends Account<PlatinumPatronus> {
    public PlatinumPatronus(Long userId, String username, String fullName,String email, String password, Date dateOfBirth, String address, String phoneNumber,Card card, UserType userType, UserAvatar avatar, String currency,double balance) {
        super(userId, username, fullName, email, password, dateOfBirth, address, phoneNumber,card, userType, avatar,currency,balance);


    }
}
