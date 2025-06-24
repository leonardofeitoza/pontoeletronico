package com.PontoEletronico.pontoeletronico.controllers;

import com.PontoEletronico.pontoeletronico.services.FuncionarioService;
import com.PontoEletronico.pontoeletronico.services.RPontoIndividualPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/relatorio/ponto/individual")
public class RPontoIndividualController {

    @Autowired
    private RPontoIndividualPdfService pdfService;

    @Autowired
    private FuncionarioService funcionarioService;

    @GetMapping("/visualizar/{idFuncionario}")
    public ResponseEntity<byte[]> visualizarRelatorioPontoIndividual(
            @PathVariable Integer idFuncionario,
            @RequestParam(required = false) String dataInicial,
            @RequestParam(required = false) String dataFinal) {
        try {
            String nomeFuncionario = funcionarioService.buscarNomeFuncionario(idFuncionario);
            byte[] pdfContent = pdfService.gerarRelatorioPontoIndividual(idFuncionario, nomeFuncionario, dataInicial, dataFinal);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline().filename("relatorio_ponto_individual.pdf").build());

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/gerar/{idFuncionario}")
    public ResponseEntity<byte[]> gerarRelatorio(
            @PathVariable Integer idFuncionario,
            @RequestParam(required = false) String dataInicial,
            @RequestParam(required = false) String dataFinal) {
        try {
            String nomeFuncionario = funcionarioService.buscarNomeFuncionario(idFuncionario);
            byte[] pdfBytes = pdfService.gerarRelatorioPontoIndividual(idFuncionario, nomeFuncionario, dataInicial, dataFinal);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_ponto_individual.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}

