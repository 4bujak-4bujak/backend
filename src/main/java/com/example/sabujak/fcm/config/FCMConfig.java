package com.example.sabujak.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.google.firebase.FirebaseApp.DEFAULT_APP_NAME;

@Slf4j
@Configuration
public class FCMConfig {

    @Value("${fcm.private.key.path}")
    private String path;

    @Value("${fcm.private.key.scope}")
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
        GoogleCredentials credentials = createGoogleCredentials();
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();
        return FirebaseApp.initializeApp(options);
    }

    private GoogleCredentials createGoogleCredentials() {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            InputStream credentialsStream = resource.getInputStream();

            InputStreamReader reader = new InputStreamReader(credentialsStream);
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            for (String key : jsonObject.keySet()) {
                log.info("Key: " + key + ", Value: " + jsonObject.get(key));
            }

            credentialsStream = resource.getInputStream();

            return GoogleCredentials
                    .fromStream(credentialsStream)
                    .createScoped(scope);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
