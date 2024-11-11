package org.crochet.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(2)
public class SecurityLoggingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(SecurityLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        logger.info("Security Event - Path: {}, Method: {}, User: {}, Auth: {}",
                request.getRequestURI(),
                request.getMethod(),
                auth != null ? auth.getName() : "anonymous",
                auth != null ? auth.getAuthorities() : "none");

        filterChain.doFilter(request, response);
    }
}
