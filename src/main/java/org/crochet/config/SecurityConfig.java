package org.crochet.config;

import lombok.extern.slf4j.Slf4j;
import org.crochet.security.CustomUserDetailsService;
import org.crochet.security.RestAuthenticationEntryPoint;
import org.crochet.security.TokenAuthenticationFilter;
import org.crochet.security.oauth2.CustomOAuth2UserService;
import org.crochet.security.oauth2.OAuth2CookieRepository;
import org.crochet.security.oauth2.OAuth2AuthenticationFailureHandler;
import org.crochet.security.oauth2.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.springframework.security.config.Customizer.withDefaults;

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

  @Autowired
  public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                        CustomOAuth2UserService customOAuth2UserService,
                        OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
                        OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler,
                        PasswordEncoder passwordEncoder) {
    this.customUserDetailsService = customUserDetailsService;
    this.customOAuth2UserService = customOAuth2UserService;
    this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
    this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
    this.passwordEncoder = passwordEncoder;
  }

  @Bean
  public TokenAuthenticationFilter tokenAuthenticationFilter() {
    return new TokenAuthenticationFilter();
  }

  /*
    By default, Spring OAuth2 uses HttpSessionOAuth2AuthorizationRequestRepository to save
    the authorization request. But, since our service is stateless, we can't save it in
    the session. We'll save the request in a Base64 encoded cookie instead.
  */
  @Bean
  public OAuth2CookieRepository cookieAuthorizationRequestRepository() {
    return new OAuth2CookieRepository();
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
        .cors(withDefaults())
        .sessionManagement(sessionManagementCustomizer -> sessionManagementCustomizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(new RestAuthenticationEntryPoint()))
        .authorizeHttpRequests(authReq -> authReq.requestMatchers("/",
                "/error",
                "/favicon.ico",
                "/*.png",
                "/*.gif",
                "/*.svg",
                "/*.jpg",
                "/*.html",
                "/*.css",
                "/*.js").permitAll()
            .requestMatchers("/auth/**", "/oauth2/**").permitAll()
            .anyRequest().authenticated())
        .oauth2Login(oauth -> oauth.authorizationEndpoint(authEndpointCustomizer ->
                authEndpointCustomizer
                    .baseUri("/oauth2/authorize")
                    .authorizationRequestRepository(cookieAuthorizationRequestRepository()))
            .redirectionEndpoint(redirectionEndpointCustomizer ->
                redirectionEndpointCustomizer.baseUri("/oauth2/callback/*"))
            .userInfoEndpoint(userInfoEndpointCustomizer -> userInfoEndpointCustomizer.userService(customOAuth2UserService))
            .successHandler(oAuth2AuthenticationSuccessHandler)
            .failureHandler(oAuth2AuthenticationFailureHandler))
        .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .authenticationProvider(authenticationProvider())
        .build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
}