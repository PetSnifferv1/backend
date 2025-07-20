package br.com.petsniffer.app.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.petsniffer.app.domain.user.Message;
import br.com.petsniffer.app.domain.user.User;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findBySenderOrReceiver(User sender, User receiver);
    @Query("SELECT m FROM Message m WHERE (m.sender.id = :user1Id AND m.receiver.id = :user2Id) OR (m.sender.id = :user2Id AND m.receiver.id = :user1Id) ORDER BY m.timestamp ASC")
    List<Message> findConversationMessagesByIds(@Param("user1Id") String user1Id, @Param("user2Id") String user2Id);
    List<Message> findByReceiverAndReadFalse(User receiver);
} 