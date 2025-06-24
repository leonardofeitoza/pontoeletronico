package com.PontoEletronico.pontoeletronico.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "Ponto")

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Ponto {

    // Relacionamento com Funcionario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CodFuncionario", referencedColumnName = "codFuncionario")
    @JsonIgnore // Ignorar este campo durante a serialização
    private Funcionario funcionario;


    /*@ManyToOne
    @JoinColumn(name = "CodFuncionario", referencedColumnName = "codFuncionario")
    private Funcionario funcionario;*/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codPonto;

    @Column(name = "DataRegistro", nullable = false) // Nome correto da coluna no banco de dados
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horaEntrada;

    @Column(nullable = false)
    private LocalTime horaSaida;

    @Column(nullable = false)
    private double totalHorasTrabalhadas;

    @Column(nullable = false)
    private double totalHorasExtras;

    // Construtores
    public Ponto() {
    }

    public Ponto(LocalDate data, LocalTime horaEntrada, LocalTime horaSaida, double totalHorasTrabalhadas, double totalHorasExtras, Funcionario funcionario) {
        this.data = data;
        this.horaEntrada = horaEntrada;
        this.horaSaida = horaSaida;
        this.totalHorasTrabalhadas = totalHorasTrabalhadas;
        this.totalHorasExtras = totalHorasExtras;
        this.funcionario = funcionario;
    }

    // Getters e Setters
    public int getCodPonto() {
        return codPonto;
    }

    public void setCodPonto(int codPonto) {
        this.codPonto = codPonto;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalTime getHoraSaida() {
        return horaSaida;
    }

    public void setHoraSaida(LocalTime horaSaida) {
        this.horaSaida = horaSaida;
    }

    public double getTotalHorasTrabalhadas() {
        return totalHorasTrabalhadas;
    }

    public void setTotalHorasTrabalhadas(double totalHorasTrabalhadas) {
        this.totalHorasTrabalhadas = totalHorasTrabalhadas;
    }

    public double getTotalHorasExtras() {
        return totalHorasExtras;
    }

    public void setTotalHorasExtras(double totalHorasExtras) {
        this.totalHorasExtras = totalHorasExtras;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
}

