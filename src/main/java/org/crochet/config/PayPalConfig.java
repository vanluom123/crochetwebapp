package org.crochet.config;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.crochet.properties.PayPalProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class PayPalConfig {

    @Bean
    @ConfigurationProperties(prefix = "paypal")
    public PayPalProperties payPalProperties() {
        return new PayPalProperties();
    }

    @Bean
    @Primary
    public PayPalHttpClient payPalHttpClient() {
        return new PayPalHttpClient(
                new PayPalEnvironment.Sandbox(
                        payPalProperties().getUsername(),
                        payPalProperties().getPassword()
                )
        );
    }
}