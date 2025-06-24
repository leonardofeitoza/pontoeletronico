package com.PontoEletronico.pontoeletronico.controllers;

import com.PontoEletronico.pontoeletronico.dto.CadastroLoginRequestDTO;
import com.PontoEletronico.pontoeletronico.services.CadastroLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class CadastroLoginController {

    @Autowired
    private CadastroLoginService cadastroLoginService;

    @PostMapping("/cadastrar")
    public ResponseEntity<String> cadastrarLogin(@RequestBody CadastroLoginRequestDTO requestDTO) {
        try {
            cadastroLoginService.cadastrarLogin(requestDTO);
            return ResponseEntity.ok("Login cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            // Retornar a mensagem amigável ao cliente
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Retornar erro genérico interno
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno no servidor");
        }
    }
}



