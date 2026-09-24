package com.siec_acc.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String ADMIN_TOKEN = "admin_token";

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @Qualifier("adminUserDetailsService")
    private UserDetailsService adminUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        logger.trace("JwtAuthenticationFilter processing request: {} {}", request.getMethod(), requestURI);

        String jwt = extractTokenFromCookie(request);

        if (jwt == null) {
            jwt = extractTokenFromHeader(request);
        }

        String username = null;
        if (jwt != null) {
            try {
                username = jwtUtil.extractUsername(jwt);
                logger.debug("Extracted username from token: {}", username);
            } catch (Exception e) {
                logger.warn("Failed to extract username from token: {}", e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("Attempting to authenticate user: {} for path: {}", username, requestURI);

            try {
                UserDetails userDetails = adminUserDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    logger.info("Successfully authenticated user: {} via JWT", username);
                } else {
                    logger.warn("JWT token validation failed for user: {}", username);
                }

            } catch (Exception e) {
                logger.warn("Authentication failed for user {}: {}", username, e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (ADMIN_TOKEN.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}