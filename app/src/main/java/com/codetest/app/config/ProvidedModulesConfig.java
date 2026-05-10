package com.codetest.app.config;

import com.codetest.auth.AuthService;
import com.codetest.email.EmailClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Exposes the pre-built modules as Spring beans so you can inject them
 * straight into your controllers/services. Provided as a starter — feel
 * free to move or rewrite this class.
 */
@Configuration
public class ProvidedModulesConfig {

    @Bean
    public EmailClient emailClient() {
        return new EmailClient();
    }

    @Bean
    public AuthService authService() {
        return new AuthService();
    }
}
