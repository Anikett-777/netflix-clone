package com.netflix.clone.serviceImpl;

import com.netflix.clone.exceptions.EmailNotVerifiedException;
import com.netflix.clone.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url: http://localhost:4200}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(String toEmail, String token) {

        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Netflix Clone - Verify Your Email..");

            String verificationLink = frontendUrl + "/verify-email?token="+token;

            String emailBody =
                    "Welcome to Netflix Clone!\n\n"
                    + "Thanks For Registering. Please Verify Your Email Address By Clicking The Link Below:\n\n"
                    + verificationLink
                    + "\n\n"
                    + "This Link Will Expire in 24 Hours.\n\n"
                    + "If you didn't create this account, please ignore this email.\n\n"
                    + "Best Regards, \n"
                    + "NetFlix Clone Team";

            message.setText(emailBody);
            mailSender.send(message);
            logger.info("Verification email send verification email to {} : {}",toEmail);

        }catch (Exception e){
            logger.error("Failed to send Verification email to {}:{}",toEmail,e.getMessage(),e);
            throw new EmailNotVerifiedException("Failed To Send Verification email");
        }

    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String token) {

        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Netflix Clone - Password Reset");

            String resetLink = frontendUrl + "/reset/password?token="+token;
            String emailBody=
                    "We Received a request to reset your password. CLick the link below to reset it :\n\n"
                    + resetLink
                    +"\n\n"
                    +"This Link Will Expire in 1 Hours.\n\n"
                    +"If you didn't create this account, please ignore this email.\n\n"
                            + "Best Regards, \n"
                            + "NetFlix Clone Team";
            message.setText(emailBody);
            mailSender.send(message);
            logger.info("Password reset email sent to {} ",toEmail);


        }catch (Exception e){
            logger.error("Failed to send password email to {} : {}",toEmail,e.getMessage());
            throw new RuntimeException("Failed To Send Password Reset Email");
        }
    }
}
