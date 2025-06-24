package com.PontoEletronico.pontoeletronico.repositories;

import com.PontoEletronico.pontoeletronico.models.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginRepository extends JpaRepository<Login, Integer> {

    // Método para encontrar um login por nome de usuário
    Login findByLogin(String login);

    // Método para verificar se um login já existe
    boolean existsByLogin(String login);

    // Método para buscar por login e senha
    Login findByLoginAndSenha(String login, String senha);

    List<Login> findByFuncionarioCodFuncionario(Integer codFuncionario);

    List<Login> findAll();
}