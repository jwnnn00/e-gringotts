package com.example;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.example.model.*;
import org.mindrot.jbcrypt.BCrypt;

public class Database {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/gringottsbank";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static final String INSERT_USER_QUERY = "INSERT INTO user (username, fullName, email, password, DOB, address, phoneNumber, userType, avatarImagePath, currency ,balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?)";
    private static final String INSERT_CARD_QUERY = "INSERT INTO card (userId, cardNum, cvv, expiryDate, cardType) VALUES (?, ?, ?, ?, ?)";

    public static int getNumberOfUsersByType(UserType userType) {
        int count = 0;
        // Perform a database query to retrieve the count of users by userType

        String query = "SELECT COUNT(*) FROM user WHERE userType = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userType.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any potential exceptions, such as database connection errors
        }

        return count;
    }




    public static boolean addConversionRate(String currency, String toCurrency, double exchangeRate, double processingFee) {
        String insertSQL = "INSERT INTO conversion (currency, toCurrency, exchangeRate, processingFee) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, currency);
            pstmt.setString(2, toCurrency);
            pstmt.setDouble(3, exchangeRate);
            pstmt.setDouble(4, processingFee);

            pstmt.executeUpdate();
            System.out.println("New conversion rate added successfully");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding new conversion rate: " + e.getMessage());
        }
        return false;
    }

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
            userStatement.setDouble(11,account.getBalance());

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

    public static Account<?> getUserByUsername(String username) {
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
                    String currency = resultSet.getString("currency").toUpperCase();
                    double balance = resultSet.getDouble("balance");

                    // Create and return the Account object
                    return new Account<>(userId, username, fullName, email, password, dateOfBirth, address, phoneNumber, userType, new UserAvatar(avatarPath, userId), currency,balance);
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

    public static void updateUserPassword(Account<?> account) {
        String updatePasswordQuery = "UPDATE user SET password = ? WHERE userId = ?";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(updatePasswordQuery)) {

            statement.setString(1, account.getPassword());
            statement.setLong(2, account.getUserId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("Failed to update password: No rows affected.");
            } else {
                System.out.println("Password updated successfully!");
            }

        } catch (SQLException e) {
            System.err.println("Failed to update password: " + e.getMessage());
        }
    }

//    public static boolean login(String username, String password) {
//        System.out.println("Login");
//
//        // Check if the username exists in the database
//        Database db = new Database();
//        Account<?> userAccount = db.getUserByUsername(username);
//
//        if (userAccount != null) {
//            // Username exists
////            if (userAccount.authenticate(password)) {
//            if (BCrypt.checkpw(password, userAccount.getPassword())) {
//                System.out.println("Login successful!");
//                return true;
//            } else {
//                System.out.println("Invalid password.");
//            }
//        } else {
//            // Username not found in the database
//            System.out.println("User not found.");
//        }
//        return false;
//
////        String storedPasswordHash = userAccount.getPassword();
////        System.out.println("Stored password hash: " + storedPasswordHash);
////
////        // Check if the stored password appears to be a BCrypt hash
////        if (storedPasswordHash != null && storedPasswordHash.startsWith("$2a$")) {
////            if (BCrypt.checkpw(password, storedPasswordHash)) {
////                System.out.println("Login successful!");
////                return true;
////            } else {
////                System.out.println("Invalid password.");
////            }
////        } else {
////            System.out.println("Stored password does not appear to be a BCrypt hash.");
////        }
////    } else {
////        // Username not found in the database
////        System.out.println("User not found.");
////    }
////    return false;
//    }

    public static Card getCardDetails(long userId) {
        Card card = null;
        String query = "SELECT * FROM card WHERE userId = ?";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String cardNum = resultSet.getString("cardNum");
                int cvv = resultSet.getInt("cvv");
                Date expiryDate = resultSet.getDate("expiryDate");
                // You might need to convert expiryDate to a java.util.Date or java.time.LocalDate if it's stored differently in your database
                // Similarly, ensure the data types and column names match your database schema

                // Assuming cardType is stored as an enum or string in your database
                String cardTypeString = resultSet.getString("cardType");
                Card.CardType cardType = Card.CardType.valueOf(cardTypeString);

                card = new Card(cardNum, cvv, expiryDate, userId, cardType);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any SQL exceptions
        }

        return card;
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

    public static Map<String, Map<String, Object>> fetchConversionRates() {
        Map<String, Map<String, Object>> conversionRates = new HashMap<>();

        String query = "SELECT * FROM conversion";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String currencyFrom = resultSet.getString("currency");
                String toCurrency = resultSet.getString("toCurrency");
                double exchangeRate = resultSet.getDouble("exchangeRate");
                double processingFee = resultSet.getDouble("processingFee");

                Map<String, Object> conversionMap = new HashMap<>();
                conversionMap.put("toCurrency", toCurrency);
                conversionMap.put("exchangeRate", exchangeRate);
                conversionMap.put("processingFee", processingFee);

                conversionRates.put(currencyFrom, conversionMap);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching conversion rates: " + e.getMessage());
        }

        return conversionRates;
    }

    // Other methods...

    public static void updateConversionRates(String currency, String toCurrency, double exchangeRate, double processingFee) {
        String updateQuery = "UPDATE conversion SET toCurrency = ?, exchangeRate = ?, processingFee = ? WHERE currency = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, toCurrency);
            preparedStatement.setDouble(2, exchangeRate);
            preparedStatement.setDouble(3, processingFee);
            preparedStatement.setString(4, currency);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Conversion rates updated successfully!");
            } else {
                System.out.println("Failed to update conversion rates.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating conversion rates: " + e.getMessage());
        }
    }


        public static int getNumberOfUsers() {
            int numberOfUsers = 0;
            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS total FROM user WHERE usertype != 'GOBLIN'")) { // Assuming 'GOBLIN' is one of the enum values
                if (resultSet.next()) {
                    numberOfUsers = resultSet.getInt("total");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return numberOfUsers;
        }




}