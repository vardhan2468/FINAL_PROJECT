package com.learnsphere.lms.config;

import com.learnsphere.lms.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    // Constructor injection
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
            CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for REST API (token-based auth)
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) // Enable CORS
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny()) // Prevent clickjacking
                        .xssProtection(xss -> xss.disable()) // XSS protection handled by browser
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data:")) // CSP
                )
                .authorizeHttpRequests(auth -> auth
                        // Public resources
                        .requestMatchers("/", "/index.html", "/register.html", "/courses.html").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        // API endpoints
                        .requestMatchers("/api/auth/**").permitAll() // Allow authentication endpoints
                        .requestMatchers("/api/users/register").permitAll() // Allow registration
                        .requestMatchers(HttpMethod.POST, "/api/courses/add").hasRole("ADMIN") // Only ADMIN can add
                                                                                               // courses
                        .requestMatchers(HttpMethod.PUT, "/api/courses/**").hasRole("ADMIN") // Only ADMIN can update
                                                                                             // courses
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**").hasRole("ADMIN") // Only ADMIN can delete
                                                                                                // courses
                        .requestMatchers(HttpMethod.GET, "/api/courses/**").authenticated() // All authenticated users
                                                                                            // can view courses
                        .requestMatchers("/api/enrollments/enroll").hasRole("STUDENT") // Only STUDENT can enroll
                        .requestMatchers("/api/enrollments/**").authenticated() // All authenticated users can view
                                                                                // enrollments
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
