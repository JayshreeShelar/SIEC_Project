package com.siec_acc.security;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AdminUserDetailsService adminUserDetailsService;
//    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          AdminUserDetailsService adminUserDetailsService
                        ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.adminUserDetailsService = adminUserDetailsService;
        logger.info("SecurityConfig bean initialized");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(adminUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean("adminAuthenticationManager")
    public AuthenticationManager adminAuthenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(adminUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

//    @Bean("userAuthenticationManager")
//    public AuthenticationManager userAuthenticationManager() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsServiceImpl);
//        provider.setPasswordEncoder(passwordEncoder());
//        return new ProviderManager(provider);
//    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173", "http://localhost:5500" , "http://127.0.0.1:5503",
                "https://terrapureenvironment.com","https://tpe-api.terrapureenvironment.com/",
                "https://admin.terrapureenvironment.com/","https://www.terrapureenvironment.com/"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/api/admin/bootstrap").permitAll()
                            .requestMatchers("/api/admin/auth/**").permitAll()
                            .requestMatchers("/api/users/auth/**").permitAll()
                            .requestMatchers("/api/admin/**").authenticated()
                            .anyRequest().permitAll();
                })
                // ❌ OAuth2 Login REMOVED
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Unauthorized - Login required\"}");
                            logger.warn("Unauthorized access attempt: {}", request.getRequestURI());
                        })
                );

        logger.info("SecurityFilterChain configured successfully (OAuth2 removed)");
        return http.build();
    }
}