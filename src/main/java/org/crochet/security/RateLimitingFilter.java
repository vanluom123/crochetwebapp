package org.crochet.security;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Bucket bucket;

    public RateLimitingFilter(Bucket bucket) {
        this.bucket = bucket;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!bucket.tryConsume(1)) {
            response.setStatus(HttpStatus.SC_TOO_MANY_REQUESTS);
            response.getWriter().write("Too many requests");
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Rate limiting won't be applied to some endpoints
        String path = request.getRequestURI();
        return path.startsWith("/public/") ||
                path.startsWith("/actuator/health") ||
                path.equals("/error");
    }
}
