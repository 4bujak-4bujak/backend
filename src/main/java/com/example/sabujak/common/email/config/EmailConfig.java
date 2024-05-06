package com.example.sabujak.common.email.config;

import com.example.sabujak.common.email.properties.EmailProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class EmailConfig {

    private final EmailProperties emailProperties;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailProperties.getHost());
        mailSender.setPort(emailProperties.getPort());
        mailSender.setUsername(emailProperties.getUsername());
        mailSender.setPassword(emailProperties.getPassword());
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setJavaMailProperties(getMailProperties());

        return mailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", emailProperties.getProperties().getSmtp().isAuth());
        properties.put("mail.smtp.starttls.enable", emailProperties.getProperties().getSmtp().isStarttlsEnable());
        properties.put("mail.smtp.starttls.required", emailProperties.getProperties().getSmtp().isStarttlsRequired());
        properties.put("mail.smtp.connectiontimeout", emailProperties.getProperties().getSmtp().getConnectionTimeout());
        properties.put("mail.smtp.timeout", emailProperties.getProperties().getSmtp().getTimeout());
        properties.put("mail.smtp.writetimeout", emailProperties.getProperties().getSmtp().getWriteTimeout());

        return properties;
    }
}
