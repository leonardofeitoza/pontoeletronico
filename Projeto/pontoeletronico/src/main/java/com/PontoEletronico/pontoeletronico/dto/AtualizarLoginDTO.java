package com.PontoEletronico.pontoeletronico.dto;

public class AtualizarLoginDTO {
    private String nomeFuncionario;
    private Integer idFuncionario;
    private String login;
    private String senha;

    // Novo campo para armazenar ID formatado
    private String idFuncionarioFormatado;

    // Getters e Setters
    public String getNomeFuncionario() {
        return nomeFuncionario;
    }

    public void setNomeFuncionario(String nomeFuncionario) {
        this.nomeFuncionario = nomeFuncionario;
    }

    public Integer getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Integer idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    // Método para formatar ID com três dígitos
    public String getIdFuncionarioFormatado() {
        return idFuncionario != null ? String.format("%03d", idFuncionario) : null;
    }

    public void setIdFuncionarioFormatado(String idFuncionarioFormatado) {
        this.idFuncionarioFormatado = idFuncionarioFormatado;
    }
}
