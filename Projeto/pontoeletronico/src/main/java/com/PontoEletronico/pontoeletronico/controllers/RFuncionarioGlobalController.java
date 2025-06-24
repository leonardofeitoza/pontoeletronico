package com.PontoEletronico.pontoeletronico.controllers;

import com.PontoEletronico.pontoeletronico.services.RFuncionarioGlobalPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/relatorio/funcionarios/global")
public class RFuncionarioGlobalController {

    @Autowired
    private RFuncionarioGlobalPdfService pdfService;

    @GetMapping("/visualizar")
    public ResponseEntity<byte[]> visualizarRelatorioGlobal() {
        byte[] pdfBytes = pdfService.gerarRelatorioGlobal();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=relatorio_global.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/gerar")
    public ResponseEntity<byte[]> gerarRelatorioGlobal() {
        byte[] pdfBytes = pdfService.gerarRelatorioGlobal();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_global.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}


