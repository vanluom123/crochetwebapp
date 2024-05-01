package org.crochet.config;

import org.crochet.enumerator.RoleType;
import org.crochet.model.User;
import org.crochet.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminConfig {
    @Bean
    public CommandLineRunner createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Create admin user
            String adminEmail = "dothithamphuong@gmail.com";
            String adminPassword = "admin";
            // Encode password
            String encodedPassword = passwordEncoder.encode(adminPassword);
            // Check if admin user exists
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                // Create admin user
                User admin = User.builder()
                        .email(adminEmail)
                        .emailVerified(true)
                        .password(encodedPassword)
                        .name("Admin")
                        .role(RoleType.ADMIN)
                        .build();
                userRepository.save(admin);
            }
        };
    }
}
