package com.giovaniwahl.dscatalog.services;

import com.giovaniwahl.dscatalog.services.exceptions.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailService {
    @Value("${spring.mail.username}")
    private String emailFrom;
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to,String subject,String body){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        }
        catch (MailException e){
            throw new EmailException("Failed to send e-mail");
        }
    }
}
