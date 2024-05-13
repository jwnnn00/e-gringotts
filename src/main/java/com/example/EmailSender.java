//package com.example;
//
//import javax.mail.*;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//import java.util.Properties;
//import java.util.Random;
//
//public class EmailSender {
//
//
//    private static final String USERNAME = "jingwen0421@gmail.com";
//    private static final String PASSWORD = "11282129";
//
//    public static void sendOtpEmail(String recipientEmail) {
//        // Generate OTP
//        String otp = generateOtp();
//
//        // Store OTP in database (you'll need to implement this)
//
//        // Compose email message
//        String subject = "Your One-Time Password (OTP)";
//        String body = "Your OTP is: " + otp;
//
//        // Send email
//        sendEmail(recipientEmail, subject, body);
//    }
//
//    private static void sendEmail(String recipientEmail, String subject, String body) {
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", "smtp@gmail.com");
//        props.put("mail.smtp.port", "587");
//
//        Session session = Session.getInstance(props, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(USERNAME, PASSWORD);
//            }
//        });
//
//        try {
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(USERNAME));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
//            message.setSubject(subject);
//            message.setText(body);
//
//            Transport.send(message);
//
//            System.out.println("Email sent successfully!");
//        } catch (MessagingException e) {
//            System.err.println("Error sending email: " + e.getMessage());
//        }
//    }
//
//    private static String generateOtp() {
//        // Generate a 6-digit OTP
//        Random random = new Random();
//        int otpValue = 100000 + random.nextInt(900000);
//        return String.valueOf(otpValue);
//    }
//
//    // Additional methods for storing OTP in database and verifying it
//}