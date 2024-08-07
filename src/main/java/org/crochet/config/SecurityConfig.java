package org.crochet.config;

import lombok.extern.slf4j.Slf4j;
import org.crochet.properties.AuthorizeHttpRequestProperties;
import org.crochet.security.CustomUserDetailsService;
import org.crochet.security.RestAuthenticationEntryPoint;
import org.crochet.security.TokenAuthenticationFilter;
import org.crochet.security.oauth2.CustomOAuth2UserService;
import org.crochet.security.oauth2.OAuth2AuthenticationFailureHandler;
import org.crochet.security.oauth2.OAuth2AuthenticationSuccessHandler;
import org.crochet.security.oauth2.OAuth2CookieRepository;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
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
    public AuthorizeHttpRequestProperties authorizeHttpRequestProperties() {
        return new AuthorizeHttpRequestProperties();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .sessionManagement(sessionManagementCustomizer -> sessionManagementCustomizer.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(
                        new RestAuthenticationEntryPoint()))
                .authorizeHttpRequests(authReq -> authReq
                        .requestMatchers(authorizeHttpRequestProperties().getAuthenticated()).authenticated()
                        .anyRequest().permitAll())
                .oauth2Login(oauth -> oauth.authorizationEndpoint(authEndpointCustomizer ->
                                authEndpointCustomizer
                                        .baseUri("/oauth2/authorize")
                                        .authorizationRequestRepository(oAuth2CookieRepository))
                        .redirectionEndpoint(redirectionEndpointCustomizer ->
                                redirectionEndpointCustomizer.baseUri("/oauth2/callback/*"))
                        .userInfoEndpoint(userInfoEndpointCustomizer -> userInfoEndpointCustomizer.userService(
                                customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler))
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
