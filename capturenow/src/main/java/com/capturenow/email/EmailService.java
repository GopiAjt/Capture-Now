package com.capturenow.email;

import lombok.AllArgsConstructor;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.capturenow.module.Customer;
import com.capturenow.module.Photographer;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class EmailService {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Async
    public void sendToCustomer(String to, Customer c) {
    	
    	c.setSignupVerificationKey(otpGanaretor());
    	
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText("Your otp to verify your email is "+ c.getSignupVerificationKey(), true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("capturenow.io@gmail.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }
    
    @Async
    public void sendToPhotographer(String to, Photographer p) {
    	
    	p.setSignupVerificationKey(otpGanaretor());
    	
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText("Your otp to verify your email is "+ p.getSignupVerificationKey(), true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("capturenow.io@gmail.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }
    
    public static int otpGanaretor()
    {
    	Random r = new Random();
    	int otp = r.nextInt(1000, 9999);
    	return otp;
    }
}
