package com.PontoEletronico.pontoeletronico.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AtualizarPontoDTO {
    private Integer codFuncionario;
    private LocalDate data;
    private LocalTime horaEntrada;
    private LocalTime horaSaida;

    // Getters e Setters
    public Integer getCodFuncionario() {
        return codFuncionario;
    }

    public void setCodFuncionario(Integer codFuncionario) {
        this.codFuncionario = codFuncionario;
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
}
