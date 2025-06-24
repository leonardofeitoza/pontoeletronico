package com.PontoEletronico.pontoeletronico.repositories;

import com.PontoEletronico.pontoeletronico.models.Funcionario;
import com.PontoEletronico.pontoeletronico.models.Ponto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;


@Repository
public interface PontoRepository extends JpaRepository<Ponto, Integer> {

    List<Ponto> findByFuncionarioCodFuncionarioOrderByDataAsc(Integer codFuncionario);

    /**
     * Busca os pontos de um funcionário em uma data específica.
     *
     * @param codFuncionario ID do funcionário.
     * @param data Data do ponto.
     * @return Lista de pontos do funcionário.
     */
    List<Ponto> findByFuncionarioCodFuncionarioAndData(int codFuncionario, LocalDate data);

    /**
     * Busca todos os pontos de um funcionário.
     *
     * @param codFuncionario ID do funcionário.
     * @return Lista de pontos do funcionário.
     */
    List<Ponto> findByFuncionarioCodFuncionario(int codFuncionario);

    List<Ponto> findByFuncionario(Funcionario funcionario);

    // Novo método para buscar por intervalo de datas
    List<Ponto> findByFuncionarioCodFuncionarioAndDataBetweenOrderByDataAsc(
            Integer codFuncionario, LocalDate dataInicio, LocalDate dataFim
    );

    @Query("SELECT DISTINCT f.codFuncionario, f.nome FROM Ponto p JOIN p.funcionario f")
    List<Object[]> findFuncionariosComPonto();

    // Método para verificar se existe um ponto vinculado ao funcionário pelo CPF
    boolean existsByFuncionario_Cpf(String cpf);

    @Query("SELECT p FROM Ponto p JOIN FETCH p.funcionario")
    List<Ponto> findAllWithFuncionario();

}

