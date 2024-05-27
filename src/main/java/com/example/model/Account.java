package com.example.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;



public class Account <E>{
    private int transactionCount;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String username;
    private String fullName;
    private String password;
    private Date dateOfBirth;
    private String email;
    private String address;
    private String phoneNumber;
    private static final Account<?> instance = new Account<>();

    private Card card;
  private UserType userType;
    private boolean check;


    private UserAvatar avatar;
    private String currency;

    public Account(Long userId, String username, String fullName,String email, String password, Date dateOfBirth, String address, String phoneNumber,Card card, UserType userType, UserAvatar avatar, String currency) {
        this.userId = userId;
        this.username = username;
        this.fullName=fullName;
        this.email=email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.address=address;
        this.phoneNumber=phoneNumber;
        this.card=card;
        this.userType=userType;
        this.avatar=avatar;
        this.currency=currency;
    }

    public Account() {

    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public Account(Long userId, String username, String fullName, String email, String password, java.sql.Date dateOfBirth, String address, String phoneNumber, UserType userType, UserAvatar userAvatar, String currency) {
        this.userId = userId;
        this.username = username;
        this.fullName=fullName;
        this.email=email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.address=address;
        this.phoneNumber=phoneNumber;

        this.userType=userType;
        this.avatar=userAvatar;
        this.currency=currency;

    }

    public Account(long userID, String username, String fullName, int transactionCount, String userType) {
    this.userId=userID;
    this.username=username;
    this.fullName=fullName;
    this.transactionCount=transactionCount;
    this.userType=UserType.valueOf(userType);

    }

    public static Account<?> getInstance(){
        return instance;
    }

    public boolean authenticate(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public UserAvatar getAvatar() {
        return avatar;
    }

    public void setAvatar(UserAvatar avatar) {
        this.avatar = avatar;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public void setLogin(boolean check){
        this.check = check;
    }
    public boolean getLogin(){
        return check;
    }
    public void clearAllValues() {
        username = null;
        email = null;
        password = null;
        check = false;
    }
}
