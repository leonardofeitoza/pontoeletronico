package com.PontoEletronico.pontoeletronico.dto;

/**
 * Classe DTO para encapsular os dados de autenticação.
 */
public class LoginDTO {
    private String usuario;
    private String senha;

    // Construtor padrão
    public LoginDTO() {
    }

    // Construtor completo
    public LoginDTO(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
    }

    // Getters e Setters
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
