package com.example;//package com.example;
//
//import com.example.model.*;
//
//
//
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Date;
//
//@Configuration
//public class DatabaseTest {
//    public static void main(String[] args) {
//        testCreateAccountAndTransactionTable();
//    }
//
//    public static void testCreateAccountAndTransactionTable() {
//        Account<User> userAccount = new Account<>();
//        // Generate a user ID
//        long userId = UserIdGenerator.generateUserId();
//        System.out.println(userId);
//
//        // Set the generated user ID to the userAccount
//        userAccount.setUserId(userId);
//
//        userAccount.setUsername("hi");
//        userAccount.setEmail("hihi@example.com");
//        userAccount.setPassword("password123");
//        userAccount.setDateOfBirth(new Date());
//        userAccount.setAddress("123 Main St");
//        userAccount.setRole(Role.USER);
//        userAccount.setAvatar(new UserAvatar("avatar.jpg",userAccount.getUserId()));
//        userAccount.setCard(new Card("1234567890123456", 123, new Date(),userAccount.getUserId()));
//
//
//
//        // Instantiate the Database class
//        Database database = new Database();
//
//        // Call the createAccount method to insert the user account into the database
//        database.createAccount(userAccount);
//
//        // Check if the transaction table for the user has been created
//        // You can add assertions here to validate the table creation if needed
//    }
//}
