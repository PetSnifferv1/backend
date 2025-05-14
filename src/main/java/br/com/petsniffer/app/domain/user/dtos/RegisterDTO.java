package br.com.petsniffer.app.domain.user.dtos;

import br.com.petsniffer.app.domain.user.UserRole;
import jakarta.validation.constraints.Email;

public record RegisterDTO(String login, String password, @Email String email, UserRole role) {
}
