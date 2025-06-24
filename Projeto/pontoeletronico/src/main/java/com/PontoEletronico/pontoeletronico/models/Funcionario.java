package com.PontoEletronico.pontoeletronico.models;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Funcionario")
public class Funcionario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodFuncionario")
    private Integer codFuncionario;

    @Column(name = "Nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "CPF", nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(name = "Setor", nullable = false, length = 50)
    private String setor;

    @Column(name = "Cargo", nullable = false, length = 50)
    private String cargo;

    @Column(name = "DataAdmissao", nullable = false)
    private String dataAdmissao;

    @Column(name = "Salario", nullable = false)
    private Double salario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CodTurno")
    private Turno turno;

    @Column(name = "Foto")
    private byte[] foto;

    public Funcionario() {}

    public Funcionario(String nome, String cpf, String setor, String cargo, String dataAdmissao, Double salario, Turno turno) {
        this.nome = nome;
        this.cpf = cpf;
        this.setor = setor;
        this.cargo = cargo;
        this.dataAdmissao = dataAdmissao;
        this.salario = salario;
        this.turno = turno;
    }

    public Integer getCodFuncionario() { return codFuncionario; }
    public void setCodFuncionario(Integer codFuncionario) { this.codFuncionario = codFuncionario; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getSetor() { return setor; }
    public void setSetor(String setor) { this.setor = setor; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getDataAdmissao() { return dataAdmissao; }
    public void setDataAdmissao(String dataAdmissao) { this.dataAdmissao = dataAdmissao; }
    public Double getSalario() { return salario; }
    public void setSalario(Double salario) { this.salario = salario; }
    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) { this.turno = turno; }
    public byte[] getFoto() { return foto; }
    public void setFoto(byte[] foto) { this.foto = foto; }
}