package com.example;

import java.sql.*;
import java.util.Calendar;
import java.util.Random;

import com.example.model.*;

public class Database {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/gringottsbank";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static final String INSERT_USER_QUERY = "INSERT INTO user (username, fullName, email, password, DOB, address, phoneNumber, userType, avatarImagePath, currency) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_CARD_QUERY = "INSERT INTO card (userId, cardNum, cvv, expiryDate, cardType) VALUES (?, ?, ?, ?, ?)";

    public void createAccount(Account<?> account) {
        if (checkUsernameExists(account.getUsername())) {
            System.err.println("Failed to create account: Username already exists.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement userStatement = connection.prepareStatement(INSERT_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement cardStatement = connection.prepareStatement(INSERT_CARD_QUERY)) {

            userStatement.setString(1, account.getUsername());
            userStatement.setString(2, account.getFullName());
            userStatement.setString(3, account.getEmail());
            userStatement.setString(4, account.getPassword());
            userStatement.setDate(5, new java.sql.Date(account.getDateOfBirth().getTime()));
            userStatement.setString(6, account.getAddress());
            userStatement.setString(7, account.getPhoneNumber());
            userStatement.setString(8, account.getUserType().toString());
            userStatement.setString(9, account.getAvatar().getImagePath());
            userStatement.setString(10, account.getCurrency().toString());

            int userAffectedRows = userStatement.executeUpdate();
            if (userAffectedRows == 0) {
                System.err.println("Failed to create account: No rows affected.");
                return;
            }

            try (ResultSet generatedKeys = userStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long userId = generatedKeys.getLong(1);
                    account.setUserId(userId);
                } else {
                    System.err.println("Failed to create account: Couldn't retrieve generated userId.");
                    return;
                }
            }

            Card card = generateCard(account.getUserId());
            account.setCard(card);
            if (card != null) {
                cardStatement.setLong(1, account.getUserId());
                cardStatement.setString(2, card.getCardNum());
                cardStatement.setInt(3, card.getCVV());
                cardStatement.setDate(4, new java.sql.Date(card.getExpiryDate().getTime()));
                cardStatement.setString(5, card.getCardType().toString());

                int cardAffectedRows = cardStatement.executeUpdate();
                if (cardAffectedRows == 0) {
                    System.err.println("Failed to create card: No rows affected.");
                } else {
                    System.out.println("Account and Card created successfully!");

                    updateUserAvatar(account.getAvatar());
                }
            } else {
                System.err.println("Failed to generate card.");
            }

        } catch (SQLException e) {
            System.err.println("Failed to create account: " + e.getMessage());
        }


    }

    public boolean checkUsernameExists(String username) {
        String query = "SELECT COUNT(*) FROM user WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
        }
        return false;
    }

    public static void updateUserAvatar(UserAvatar userAvatar) {
        String updateQuery = "UPDATE useravatar SET imagePath = ? WHERE userId = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, userAvatar.getImagePath());
            preparedStatement.setLong(2, userAvatar.getUserId());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("User avatar updated successfully!");
            } else {
                System.out.println("Failed to update user avatar.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating user avatar: " + e.getMessage());
        }
    }

    public Card generateCard(long userId) {
        String cardNumber = generateRandomCardNumber();
        int cvv = generateRandomCVV();
        Date expiryDate = generateRandomExpiryDate();
        Card.CardType cardType = Card.CardType.Debit;

        return new Card(cardNumber, cvv, expiryDate, userId, cardType);
    }

    private String generateRandomCardNumber() {
        StringBuilder cardNumber = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

    private int generateRandomCVV() {
        Random random = new Random();
        return 100 + random.nextInt(900);
    }

    private java.sql.Date generateRandomExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 4); // Set expiry date to 3 years from now
        long expiryTimeMillis = calendar.getTimeInMillis();
        return new java.sql.Date(expiryTimeMillis);
    }

    public Account<?> getUserByUsername(String username) {
        String query = "SELECT * FROM user WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve user information from the ResultSet and create an Account object
                    Long userId = resultSet.getLong("userId");
                    String fullName = resultSet.getString("fullName");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    Date dateOfBirth = resultSet.getDate("DOB");
                    String address = resultSet.getString("address");
                    String phoneNumber = resultSet.getString("phoneNumber");
                    UserType userType = UserType.valueOf(resultSet.getString("userType"));
                    // Assuming you have a method to retrieve the avatar path
                    String avatarPath = resultSet.getString("avatarImagePath");
                    // Assuming you have a method to retrieve the currency
                    Currency currency = Currency.valueOf(resultSet.getString("currency").toUpperCase());

                    // Create and return the Account object
                    return new Account<>(userId, username, fullName, email, password, dateOfBirth, address, phoneNumber, userType, new UserAvatar(avatarPath, userId), currency);
                } else {
                    // User not found
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching for user: " + e.getMessage());
            return null;
        }
    }

    public static boolean login(String username, String password) {
        System.out.println("Login");

        // Check if the username exists in the database
        Database db = new Database();
        Account<?> userAccount = db.getUserByUsername(username);

        if (userAccount != null) {
            // Username exists
            if (userAccount.authenticate(password)) {
                System.out.println("Login successful!");
                return true;
            } else {
                System.out.println("Invalid password.");
            }
        } else {
            // Username not found in the database
            System.out.println("User not found.");
        }
        return false;
    }



    public static Connection getConnection() throws SQLException {



        String jdbcUrl = "jdbc:mysql://localhost:3306/gringottsbank";



        String username = "root";
        String password = "";

        return DriverManager.getConnection(jdbcUrl, username, password);
    }
    public static Connection getConnection(String jdbcUrl) throws SQLException {


        String username = "root";
        String password = "";

        return DriverManager.getConnection(jdbcUrl, username, password);
    }


}
