package com.example;

import com.example.model.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LoginRegisterProgram {
    private static final Map<String, Account<?>> users = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        boolean running = true;
        while (running) {
            System.out.println("Welcome to the Login/Register Program!");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    login(scanner);
                    break;
                case 2:
                    register(scanner);
                    break;
                case 3:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please enter 1, 2, or 3.");
            }
        }

        scanner.close();
        System.out.println("Goodbye!");
    }



    public static void login(Scanner scanner) {
        System.out.println("Login");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        // Check if the username exists in the database
        Database db = new Database();
        Account<?> userAccount = db.getUserByUsername(username);

        if (userAccount != null) {
            // Username exists, prompt for password
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            if (userAccount.authenticate(password)) {
                System.out.println("Login successful!");
            } else {
                System.out.println("Invalid password.");
            }
        } else {
            // Username not found in the database
            System.out.println("User not found.");
        }
    }
    private static void register(Scanner scanner) {
        long userID = UserIdGenerator.generateUserId();

        System.out.println("Register");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        if (users.containsKey(username)) {
            System.out.println("Username already exists. Please choose another one.");
            return;
        }


        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine();


        String email;
        do {
            System.out.print("Enter email: ");
            email = scanner.nextLine();
            if (!isValidEmail(email)) {
                System.out.println("Invalid email format. Please enter a valid email.");
            }
        } while (!isValidEmail(email));

        String password;
        boolean validPassword = false;

        do {
            System.out.print("Enter password (at least 8 characters, including uppercase, lowercase, digit, and special character): ");
            password = scanner.nextLine();
            if (!isStrongPassword(password)) {
                // Check and inform the user about the missing elements in the password
                if (password.length() < 8) {
                    System.out.println("Password must be at least 8 characters long.");
                }
                if (!password.matches(".*[A-Z].*")) {
                    System.out.println("Password must contain at least one uppercase letter.");
                }
                if (!password.matches(".*[a-z].*")) {
                    System.out.println("Password must contain at least one lowercase letter.");
                }
                if (!password.matches(".*\\d.*")) {
                    System.out.println("Password must contain at least one digit.");
                }
                if (!password.matches(".*[!@#$%^&*()-_=+].*")) {
                    System.out.println("Password must contain at least one special character.");
                }
            } else {
                validPassword = true;
            }
        } while (!validPassword);

        // Confirm password loop
        String confirmPassword;
        do {
            System.out.print("Confirm password: ");
            confirmPassword = scanner.nextLine();
            if (!password.equals(confirmPassword)) {
                System.out.println("Passwords do not match. Please try again.");
            }
        } while (!password.equals(confirmPassword));

        System.out.print("Enter date of birth (yyyy-MM-dd): ");
        String dateOfBirthStr = scanner.nextLine();
        Date dateOfBirth = parseDate(dateOfBirthStr);
        if (dateOfBirth == null) {
            System.out.println("Invalid date format. Registration failed.");
            return;
        }
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();



        // Avatar path
        System.out.print("Enter path of avatar image: ");
        String avatarPath = scanner.nextLine();
        UserType userType=UserType.Silver_Snitch;

        System.out.println("Choose currency: ");
        System.out.println("1. Knut");
        System.out.println("2. Sickle");
        System.out.println("3. Galleon");
        System.out.print("Enter your choice (1/2/3): ");
        int currencyChoice = scanner.nextInt();
        Currency currency;
        switch (currencyChoice) {
            case 1:
                currency = Currency.KNUT;
                break;
            case 2:
                currency = Currency.SICKLE;
                break;
            case 3:
                currency = Currency.GALLEON;
                break;
            default:
                System.out.println("Invalid choice. Defaulting to Knut.");
                currency = Currency.KNUT;
                break;
        }



        Account<SilverSnitch> account=new SilverSnitch();


        Database db = new Database();

        account.setUsername(username);
        account.setFullName(fullName);
        account.setEmail(email);
        account.setPassword(password);
        account.setDateOfBirth(dateOfBirth);
        account.setAddress(address);
        account.setPhoneNumber(phoneNumber);
        account.setUserType(userType);
        account.setAvatar(new UserAvatar(avatarPath, userID));
        account.setCurrency(currency);

        // Call the createAccount method to insert the user account into the database
        db.createAccount(account);

        System.out.println("\nRegistration Successful!");
        System.out.println("Generated User ID: " + account.getUserId());
        System.out.println("Generated Card Number: " + account.getCard().getCardNum());
        System.out.println("Generated CVV: " + account.getCard().getCVV());
        System.out.println("Generated Expiry Date: " + account.getCard().getExpiryDate());
    }

    public static Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(dateString);
        } catch (java.text.ParseException e) {
            return null;
        }
    }

    private static boolean isStrongPassword(String password) {
        // Check password strength criteria (8 characters, including uppercase, lowercase, digit, and special character)
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[!@#$%^&*()-_=+].*");
    }

    private static boolean isValidEmail(String email) {
        // Simple email validation using regex
        return email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }


}
