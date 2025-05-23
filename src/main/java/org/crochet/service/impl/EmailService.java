package org.crochet.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.crochet.enums.ResultCode;
import org.crochet.exception.IllegalStateException;
import org.crochet.service.EmailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * EmailService class
 */
@Service
@Slf4j
public class EmailService implements EmailSender {
    private final JavaMailSender javaMailSender;

    /**
     * Constructor
     *
     * @param javaMailSender JavaMailSender
     */
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Send email
     *
     * @param to      user's email
     * @param subject subject
     * @param content content
     * @throws IllegalStateException failed to send email
     * @throws RuntimeException      unsupported encoding
     */
    @Override
    @Async
    public void send(String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(content, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("thamphuong.crochet@gmail.com", "Little Crochet");
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error(ResultCode.MSG_FAILED_SEND_EMAIL.message(), e);
            throw new IllegalStateException(ResultCode.MSG_FAILED_SEND_EMAIL.message(),
                    ResultCode.MSG_FAILED_SEND_EMAIL.code());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

