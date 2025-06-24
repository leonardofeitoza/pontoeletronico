package com.PontoEletronico.pontoeletronico.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Relatorio")
public class Relatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codRelatorio;

    @Column(nullable = false, length = 50)
    private String tipoRelatorio; // Geral ou Individual

    @ManyToOne
    @JoinColumn(name = "CodFuncionario", referencedColumnName = "codFuncionario", nullable = true)
    private Funcionario funcionario; // Pode ser nulo para relatórios gerais

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private LocalDate dataFim;

    @Column(nullable = false)
    private LocalDate dataGeracao;

    // Construtores
    public Relatorio() {
    }

    public Relatorio(String tipoRelatorio, Funcionario funcionario, LocalDate dataInicio, LocalDate dataFim, LocalDate dataGeracao) {
        this.tipoRelatorio = tipoRelatorio;
        this.funcionario = funcionario;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.dataGeracao = dataGeracao;
    }

    // Getters e Setters
    public int getCodRelatorio() {
        return codRelatorio;
    }

    public void setCodRelatorio(int codRelatorio) {
        this.codRelatorio = codRelatorio;
    }

    public String getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(String tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public LocalDate getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDate dataGeracao) {
        this.dataGeracao = dataGeracao;
    }
}

