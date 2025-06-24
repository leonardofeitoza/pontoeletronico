package com.PontoEletronico.pontoeletronico.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "Ponto")
public class RegistroPonto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodPonto")  // Corrige para refletir o nome correto da coluna
    private Integer codRegistro;

    @Column(name = "CodFuncionario", nullable = false)
    private Integer codFuncionario;

    @Column(name = "DataRegistro", nullable = false)
    private LocalDate dataRegistro;

    @Column(name = "HoraEntrada", nullable = false)
    private LocalTime horaEntrada;

    @Column(name = "HoraSaida")
    private LocalTime horaSaida;

    @Column(name = "TotalHorasTrabalhadas", nullable = false)
    private double totalHorasTrabalhadas;

    @Column(name = "TotalHorasExtras", nullable = false)
    private double totalHorasExtras;

    // Getters e Setters
    public Integer getCodRegistro() {
        return codRegistro;
    }

    public void setCodRegistro(Integer codRegistro) {
        this.codRegistro = codRegistro;
    }

    public Integer getCodFuncionario() {
        return codFuncionario;
    }

    public void setCodFuncionario(Integer codFuncionario) {
        this.codFuncionario = codFuncionario;
    }

    public LocalDate getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDate dataRegistro) {
        this.dataRegistro = dataRegistro;
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
}
