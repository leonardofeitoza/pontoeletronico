package com.PontoEletronico.pontoeletronico.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Justificativa")
public class Justificativa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codJustificativa;

    @Column(nullable = false)
    private Integer codFuncionario;

    @Column(nullable = false)
    private LocalDate dataJustificativa;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String motivo;

    @Column(nullable = false, length = 50)
    private String tipoJustificativa;

    // Getters e Setters
    public Integer getCodJustificativa() {
        return codJustificativa;
    }

    public void setCodJustificativa(Integer codJustificativa) {
        this.codJustificativa = codJustificativa;
    }

    public Integer getCodFuncionario() {
        return codFuncionario;
    }

    public void setCodFuncionario(Integer codFuncionario) {
        this.codFuncionario = codFuncionario;
    }

    public LocalDate getDataJustificativa() {
        return dataJustificativa;
    }

    public void setDataJustificativa(LocalDate dataJustificativa) {
        this.dataJustificativa = dataJustificativa;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getTipoJustificativa() {
        return tipoJustificativa;
    }

    public void setTipoJustificativa(String tipoJustificativa) {
        this.tipoJustificativa = tipoJustificativa;
    }

    // Novo getter para retornar o código do funcionário formatado com 3 dígitos
    public String getCodFuncionarioFormatado() {
        if (codFuncionario == null) {
            return "";
        }
        return String.format("%03d", codFuncionario);
    }
}
