package com.PontoEletronico.pontoeletronico.repositories;

import jakarta.transaction.Transactional;
import com.PontoEletronico.pontoeletronico.models.Funcionario;
import com.PontoEletronico.pontoeletronico.models.Relatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelatorioRepository extends JpaRepository<Relatorio, Integer> {

    /**
     * Gera um relatório individual para o funcionário fornecido.
     *
     * @param funcionario Objeto Funcionario para o qual o relatório será gerado.
     */
    @Transactional
    default void gerarRelatorioIndividual(Funcionario funcionario) {
        System.out.println("Gerando relatório individual para o funcionário: " + funcionario.getNome());
        // Implementação personalizada para criar relatório (e.g., PDF, CSV, etc.).
        // Este código pode ser ajustado para realizar operações específicas relacionadas à lógica de negócios.
    }

    /**
     * Gera um relatório abrangendo todos os funcionários.
     */
    @Transactional
    default void gerarRelatorioGlobal() {
        System.out.println("Gerando relatório global para todos os funcionários.");
        // Implementação personalizada para criar relatório global.
        // Este código pode ser ajustado para operações específicas do relatório global.
    }

    /**
     * Busca relatórios individuais para um funcionário.
     *
     * @param funcionario Objeto Funcionario.
     * @return Lista de relatórios.
     */
    List<Relatorio> findAllByFuncionario(Funcionario funcionario);

    /**
     * Busca relatórios globais (sem funcionário associado).
     *
     * @return Lista de relatórios globais.
     */
    List<Relatorio> findAllByFuncionarioIsNull();
}
