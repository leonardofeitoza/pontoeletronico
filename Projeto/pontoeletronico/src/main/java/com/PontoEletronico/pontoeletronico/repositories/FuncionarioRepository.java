package com.PontoEletronico.pontoeletronico.repositories;

import com.PontoEletronico.pontoeletronico.models.Funcionario;
import com.PontoEletronico.pontoeletronico.models.Turno;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {

    @Query("SELECT MAX(f.codFuncionario) FROM Funcionario f")
    Integer findMaxCodFuncionario();


    @Query("SELECT f.nome FROM Funcionario f")
    List<String> findAllNomes(); // Retorna apenas os nomes dos funcionários

    @Query("SELECT f FROM Funcionario f WHERE f.nome = :nome")
    Funcionario findByNome(@Param("nome") String nome);

    /**
     * Busca um funcionário pelo CPF.
     *
     * @param cpf CPF do funcionário.
     * @return Funcionário correspondente ao CPF.
     */
    @Query("SELECT f FROM Funcionario f JOIN FETCH f.turno WHERE f.cpf = :cpf")
    Funcionario findByCpf(@Param("cpf") String cpf);

    /**
     * Verifica se um turno existe no banco de dados.
     *
     * @param codTurno Código do turno.
     * @return true se o turno existir, false caso contrário.
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Turno t WHERE t.codTurno = :codTurno")
    boolean existsByTurnoId(@Param("codTurno") int codTurno);

    @EntityGraph(attributePaths = "turno")
    @Query("SELECT f FROM Funcionario f JOIN FETCH f.turno")
    List<Funcionario> findAllWithTurno();

    /**
     * Busca um funcionário pelo nome.
     *
     * @param nome Nome do funcionário.
     * @return Funcionário correspondente ao nome.
     */
    @Query("SELECT f FROM Funcionario f WHERE f.nome LIKE %:nome%")
    List<Funcionario> buscarPorNome(@Param("nome") String nome);

    boolean existsByCpf(String cpf);

    @Query("SELECT f FROM Funcionario f JOIN FETCH f.turno WHERE f.nome = :nome")
    Funcionario findByNomeWithTurno(@Param("nome") String nome);

    @Query("SELECT t FROM Turno t WHERE t.descricaoTurno = :descricaoTurno")
    Turno findByDescricaoTurno(@Param("descricaoTurno") String descricaoTurno);
}