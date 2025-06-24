package com.PontoEletronico.pontoeletronico.repositories;

import com.PontoEletronico.pontoeletronico.models.Justificativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JustificativaRepository extends JpaRepository<Justificativa, Integer> {

    // Método para buscar justificativas por funcionário e ordenar por data
    List<Justificativa> findByCodFuncionarioOrderByDataJustificativaAsc(Integer codFuncionario);

    // Método para buscar justificativas por funcionário em um intervalo de datas
    List<Justificativa> findByCodFuncionarioAndDataJustificativaBetweenOrderByDataJustificativaAsc(Integer codFuncionario, LocalDate startDate, LocalDate endDate);

    List<Justificativa> findByCodFuncionarioAndDataJustificativa(Integer codFuncionario, LocalDate dataJustificativa);

    @Query("SELECT DISTINCT j.codFuncionario, f.nome FROM Justificativa j JOIN Funcionario f ON j.codFuncionario = f.codFuncionario")
    List<Object[]> findDistinctFuncionarios();
}


