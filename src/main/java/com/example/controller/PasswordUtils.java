package com.example.controller;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    // Generate a random salt
    public static String generateSalt() {
        return BCrypt.gensalt();
    }

    // Hash the password with the provided salt
    public static String hashPassword(String password, String salt) {
        return BCrypt.hashpw(password, salt);
    }

    // Verify the entered password against the stored hashed password
    public static boolean verifyPassword(String enteredPassword, String storedHash) {
        return BCrypt.checkpw(enteredPassword, storedHash);
    }
}
