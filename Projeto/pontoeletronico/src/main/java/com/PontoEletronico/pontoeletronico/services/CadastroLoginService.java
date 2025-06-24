package com.PontoEletronico.pontoeletronico.services;

import com.PontoEletronico.pontoeletronico.dto.CadastroLoginRequestDTO;
import com.PontoEletronico.pontoeletronico.models.CadastroLogin;
import com.PontoEletronico.pontoeletronico.repositories.CadastroLoginRepository;
import com.PontoEletronico.pontoeletronico.repositories.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CadastroLoginService {

    @Autowired
    private CadastroLoginRepository cadastroLoginRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    public CadastroLogin cadastrarLogin(CadastroLoginRequestDTO dto) {
        // Verificar se o funcionário existe
        if (!funcionarioRepository.existsById(dto.getCodFuncionario())) {
            throw new IllegalArgumentException("Código do funcionário inválido.");
        }

        // Verificar se o funcionário já possui login
        if (cadastroLoginRepository.existsByCodFuncionario(dto.getCodFuncionario())) {
            throw new IllegalArgumentException("O funcionário já possui um login cadastrado.");
        }

        // Verificar se o login está em uso
        if (cadastroLoginRepository.existsByLogin(dto.getLogin())) {
            throw new IllegalArgumentException("O login está em uso. Por favor, escolha outro login.");
        }

        // Criar o cadastro de login
        CadastroLogin cadastroLogin = new CadastroLogin();
        cadastroLogin.setLogin(dto.getLogin());
        cadastroLogin.setSenha(dto.getSenha());
        cadastroLogin.setCodFuncionario(dto.getCodFuncionario());

        return cadastroLoginRepository.save(cadastroLogin);
    }
}


