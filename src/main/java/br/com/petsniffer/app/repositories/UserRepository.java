package br.com.petsniffer.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.petsniffer.app.domain.user.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findByLogin(String login);
    User findByEmail(String email);
}
