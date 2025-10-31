package com.library.librarymanagement.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableAsync
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendAccountInfor(String toEmail, String username, String password){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("FPT Electric Library: Information about your account");
        message.setText("Dear you,\n\n" +
                "Your account has already created successfully\n" +
                "Username: " + username + "\n" +
                "Password: " + password + "\n" +
                "Please login by this account to access system\n\n" +
                "Sincerely,\nFPT Electric Library");
        mailSender.send(message);
    }

}
