package br.com.petsniffer.app.infra.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return  httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET,  "pets/public-pets").permitAll()
                        .requestMatchers(HttpMethod.POST, "auth/login").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "auth/register").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "pets/alter-pets/").permitAll()
                        .requestMatchers(HttpMethod.PUT, "pets/alter-pets/").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "pets/delete-pets").permitAll()
                        .requestMatchers(HttpMethod.POST, "pets/create-pets").permitAll()
                        .requestMatchers(HttpMethod.POST, "pets/upload-imagem").permitAll()
                        .requestMatchers(HttpMethod.POST, "files/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "fileup/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "pets/search-by-location/**").permitAll()

                        .anyRequest().authenticated()
                )
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of("*")); // Permitir todos os domínios
                     //       "http://localhost:5173",
                            //       "http://petsniffer.com.br:5173",
                            //        "http://petsniffer.com.br",
                            //        "http://www.petsniffer.com.br",
                            //        "http://www.petsniffer.com.br:5173",
                            //        "http://petsniffer-alb-298396905.us-east-1.elb.amazonaws.com",
                            //        "http://petsniffer-alb-298396905.us-east-1.elb.amazonaws.com:5173"
                            
                    //));
                    //corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedMethods(List.of("*"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setExposedHeaders(List.of("Authorization", "Content-Type")); // Adicione se precisar expor headers
                    //corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;

                }))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true); // Permitir barras codificadas na URL
        firewall.setAllowSemicolon(true); // Permitir ponto e vírgula na URL, se necessário
        firewall.setAllowUrlEncodedPercent(true);
        // Adicione mais permissões conforme necessário
        return firewall;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/h2-console/**");
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



}
