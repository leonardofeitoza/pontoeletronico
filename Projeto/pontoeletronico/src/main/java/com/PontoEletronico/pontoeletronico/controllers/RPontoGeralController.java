package com.PontoEletronico.pontoeletronico.controllers;

import com.PontoEletronico.pontoeletronico.services.RPontoGeralPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/relatorio/ponto/geral")
public class RPontoGeralController {

    @Autowired
    private RPontoGeralPdfService pdfService;

    // Endpoint para visualização inline do relatório geral
    @GetMapping("/visualizar")
    public ResponseEntity<byte[]> visualizarRelatorioPontoGeral(
            @RequestParam(required = false) String dataInicial,
            @RequestParam(required = false) String dataFinal) {
        try {
            byte[] pdfContent = pdfService.gerarRelatorioPontoGeral(dataInicial, dataFinal);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline().filename("relatorio_ponto_geral.pdf").build());
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Endpoint para download do relatório geral em PDF
    @GetMapping("/gerar")
    public ResponseEntity<byte[]> gerarRelatorioPontoGeral(
            @RequestParam(required = false) String dataInicial,
            @RequestParam(required = false) String dataFinal) {
        try {
            byte[] pdfContent = pdfService.gerarRelatorioPontoGeral(dataInicial, dataFinal);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_ponto_geral.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfContent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
}
