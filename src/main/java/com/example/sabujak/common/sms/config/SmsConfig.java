package com.example.sabujak.common.sms.config;

import com.example.sabujak.common.sms.properties.SmsProperties;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SmsConfig {
    private final SmsProperties properties;

    @Bean
    public DefaultMessageService defaultMessageService() {
        return NurigoApp.INSTANCE.initialize(properties.getKey(), properties.getSecret(), "https://api.coolsms.co.kr");
    }
}
