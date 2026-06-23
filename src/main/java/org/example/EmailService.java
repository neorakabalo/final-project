package org.example;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailService {

    // נקראת לאחר בחירת אימות במייל; שולחת לנמען את קוד האימות שנוצר בצ'אט.
    // אינה מחזירה ערך, ובכשל מעבירה חריגה כדי שממשק הצ'אט יבקש לנסות שוב.
    public void sendVerificationCode(String recipientEmail, String code) throws MessagingException {
        // פרטי ההתחברות נקראים ממשתני סביבה כדי שלא יישמרו בקוד המקור.
        String username = System.getenv("MAIL_USERNAME");
        String password = System.getenv("MAIL_PASSWORD");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalStateException("MAIL_USERNAME and MAIL_PASSWORD must be set");
        }

        // הגדרות SMTP של Gmail עם אימות וחיבור TLS.
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // בניית הודעת המייל עם הנמען וקוד האימות בן שש הספרות.
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Verification Code");
        message.setText("Your verification code is: " + code);

        Transport.send(message);
    }
}
