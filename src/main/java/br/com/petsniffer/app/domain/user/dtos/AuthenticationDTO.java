package br.com.petsniffer.app.domain.user.dtos;


public record AuthenticationDTO(String login, String password) {


    @Override
    public String login() { return login; }

    @Override
    public String password() {
        return password;
    }

    public Object getUsername() {
    return this.login;
    }

    public Object getPassword() {
        return this.password;
    }


}
