package com.PontoEletronico.pontoeletronico.controllers;

import com.PontoEletronico.pontoeletronico.services.RLoginGlobalPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorio/login/global")
public class RLoginGlobalController {

    @Autowired
    private RLoginGlobalPdfService pdfService;

    @GetMapping("/visualizar")
    public ResponseEntity<byte[]> visualizarRelatorioGlobal() {
        byte[] pdfBytes = pdfService.gerarRelatorioGlobal();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=relatorio_global_login.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
