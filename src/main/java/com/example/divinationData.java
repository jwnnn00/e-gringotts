package com.example;

import com.example.Transaction;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;


public class divinationData {
    //private final List<Transaction> transactions;
    private static final String URL = "jdbc:mysql://localhost:3307/gringottsbank";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static class DatabaseManager {
        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
    }

    private static void recordTransaction(Connection conn, Long userID, double amount, LocalDate date, String category, String paymentMethod) throws SQLException {
        String sql = "INSERT INTO Transaction (userID, amount, transaction_date, category, payment_method) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userID);
            stmt.setDouble(2, amount);
            stmt.setDate(3, java.sql.Date.valueOf(date));
            stmt.setString(4, category);
            stmt.setString(5, paymentMethod);
            stmt.executeUpdate();
        }
    }

//    private static void calculateAndDisplayExpenditures(Connection conn, Long userID) throws SQLException {
//        String sql = "SELECT category, SUM(amount) AS total_amount FROM transactions WHERE userID = ? GROUP BY category";
//        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setLong(1, userID);
//            ResultSet rs = stmt.executeQuery();
//
//            System.out.println("Expenditure Breakdown for User: " + userID);
//            while (rs.next()) {
//                String category = rs.getString("category");
//                double totalAmount = rs.getDouble("total_amount");
//                System.out.printf("%s: %.2f\n", category, totalAmount);
//            }
//        }
//    }

    public static Map<String, Double> calculateExpenditures(Connection conn, Long userID) throws SQLException {
        String sql = "SELECT category, SUM(amount) AS total_amount FROM Transaction WHERE userID = ? GROUP BY category";
        Map<String, Double> expenditures = new HashMap<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String category = rs.getString("category");
                double totalAmount = rs.getDouble("total_amount");
                expenditures.put(category, totalAmount);
            }
        }
        return expenditures;
    }

    //Use MySQL to track by using userId
    public List<Transaction> getTransactionsByUserId(Long userId) throws SQLException{
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT FROM transaction  WHERE userId = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double amount = rs.getDouble("amount");
                LocalDate transaction_date = rs.getDate("transaction_date").toLocalDate();
                String category = rs.getString("category");
                String paymentMethod = rs.getString("paymentMethod");

                Transaction transaction = new Transaction(userId, amount, transaction_date, category, paymentMethod);
                transactions.add(transaction);
            }
        }
        return transactions;
    }

//    public Map<String, Double> calculateExpendituresByCategory(String userId) {
//        Map<String, Double> expendituresByCategory = new HashMap<>();
//
//        String sql = "SELECT category, amount FROM allTransactions WHERE userId = ?";
//
//        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setString(1, userId);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                String category = rs.getString("category");
//                double amount = rs.getDouble("amount");
//
//                // Update expendituresByCategory map with category and amount
//                expendituresByCategory.put(category, expendituresByCategory.getOrDefault(category, 0.0) + amount);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return expendituresByCategory;
//    }

    // Display expenditure breakdown by category
//    public void displayExpenditureBreakdown(String userId) {
//        Map<String, Double> expendituresByCategory = calculateExpendituresByCategory(userId);
//        double totalExpenses = expendituresByCategory.values().stream().mapToDouble(Double::doubleValue).sum();
//
//        System.out.println("Expenditure Breakdown for User: " + userId);
//        for (Map.Entry<String, Double> entry : expendituresByCategory.entrySet()) {
//            String category = entry.getKey();
//            double amount = entry.getValue();
//            double percentage = (amount / totalExpenses) * 100;
//            System.out.printf("%s: %.2f%% of total expenses\n", category, percentage);
//        }
//    }

    // Filter transactions by category from MySQL
    public static void filterTransactionsByCategory(Long userId, String category) {

        String sql = "SELECT * FROM Transaction WHERE userId = ? AND category LIKE ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setString(2, "%" + category + "%"); // Use LIKE for case-insensitive match

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                displayTransaction(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle or log the exception appropriately
        }

    }


    // Filter transactions by date range from MySQL
    public static void filterTransactionsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {

        String sql = "SELECT * FROM Transaction WHERE userId = ? AND transaction_date BETWEEN ? AND ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setDate(2, java.sql.Date.valueOf(startDate));
            stmt.setDate(3, java.sql.Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                displayTransaction(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle or log the exception appropriately
        }

    }

    // Filter transactions by payment method from MySQL
    public static void filterTransactionsByPaymentMethod(Long userId, String paymentMethod) {

        String sql = "SELECT * FROM Transaction WHERE userId = ? AND payment_method = ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setString(2, paymentMethod);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                displayTransaction(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle or log the exception appropriately
        }
    }

    private static void displayTransaction(ResultSet rs) throws SQLException {
        System.out.printf("Amount: %.2f, Date: %s, Category: %s, Payment Method: %s\n",
                rs.getDouble("amount"), rs.getDate("transaction_date").toLocalDate(), rs.getString("category"), rs.getString("payment_method"));
    }

//    public static void main(String[] args) {
//        try (Scanner scanner = new Scanner(System.in);
//             Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
//
//            // Input transaction details from user
//            System.out.println("Enter userID:");
//            Long userID = Long.parseLong(scanner.nextLine());
//
//            System.out.println("Enter amount:");
//            double amount = scanner.nextDouble();
//
//            // Generate current date
//            LocalDate currentDate = LocalDate.now();
//
//            System.out.println("Enter category:");
//            String category = scanner.next();
//
//            System.out.println("Enter payment method:");
//            String paymentMethod = scanner.next();
//
//            // Insert transaction into database
//            recordTransaction(conn, userID, amount, currentDate, category, paymentMethod);
//
//            // Calculate expenditures and display breakdown
//            calculateExpenditures(conn, userID);
//
//            // Prompt user to filter transactions
//            System.out.println("Choose an option:");
//            System.out.println("1. Filter transactions by category");
//            System.out.println("2. Filter transactions by date range");
//            System.out.println("3. Filter transactions by payment method");
//
//            int option = scanner.nextInt();
//            String userInput;
//            switch (option) {
//                case 1:
//                    System.out.println("Please enter category: ");
//                    userInput = scanner.nextLine();
//                    filterTransactionsByCategory(userID,userInput);
//                    break;
//                case 2:
//                    System.out.println("Please enter start date (yyyy-MM-dd): ");
//                    LocalDate startDate = LocalDate.parse(scanner.nextLine());
//                    System.out.println("Please enter end date (yyyy-MM-dd): ");
//                    LocalDate endDate = LocalDate.parse(scanner.nextLine());
//                    filterTransactionsByDateRange(userID, startDate, endDate);
//                    break;
//                case 3:
//                    System.out.println("Please enter payment method: ");
//                    userInput = scanner.nextLine();
//                    filterTransactionsByPaymentMethod(userID, userInput);
//                    break;
//                default:
//                    System.out.println("Invalid option. Exiting...");
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}
