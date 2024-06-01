package com.example.sabujak.common.sms.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@AllArgsConstructor
@Getter
@ConfigurationProperties(prefix = "coolsms.api")
public class SmsProperties {
    private String key;
    private String secret;
}
