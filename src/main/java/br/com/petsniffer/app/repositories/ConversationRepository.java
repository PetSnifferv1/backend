package br.com.petsniffer.app.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.petsniffer.app.domain.user.Conversation;
import br.com.petsniffer.app.domain.user.User;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    List<Conversation> findByUser1OrUser2(User user1, User user2);
    Conversation findByUser1AndUser2OrUser2AndUser1(User user1, User user2, User user2b, User user1b);
} 