package com.PontoEletronico.pontoeletronico.controllers;

import com.PontoEletronico.pontoeletronico.dto.AtualizarLoginDTO;
import com.PontoEletronico.pontoeletronico.dto.LoginDTO;
import com.PontoEletronico.pontoeletronico.models.Login;
import com.PontoEletronico.pontoeletronico.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * Endpoint para autenticação de login.
     * @param loginDTO Objeto com dados do usuário e senha.
     * @return Mensagem de sucesso ou redirecionamento para o dashboard.
     */
    @PostMapping("/login")
    public ResponseEntity<String> autenticar(@RequestBody LoginDTO loginDTO) {
        boolean autenticado = loginService.autenticar(loginDTO.getUsuario(), loginDTO.getSenha());
        if (autenticado) {
            return ResponseEntity.ok("/dashboard/dashboard.html"); // Retorna o caminho do dashboard
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário ou senha inválidos.");
        }
    }

    /**
     * Endpoint para cadastrar um novo login.
     * @param loginRequest Objeto Login com dados para cadastro.
     * @return Mensagem de sucesso ou erro.
     */
    @PostMapping("/cadastrar")
    public ResponseEntity<String> cadastrarLogin(@RequestBody Login loginRequest) {
        try {
            loginService.cadastrarLogin(
                    loginRequest.getFuncionario().getCodFuncionario(),
                    loginRequest.getLogin(),
                    loginRequest.getSenha()
            );
            return ResponseEntity.ok("Login cadastrado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao cadastrar login: " + e.getMessage());
        }
    }

    /**
     * Endpoint para buscar um login pelo nome de usuário.
     * @param login Nome de usuário a ser buscado.
     * @return Objeto Login encontrado ou status 404.
     */
    @GetMapping("/buscar/{login}")
    public ResponseEntity<Login> buscarPorLogin(@PathVariable String login) {
        Login loginEncontrado = loginService.buscarPorLogin(login);
        if (loginEncontrado != null) {
            return ResponseEntity.ok(loginEncontrado);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/login/funcionario/{nome}")
    public ResponseEntity<AtualizarLoginDTO> buscarLoginPorFuncionario(@PathVariable String nome) {
        try {
            AtualizarLoginDTO alterarLoginDTO = loginService.buscarDadosParaAlteracao(nome);
            if (alterarLoginDTO != null) {
                // Formatar ID com três dígitos antes de retornar
                String idFormatado = String.format("%03d", alterarLoginDTO.getIdFuncionario());
                alterarLoginDTO.setIdFuncionarioFormatado(idFormatado);
                return ResponseEntity.ok(alterarLoginDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PutMapping("/login/atualizar")
    public ResponseEntity<String> atualizarLogin(@RequestBody AtualizarLoginDTO alterarLoginDTO) {
        try {
            loginService.atualizarLogin(alterarLoginDTO.getIdFuncionario(), alterarLoginDTO.getLogin(), alterarLoginDTO.getSenha());
            return ResponseEntity.ok("Login atualizado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao atualizar login: " + e.getMessage());
        }
    }

    @DeleteMapping("/login/excluir/{idFuncionario}")
    public ResponseEntity<String> excluirLogin(@PathVariable Integer idFuncionario) {
        try {
            loginService.excluirLogin(idFuncionario);
            return ResponseEntity.ok("Login excluído com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao excluir login: " + e.getMessage());
        }
    }

    @GetMapping("/login/listar")
    public ResponseEntity<List<AtualizarLoginDTO>> listarLogins() {
        try {
            List<AtualizarLoginDTO> logins = loginService.listarLogins();
            // Formatar ID antes de retornar
            logins.forEach(login -> login.setIdFuncionarioFormatado(String.format("%03d", login.getIdFuncionario())));
            return ResponseEntity.ok(logins);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}