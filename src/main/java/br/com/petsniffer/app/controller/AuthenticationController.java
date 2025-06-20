package br.com.petsniffer.app.controller;

import br.com.petsniffer.app.domain.user.User;
import br.com.petsniffer.app.domain.user.dtos.AuthenticationDTO;
import br.com.petsniffer.app.domain.user.dtos.RegisterDTO;
import br.com.petsniffer.app.repositories.UserRepository;
import br.com.petsniffer.app.infra.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository repository;
    @Autowired
    private TokenService tokenService;


    private AuthenticationController authenticationController;


    @GetMapping("/debug-headers")
    public ResponseEntity<String> debugHeaders(@RequestHeader Map<String, String> headers) {
        headers.forEach((k, v) -> System.out.println(k + ": " + v));
        return ResponseEntity.ok("Check the logs");
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid AuthenticationDTO authRequest) {
        UsernamePasswordAuthenticationToken loginData = new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(), authRequest.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(loginData);
            // Obter o principal (usuário autenticado)
            User user = (User) authentication.getPrincipal();

// Obter as permissões (roles) do usuário
            String role = authentication.getAuthorities()
                    .stream()
                    .findFirst() // Caso o usuário tenha uma única role
                    .map(GrantedAuthority::getAuthority)
                    .orElse("UNKNOWN");

// Gerar o token JWT
            String token = tokenService.generateToken(user);

// Criar a resposta
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", user.getId());
            response.put("userRole", role);

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data){
        if(this.repository.findByLogin(data.login()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser =  new User(data.login(), encryptedPassword,  data.email(), data.role());
        System.out.println(data.login() + encryptedPassword + data.email() + data.role());

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }
}
