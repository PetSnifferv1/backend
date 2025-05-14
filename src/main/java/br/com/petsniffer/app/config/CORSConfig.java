package br.com.petsniffer.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir requisições do domínio petsniffer.com.br
        config.addAllowedOrigin("http://petsniffer.com.br:5173");
        config.addAllowedOrigin("http://localhost:5173");
        
        // Permitir todos os métodos HTTP
        config.addAllowedMethod("*");
        
        // Permitir todos os headers
        config.addAllowedHeader("*");
        
        // Permitir credenciais (cookies, etc)
        config.setAllowCredentials(true);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
} 