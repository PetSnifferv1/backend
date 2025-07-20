package br.com.petsniffer.app.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.petsniffer.app.domain.user.Message;
import br.com.petsniffer.app.domain.user.User;
import br.com.petsniffer.app.repositories.MessageRepository;
import br.com.petsniffer.app.repositories.UserRepository;

@RestController
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    // Enviar mensagem
    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody MessageDTO body) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User sender)) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }
        User receiver = userRepository.findById(body.receiverId()).orElse(null);
        if (receiver == null) {
            return ResponseEntity.badRequest().body("Destinatário não encontrado");
        }
        Message message = new Message(sender, receiver, body.content(), LocalDateTime.now());
        messageRepository.save(message);
        return ResponseEntity.ok(message);
    }

    // Listar todas as conversas do usuário autenticado
    @GetMapping
    public ResponseEntity<List<Message>> getAllConversations() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(401).build();
        }
        List<Message> messages = messageRepository.findBySenderOrReceiver(user, user);
        return ResponseEntity.ok(messages);
    }

    // Listar mensagens entre usuário autenticado e outro usuário
    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<?> getConversation(@PathVariable String otherUserId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            System.out.println("[DEBUG] Usuário autenticado não encontrado");
            return ResponseEntity.status(401).build();
        }
        User other = userRepository.findById(otherUserId).orElse(null);
        if (other == null) {
            System.out.println("[DEBUG] Usuário destino não encontrado: " + otherUserId);
            return ResponseEntity.badRequest().body("Usuário destino não encontrado");
        }
        System.out.println("[DEBUG] getConversation: user=" + user.getId() + ", other=" + other.getId());
        try {
            List<Message> messages = messageRepository.findConversationMessagesByIds(user.getId(), other.getId());
            System.out.println("[DEBUG] messages.size=" + messages.size());
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao buscar mensagens: " + e.getMessage());
        }
    }

    // DTO para envio de mensagem
    public record MessageDTO(String receiverId, String content) {}
} 