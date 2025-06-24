package com.PontoEletronico.pontoeletronico.controllers;

import com.PontoEletronico.pontoeletronico.models.Funcionario;
import com.PontoEletronico.pontoeletronico.repositories.FuncionarioRepository;
import com.PontoEletronico.pontoeletronico.services.RLoginIndividualPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorio/login/individual")
public class RLoginIndividualController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private RLoginIndividualPdfService pdfService;

    @GetMapping("/nomes-codigos")
    public ResponseEntity<List<Map<String, Object>>> listarNomesEIds() {
        List<Funcionario> funcionarios = funcionarioRepository.findAll();
        List<Map<String, Object>> resposta = new ArrayList<>();

        for (Funcionario funcionario : funcionarios) {
            Map<String, Object> mapa = new HashMap<>();
            mapa.put("nome", funcionario.getNome());
            mapa.put("id", String.format("%03d", funcionario.getCodFuncionario())); // Formata o ID com 3 dígitos
            resposta.add(mapa);
        }

        return ResponseEntity.ok(resposta);
    }


    @GetMapping("/visualizar/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> visualizarRelatorioIndividual(@PathVariable Integer id) {
        byte[] pdfBytes = pdfService.gerarRelatorioIndividual(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=relatorio_individual_login.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
