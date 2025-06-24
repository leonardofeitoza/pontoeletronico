package com.PontoEletronico.pontoeletronico.dto;

public class FuncionarioDTO {
    private String codFuncionario;
    private String nome;
    private String cpf;
    private String cargo;
    private String setor;
    private String dataAdmissao;
    private Double salario;
    private String descricaoTurno;
    private String foto; // Adicionado campo para a foto em base64

    public FuncionarioDTO(String codFuncionario, String nome, String cpf, String cargo, String setor, String dataAdmissao, Double salario, String descricaoTurno) {
        this.codFuncionario = codFuncionario;
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.setor = setor;
        this.dataAdmissao = dataAdmissao;
        this.salario = salario;
        this.descricaoTurno = descricaoTurno;
    }

    public String getCodFuncionario() { return codFuncionario; }
    public void setCodFuncionario(String codFuncionario) { this.codFuncionario = codFuncionario; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getSetor() { return setor; }
    public void setSetor(String setor) { this.setor = setor; }
    public String getDataAdmissao() { return dataAdmissao; }
    public void setDataAdmissao(String dataAdmissao) { this.dataAdmissao = dataAdmissao; }
    public Double getSalario() { return salario; }
    public void setSalario(Double salario) { this.salario = salario; }
    public String getDescricaoTurno() { return descricaoTurno; }
    public void setDescricaoTurno(String descricaoTurno) { this.descricaoTurno = descricaoTurno; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}






