package com.PontoEletronico.pontoeletronico.repositories;

import com.PontoEletronico.pontoeletronico.models.RegistroPonto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RegistroPontoRepository extends JpaRepository<RegistroPonto, Integer> {

    /**
     * Verifica se já existe um registro de ponto para um funcionário em uma data específica.
     *
     * @param codFuncionario ID do funcionário.
     * @param dataRegistro Data do ponto.
     * @return true se o registro já existir, false caso contrário.
     */
    boolean existsByCodFuncionarioAndDataRegistro(Integer codFuncionario, LocalDate dataRegistro);
}
