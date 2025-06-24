package com.PontoEletronico.pontoeletronico.controllers;

import com.PontoEletronico.pontoeletronico.services.RJustificativaPdfService;
import com.PontoEletronico.pontoeletronico.services.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/relatorio/justificativa")
public class RJustificativaController {

    @Autowired
    private RJustificativaPdfService pdfService;

    @Autowired
    private FuncionarioService funcionarioService;

    @GetMapping("/visualizar/{idFuncionario}")
    public ResponseEntity<InputStreamResource> visualizarRelatorio(@PathVariable Integer idFuncionario,
                                                                   @RequestParam(required = false) String dataInicial,
                                                                   @RequestParam(required = false) String dataFinal) {
        try {
            String nomeFuncionario = funcionarioService.buscarNomeFuncionario(idFuncionario);
            byte[] pdfContent = pdfService.gerarRelatorioJustificativa(idFuncionario, nomeFuncionario, dataInicial, dataFinal);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfContent);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline().filename("relatorio_justificativa.pdf").build());

            return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
