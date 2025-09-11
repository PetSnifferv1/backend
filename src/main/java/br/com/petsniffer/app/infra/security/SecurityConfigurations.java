package br.com.petsniffer.app.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity ) throws Exception {
        return httpSecurity
                // Desativa a proteção CSRF, comum para APIs REST stateless.
                .csrf(csrf -> csrf.disable( ))

                // Garante que nenhuma sessão seja criada no servidor; cada requisição é independente.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Aplica sua configuração de CORS (Cross-Origin Resource Sharing).
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Define as regras de autorização para os endpoints HTTP.
                .authorizeHttpRequests(authorize -> authorize

                        // --- ENDPOINTS PÚBLICOS ---
                        // Rotas que qualquer pessoa pode acessar, sem necessidade de login.

                        // 1. Autenticação: Permite o registro e login de usuários.
                        //    A regra "/auth/**" já cobre /auth/login e /auth/register para todos os métodos (POST, OPTIONS, etc).
                        .requestMatchers("/auth/**").permitAll()

                        // 2. Visualização Pública: Permite que visitantes vejam pets públicos.
                        .requestMatchers(HttpMethod.GET, "/pets/public-pets").permitAll()
                        .requestMatchers(HttpMethod.GET, "/pets/search-by-location/**").permitAll()

                        // 3. Health Check: Permite que o AWS Load Balancer verifique se a aplicação está saudável.
                        .requestMatchers("/actuator/**").permitAll()


                        // --- ENDPOINTS PRIVADOS ---
                        // Qualquer outra requisição que não corresponda às regras acima exige autenticação.
                        // Isso protegerá automaticamente seus endpoints de criar, alterar e deletar pets.
                        .anyRequest().authenticated()
                )

                // Adiciona seu filtro de segurança JWT para validar tokens em requisições autenticadas.
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                // Dev
                "http://localhost:*",
                "http://127.0.0.1:*",
                // Production (HTTPS only)
                "https://petsniffer.com.br",
                "https://www.petsniffer.com.br",
                "https://*.petsniffer.com.br",
                "https://petsniffer-alb-298396905.us-east-1.elb.amazonaws.com"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        // Debug temporário — você pode remover depois
        System.out.println("🔧 CORS configurado com: " + configuration.getAllowedOriginPatterns());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        firewall.setAllowSemicolon(true);
        return firewall;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
