package com.example.sabujak.fcm.config;

import com.example.sabujak.common.exception.CommonException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

import static com.example.sabujak.common.exception.CommonErrorCode.COMMON_SYSTEM_ERROR;
import static com.google.firebase.FirebaseApp.DEFAULT_APP_NAME;

@Slf4j
@Configuration
public class FCMConfig {

    @Value("${fcm.key.path}")
    private String path;

    @Value("${fcm.key.scope}")
    private String scope;

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance(getFirebaseApp());
    }

    private FirebaseApp getFirebaseApp() {
        return FirebaseApp.getApps().stream()
                .filter(app -> app.getName().equals(DEFAULT_APP_NAME))
                .findFirst()
                .orElseGet(this::createFirebaseApp);
    }

    private FirebaseApp createFirebaseApp() {
        try {
            GoogleCredentials credentials = createGoogleCredentials();
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();
            FirebaseApp app = FirebaseApp.initializeApp(options);
            log.info("FirebaseApp initialized. App Name: [{}]", app.getName());
            return app;
        } catch (IOException e) {
            throw new CommonException(COMMON_SYSTEM_ERROR);
        }
    }

    private GoogleCredentials createGoogleCredentials() throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        InputStream credentialsStream = resource.getInputStream() ;
        return GoogleCredentials
                .fromStream(credentialsStream)
                .createScoped(scope);
    }
}
