package org.crochet.config;

import lombok.RequiredArgsConstructor;
import org.crochet.constant.AppConstant;
import org.crochet.properties.AuthorizeHttpRequestProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthorizeHttpRequestProperties authorizeHttpRequestProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(authorizeHttpRequestProperties.getAllowedOrigins())
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(AppConstant.MAX_AGE_SECS);
    }
}
