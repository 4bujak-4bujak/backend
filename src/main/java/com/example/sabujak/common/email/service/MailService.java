package com.example.sabujak.common.email.service;

import com.example.sabujak.common.exception.CommonException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.example.sabujak.common.exception.CommonErrorCode.SENDING_MAIL_FAIL;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String toEmail, String title, String text) {
        SimpleMailMessage emailForm = createSimpleEmailForm(toEmail, title, text);
        try {
            mailSender.send(emailForm);
        } catch (Exception e) {
            log.error("[Exception] Message: {}", e.getMessage(), e);
            throw new CommonException(SENDING_MAIL_FAIL);
        }
    }

    @Async
    public void sendEmail(MimeMessage message) {
        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error("[Exception] Message: {}", e.getMessage(), e);
            throw new CommonException(SENDING_MAIL_FAIL);
        }
    }

    public MimeMessage createEmptyMimeMessage() {
        return mailSender.createMimeMessage();
    }

    private SimpleMailMessage createSimpleEmailForm(String toEmail, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);

        return message;
    }
}
