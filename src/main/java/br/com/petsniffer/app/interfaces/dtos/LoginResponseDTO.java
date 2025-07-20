package br.com.petsniffer.app.interfaces.dtos;

public record LoginResponseDTO(String token, String userId, String userRole) {
}
