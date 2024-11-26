package org.crochet.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@RequiredArgsConstructor
@Configuration
public class FirebaseConfig {

    @Value("${firebase.serviceAccountKey}")
    private String serviceAccountKey;

    @Bean
    FirebaseApp firebaseApp() throws IOException {
        byte[] decodedKey = Base64.getDecoder().decode(serviceAccountKey);
        ByteArrayInputStream is = new ByteArrayInputStream(decodedKey);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(is))
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    StorageClient storageClient(FirebaseApp firebaseApp) {
        return StorageClient.getInstance(firebaseApp);
    }
}

