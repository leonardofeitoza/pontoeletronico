package com.PontoEletronico.pontoeletronico.services;

import com.PontoEletronico.pontoeletronico.dto.AtualizarLoginDTO;
import com.PontoEletronico.pontoeletronico.models.Funcionario;
import com.PontoEletronico.pontoeletronico.models.Login;
import com.PontoEletronico.pontoeletronico.repositories.FuncionarioRepository;
import com.PontoEletronico.pontoeletronico.repositories.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoginService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private LoginRepository loginRepository;

    /**
     * Cadastra um novo login no sistema, associado a um funcionário.
     *
     * @param codFuncionario Código do funcionário.
     * @param login          Login do usuário.
     * @param senha          Senha do usuário.
     * @throws Exception Caso o funcionário não seja encontrado.
     */
    public void cadastrarLogin(Integer codFuncionario, String login, String senha) throws Exception {
        // Verifica se o funcionário existe
        Funcionario funcionario = funcionarioRepository.findById(codFuncionario)
                .orElseThrow(() -> new Exception("Funcionário não encontrado"));

        // Cria o novo login
        Login novoLogin = new Login();
        novoLogin.setLogin(login);
        novoLogin.setSenha(senha);
        novoLogin.setFuncionario(funcionario);

        // Salva no banco de dados
        loginRepository.save(novoLogin);
    }

    /**
     * Busca um login pelo nome de usuário.
     *
     * @param login Nome de usuário.
     * @return Login encontrado ou null se não existir.
     */
    public Login buscarPorLogin(String login) {
        return loginRepository.findByLogin(login);
    }

    /**
     * Autentica um usuário pelo login e senha.
     *
     * @param usuario Nome de usuário.
     * @param senha   Senha do usuário.
     * @return true se as credenciais forem válidas, false caso contrário.
     */
    public boolean autenticar(String usuario, String senha) {
        Login login = loginRepository.findByLogin(usuario);
        return login != null && login.getSenha().equals(senha);
    }

    public AtualizarLoginDTO buscarDadosParaAlteracao(String nomeFuncionario) throws Exception {
        Funcionario funcionario = funcionarioRepository.findByNome(nomeFuncionario);
        if (funcionario == null) {
            throw new Exception("Funcionário não encontrado.");
        }

        Login login = loginRepository.findByFuncionarioCodFuncionario(funcionario.getCodFuncionario())
                .stream()
                .findFirst()
                .orElse(null);

        if (login == null) {
            throw new Exception("Login não encontrado.");
        }

        AtualizarLoginDTO dto = new AtualizarLoginDTO();
        dto.setNomeFuncionario(funcionario.getNome());
        dto.setIdFuncionario(funcionario.getCodFuncionario());
        dto.setLogin(login.getLogin());
        dto.setSenha(login.getSenha());

        return dto;
    }


    public void atualizarLogin(Integer idFuncionario, String novoLogin, String novaSenha) throws Exception {
        Login login = loginRepository.findByFuncionarioCodFuncionario(idFuncionario)
                .stream()
                .findFirst()
                .orElseThrow(() -> new Exception("Login não encontrado."));

        login.setLogin(novoLogin);
        login.setSenha(novaSenha);
        loginRepository.save(login);
    }

    public void excluirLogin(Integer idFuncionario) throws Exception {
        // Busca o login do funcionário pelo ID
        List<Login> logins = loginRepository.findByFuncionarioCodFuncionario(idFuncionario);
        if (logins.isEmpty()) {
            throw new Exception("Login não encontrado para o funcionário com ID: " + idFuncionario);
        }

        // Exclui todos os logins associados ao funcionário
        loginRepository.deleteAll(logins);
    }

    public List<AtualizarLoginDTO> listarLogins() {
        List<Login> logins = loginRepository.findAll();
        List<AtualizarLoginDTO> listaDTO = new ArrayList<>();

        for (Login login : logins) {
            AtualizarLoginDTO dto = new AtualizarLoginDTO();
            dto.setIdFuncionario(login.getFuncionario().getCodFuncionario());
            dto.setNomeFuncionario(login.getFuncionario().getNome());
            dto.setLogin(login.getLogin());
            listaDTO.add(dto);
        }
        return listaDTO;
    }

}