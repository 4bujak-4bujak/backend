package com.example.sabujak.common.email.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@AllArgsConstructor
@Getter
@ConfigurationProperties(prefix = "spring.mail")
public class EmailProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private Properties properties;

    @AllArgsConstructor
    @Getter
    public static class Properties {
        private Smtp smtp;

        @AllArgsConstructor
        @Getter
        public static class Smtp {
            private boolean auth;
            private boolean starttlsEnable;
            private boolean starttlsRequired;
            private int connectionTimeout;
            private int timeout;
            private int writeTimeout;
        }
    }
}