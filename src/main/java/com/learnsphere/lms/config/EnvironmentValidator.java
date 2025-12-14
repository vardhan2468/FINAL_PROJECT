package com.learnsphere.lms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Environment Configuration Validator
 * Validates required environment variables on application startup
 */
@Component
public class EnvironmentValidator {

    @Value("${DB_URL:}")
    private String dbUrl;

    @Value("${DB_USERNAME:}")
    private String dbUsername;

    @Value("${DB_PASSWORD:}")
    private String dbPassword;

    @Value("${JWT_SECRET:}")
    private String jwtSecret;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    /**
     * Validate environment variables on application startup
     */
    @EventListener(ApplicationReadyEvent.class)
    public void validateEnvironment() {
        System.out.println("=================================================");
        System.out.println("   ENVIRONMENT VALIDATION");
        System.out.println("=================================================");
        System.out.println("Active Profile: " + activeProfile);

        // Production profile requires all environment variables
        if ("prod".equals(activeProfile)) {
            validateProductionEnvironment();
        } else {
            validateDevelopmentEnvironment();
        }

        System.out.println("=================================================");
        System.out.println("   ENVIRONMENT VALIDATION PASSED");
        System.out.println("=================================================\n");
    }

    /**
     * Validate production environment requirements
     */
    private void validateProductionEnvironment() {
        System.out.println("\nValidating PRODUCTION environment...");

        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        // Validate Database URL
        if (dbUrl == null || dbUrl.trim().isEmpty()) {
            errors.append("\n  ✗ DB_URL is not set");
            isValid = false;
        } else {
            System.out.println("  ✓ DB_URL configured");
        }

        // Validate Database Username
        if (dbUsername == null || dbUsername.trim().isEmpty()) {
            errors.append("\n  ✗ DB_USERNAME is not set");
            isValid = false;
        } else {
            System.out.println("  ✓ DB_USERNAME configured");
        }

        // Validate Database Password
        if (dbPassword == null || dbPassword.trim().isEmpty()) {
            errors.append("\n  ✗ DB_PASSWORD is not set");
            isValid = false;
        } else {
            System.out.println("  ✓ DB_PASSWORD configured");
        }

        // Validate JWT Secret
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            errors.append("\n  ✗ JWT_SECRET is not set");
            isValid = false;
        } else if (jwtSecret.length() < 32) {
            errors.append("\n  ✗ JWT_SECRET must be at least 32 characters");
            isValid = false;
        } else {
            System.out.println("  ✓ JWT_SECRET configured (length: " + jwtSecret.length() + " chars)");
        }

        if (!isValid) {
            String errorMessage = "\n\n" +
                    "=================================================\n" +
                    "   PRODUCTION ENVIRONMENT VALIDATION FAILED\n" +
                    "=================================================\n" +
                    "Missing required environment variables:" +
                    errors.toString() +
                    "\n\nPlease set the following environment variables:\n" +
                    "  export DB_URL='jdbc:mysql://host:port/database'\n" +
                    "  export DB_USERNAME='your_username'\n" +
                    "  export DB_PASSWORD='your_password'\n" +
                    "  export JWT_SECRET='your_secret_key_min_32_chars'\n" +
                    "=================================================\n";

            throw new IllegalStateException(errorMessage);
        }
    }

    /**
     * Validate development environment
     */
    private void validateDevelopmentEnvironment() {
        System.out.println("\nValidating DEVELOPMENT environment...");

        // Check if variables are set (warnings only)
        if (dbUrl == null || dbUrl.trim().isEmpty()) {
            System.out.println("  ⚠ DB_URL not set (using default)");
        } else {
            System.out.println("  ✓ DB_URL configured");
        }

        if (dbUsername == null || dbUsername.trim().isEmpty()) {
            System.out.println("  ⚠ DB_USERNAME not set (using default)");
        } else {
            System.out.println("  ✓ DB_USERNAME configured");
        }

        if (dbPassword == null || dbPassword.trim().isEmpty()) {
            System.out.println("  ⚠ DB_PASSWORD not set (using default)");
        } else {
            System.out.println("  ✓ DB_PASSWORD configured");
        }

        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            System.out.println("  ⚠ JWT_SECRET not set (using default - NOT SECURE for production!)");
        } else {
            System.out.println("  ✓ JWT_SECRET configured");
        }

        System.out.println("\n  NOTE: Development mode allows missing environment variables");
        System.out.println("  WARNING: Default values are NOT SECURE for production!");
    }
}
