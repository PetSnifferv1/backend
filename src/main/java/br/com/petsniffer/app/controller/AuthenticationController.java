package br.com.petsniffer.app.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.petsniffer.app.domain.user.User;
import br.com.petsniffer.app.infra.security.TokenService;
import br.com.petsniffer.app.interfaces.dtos.AuthenticationDTO;
import br.com.petsniffer.app.interfaces.dtos.LoginResponseDTO;
import br.com.petsniffer.app.interfaces.dtos.RegisterDTO;
import br.com.petsniffer.app.repositories.PetRepository;
import br.com.petsniffer.app.repositories.UserRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository repository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    private AuthenticationController authenticationController;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO authRequest) {
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
            LoginResponseDTO response = new LoginResponseDTO(token, user.getId(), role);
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data){
        if(this.repository.findByLogin(data.login()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser =  new User(data.login(), encryptedPassword,  data.email(), data.role());
        System.out.println(data.login() + encryptedPassword + data.email() + data.role());

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAccount() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(401).build();
        }
        petRepository.deleteAllByOwnerid(user.getId());
        repository.deleteById(user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getProfile() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(401).build();
        }
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("login", user.getLogin());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole());
        return ResponseEntity.ok(userData);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        User user = repository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("login", user.getLogin());
        userData.put("email", user.getEmail());
        return ResponseEntity.ok(userData);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");
        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Campos obrigatórios não informados");
        }
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.status(400).body("Senha atual incorreta");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);
        return ResponseEntity.ok().body("Senha alterada com sucesso");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> body) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }
        String newLogin = body.get("login");
        String newEmail = body.get("email");
        if (newLogin == null || newEmail == null) {
            return ResponseEntity.badRequest().body("Campos obrigatórios não informados");
        }
        // Nunca altere o id do usuário! Apenas login e email podem ser atualizados.
        User existingLogin = repository.findByLogin(newLogin);
        if (existingLogin != null && !existingLogin.getId().equals(user.getId())) {
            return ResponseEntity.status(400).body("Login já está em uso");
        }
        User existingEmail = repository.findByEmail(newEmail);
        if (existingEmail != null && !existingEmail.getId().equals(user.getId())) {
            return ResponseEntity.status(400).body("Email já está em uso");
        }
        user.setLogin(newLogin);
        user.setEmail(newEmail);
        // O id permanece o mesmo!
        repository.save(user);
        return ResponseEntity.ok().body("Perfil atualizado com sucesso");
    }
}
