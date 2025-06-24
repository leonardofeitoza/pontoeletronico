package com.PontoEletronico.pontoeletronico.controllers;

import com.PontoEletronico.pontoeletronico.services.RJustificativaGlobalPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/relatorio/justificativa/geral")
public class RJustificativaGlobalController {

    @Autowired
    private RJustificativaGlobalPdfService pdfService;

    @GetMapping("/visualizar")
    public ResponseEntity<InputStreamResource> visualizarRelatorio(
            @RequestParam(required = false) String dataInicial,
            @RequestParam(required = false) String dataFinal) {
        try {
            byte[] pdfContent = pdfService.gerarRelatorioJustificativaGlobal(dataInicial, dataFinal);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfContent);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline().filename("relatorio_justificativa_geral.pdf").build());
            return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/gerar")
    public ResponseEntity<byte[]> gerarRelatorio(
            @RequestParam(required = false) String dataInicial,
            @RequestParam(required = false) String dataFinal) {
        try {
            byte[] pdfContent = pdfService.gerarRelatorioJustificativaGlobal(dataInicial, dataFinal);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_justificativa_geral.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfContent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
}
