package br.com.petsniffer.app.interfaces.dtos;

public record AuthenticationDTO(String login, String password) {
    public Object getUsername() {
        return this.login;
    }
    public Object getPassword() {
        return this.password;
    }
}
