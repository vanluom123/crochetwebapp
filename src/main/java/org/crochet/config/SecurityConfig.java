package org.crochet.config;

import lombok.extern.slf4j.Slf4j;

import org.crochet.constant.AppConstant;
import org.crochet.properties.AuthorizeHttpRequestProperties;
import org.crochet.security.CustomUserDetailsService;
import org.crochet.security.TokenAuthenticationFilter;
import org.crochet.security.oauth2.CustomOAuth2UserService;
import org.crochet.security.oauth2.OAuth2AuthenticationFailureHandler;
import org.crochet.security.oauth2.OAuth2AuthenticationSuccessHandler;
import org.crochet.security.oauth2.OAuth2CookieRepository;
import org.crochet.security.RestAuthenticationEntryPoint;
import org.crochet.security.RestAccessDeniedHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true
)
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2CookieRepository oAuth2CookieRepository;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          CustomOAuth2UserService customOAuth2UserService,
                          OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
                          OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler,
                          PasswordEncoder passwordEncoder,
                          OAuth2CookieRepository oAuth2CookieRepository,
                          TokenAuthenticationFilter tokenAuthenticationFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
        this.passwordEncoder = passwordEncoder;
        this.oAuth2CookieRepository = oAuth2CookieRepository;
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
    }

    @Bean
    @ConfigurationProperties(prefix = "authorize.http-request")
    AuthorizeHttpRequestProperties authorizeHttpRequestProperties() {
        return new AuthorizeHttpRequestProperties();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                    .xssProtection(HeadersConfigurer.XXssConfig::disable)
                    .contentSecurityPolicy(csp -> 
                        csp.policyDirectives("default-src 'self'; frame-ancestors 'none';"))
                    .referrerPolicy(referrer -> 
                        referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                    .permissionsPolicy(permissions -> permissions.policy(
                        "camera=(), microphone=(), geolocation=(), payment=()"
                    ))
                )
                .exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                    .accessDeniedHandler(new RestAccessDeniedHandler())
                )
                .authorizeHttpRequests(authReq -> authReq
                        .requestMatchers(authorizeHttpRequestProperties().getAuthenticated()).authenticated()
                        .anyRequest().permitAll())
                .oauth2Login(oauth -> oauth.authorizationEndpoint(authEndpointCustomizer ->
                                authEndpointCustomizer
                                        .baseUri("/oauth2/authorize")
                                        .authorizationRequestRepository(oAuth2CookieRepository))
                        .redirectionEndpoint(redirectionEndpointCustomizer ->
                                redirectionEndpointCustomizer.baseUri("/oauth2/callback/*"))
                        .userInfoEndpoint(userInfoEndpointCustomizer -> 
                            userInfoEndpointCustomizer.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler))
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            authorizeHttpRequestProperties().getAllowedOrigins()
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(AppConstant.MAX_AGE_SECS);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
