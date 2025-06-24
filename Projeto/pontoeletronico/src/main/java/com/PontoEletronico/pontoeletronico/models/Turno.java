package com.PontoEletronico.pontoeletronico.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignora os proxies do Hibernate

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "Turno")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codTurno;

    private String descricaoTurno;

    private String horaInicio;

    private String horaTermino;

    // Construtor padrão (necessário para JPA)
    public Turno() {
    }

    // Construtor com todos os parâmetros
    public Turno(String descricaoTurno, String horaInicio, String horaTermino) {
        this.descricaoTurno = descricaoTurno;
        this.horaInicio = horaInicio;
        this.horaTermino = horaTermino;
    }

    // Construtor para JSON
    @JsonCreator
    public Turno(String descricaoTurno) {
        this.descricaoTurno = descricaoTurno;
    }

    @JsonValue
    public String getDescricaoTurno() {
        return descricaoTurno;
    }

    public void setDescricaoTurno(String descricaoTurno) {
        this.descricaoTurno = descricaoTurno;
    }

    public int getCodTurno() {
        return codTurno;
    }

    public void setCodTurno(int codTurno) {
        this.codTurno = codTurno;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraTermino() {
        return horaTermino;
    }

    public void setHoraTermino(String horaTermino) {
        this.horaTermino = horaTermino;
    }
}
