package org.crochet.config;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OkHttpConfig {
    @Value("${paypal.username:username not found}")
    private String username;

    @Value("${paypal.password:password not found}")
    private String password;

    @Bean
    public OkHttpClient client() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .authenticator((route, response) -> {
                    if (response.request().header("Authorization") != null) {
                        return null; // Give up, we've already attempted to authenticate.
                    }

                    System.out.println("Authenticating for response: " + response);
                    System.out.println("Challenges: " + response.challenges());
                    String credential = Credentials.basic(username, password);
                    return response.request().newBuilder()
                            .header("Authorization", credential)
                            .build();
                })
                .build();
        return  okHttpClient;
    }
}
