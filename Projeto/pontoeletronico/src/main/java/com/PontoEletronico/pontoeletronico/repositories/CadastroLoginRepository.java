package com.PontoEletronico.pontoeletronico.repositories;

import com.PontoEletronico.pontoeletronico.models.CadastroLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CadastroLoginRepository extends JpaRepository<CadastroLogin, Integer> {
    /**
     * Verifica se um login específico já está em uso.
     *
     * @param login Login a ser verificado.
     * @return true se o login estiver em uso, false caso contrário.
     */
    boolean existsByLogin(String login);

    /**
     * Verifica se existe um login associado ao código de um funcionário.
     *
     * @param codFuncionario Código do funcionário.
     * @return true se existir login associado, false caso contrário.
     */
    boolean existsByCodFuncionario(Integer codFuncionario);
}



