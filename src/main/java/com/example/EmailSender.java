package com.example;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Random;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {
    private static final String SENDER_EMAIL = "gringotts0000@gmail.com"; // Update with your email address
    private static final String SENDER_PASSWORD = "ankh jllj uwoa hyif"; // Update with your email password

    public static boolean sendEmail(String recipientEmail, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            System.out.println("Email sent successfully.");
            return true;
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            return false;
        }
    }

    public static void sendEmailWithAttachments(String recipientEmail, String subject, String body, String attachmentImagePath){
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // Authenticate sender's email and password
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        };

        // Create a session with the SMTP server
        Session session = Session.getInstance(props, auth);

        try {
            // Create a MIME message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);

            // Create the message body part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);

            // Create the image attachment part
            MimeBodyPart imageAttachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(attachmentImagePath);
            imageAttachmentPart.setDataHandler(new DataHandler(source));
            imageAttachmentPart.setFileName(new File(attachmentImagePath).getName());

            // Create a multipart message
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(imageAttachmentPart);

            // Set the multipart as the message content
            message.setContent(multipart);

            // Send the email
            Transport.send(message);

            System.out.println("Email sent successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Failed to send email.");
        }
    }

    public static String generateOTP() {
        // Generate a 6-digit OTP
        int otp = 100000 + new Random().nextInt(900000);
        return String.valueOf(otp);
    }

    public static LocalDateTime generateOTPTime() {
        LocalDateTime otpTime = LocalDateTime.now();
        return otpTime;
    }
}
