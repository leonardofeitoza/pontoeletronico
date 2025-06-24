package com.PontoEletronico.pontoeletronico.controllers;

import com.PontoEletronico.pontoeletronico.dto.FuncionarioDTO;
import com.PontoEletronico.pontoeletronico.models.Funcionario;
import com.PontoEletronico.pontoeletronico.services.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/api/funcionarios")
public class AtualizarFuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;

    @GetMapping(value = "/nome/{nome}", produces = "application/json")
    public ResponseEntity<FuncionarioDTO> buscarPorNome(@PathVariable String nome) {
        Funcionario funcionario = funcionarioService.buscarPorNome(nome);
        if (funcionario == null) {
            return ResponseEntity.notFound().build();
        }

        String fotoBase64 = funcionario.getFoto() != null ? Base64.getEncoder().encodeToString(funcionario.getFoto()) : null;

        FuncionarioDTO funcionarioDTO = new FuncionarioDTO(
                String.format("%03d", funcionario.getCodFuncionario()),
                funcionario.getNome(),
                funcionario.getCpf(),
                funcionario.getCargo(),
                funcionario.getSetor(),
                funcionario.getDataAdmissao(),
                funcionario.getSalario(),
                funcionario.getTurno() != null ? funcionario.getTurno().getDescricaoTurno() : null
        );
        funcionarioDTO.setFoto(fotoBase64); // Adiciona a foto em base64 ao DTO

        return ResponseEntity.ok(funcionarioDTO);
    }

    @DeleteMapping("/nome/{nome}")
    public ResponseEntity<String> excluirPorNome(@PathVariable String nome) {
        try {
            funcionarioService.removerFuncionarioPorNome(nome);
            return ResponseEntity.ok("Funcionário removido com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao excluir funcionário: " + e.getMessage());
        }
    }
}