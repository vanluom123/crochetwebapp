package org.crochet.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
@Configuration
public class FirebaseConfig {

  @Bean
  public FirebaseApp firebaseApp() throws IOException {
    InputStream is = new ClassPathResource("serviceAccount.json").getInputStream();

    FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(is))
        .build();

    return FirebaseApp.initializeApp(options);
  }

  @Bean
  public StorageClient storageClient(FirebaseApp firebaseApp) {
    return StorageClient.getInstance(firebaseApp);
  }
}

