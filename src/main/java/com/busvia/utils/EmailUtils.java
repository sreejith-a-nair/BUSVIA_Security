package com.busvia.utils;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class EmailUtils {


    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String toEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("anairsreejith1998@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);  // Set the body as HTML

            mailSender.send(message);

            System.out.println("Mail sent successfully.......");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to send email");
        }
    }


}
