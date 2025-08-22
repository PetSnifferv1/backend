package br.com.petsniffer.app.interfaces.mapper;

import br.com.petsniffer.app.domain.user.User;
import br.com.petsniffer.app.interfaces.dtos.AuthenticationDTO;
import br.com.petsniffer.app.interfaces.dtos.RegisterDTO;

public class UserMapper {
    public static User toEntity(RegisterDTO dto, String encryptedPassword) {
        return new User(dto.login(), encryptedPassword, dto.email(), dto.role());
    }
    public static User toEntity(AuthenticationDTO dto) {
        return new User(dto.login(), dto.password(), null, null);
    }

} 