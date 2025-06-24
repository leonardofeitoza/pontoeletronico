package com.PontoEletronico.pontoeletronico.services;

import com.PontoEletronico.pontoeletronico.models.Justificativa;
import com.PontoEletronico.pontoeletronico.repositories.JustificativaRepository;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RJustificativaPdfService {

    @Autowired
    private JustificativaRepository justificativaRepository;

    public byte[] gerarRelatorioJustificativa(Integer codFuncionario, String nomeFuncionario, String dataInicial, String dataFinal) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate startDate = dataInicial != null ? LocalDate.parse(dataInicial, inputFormatter) : null;
        LocalDate endDate = dataFinal != null ? LocalDate.parse(dataFinal, inputFormatter) : null;

        // Busca as justificativas do funcionário, filtrando se houver período
        List<Justificativa> justificativas;
        if (startDate != null && endDate != null) {
            justificativas = justificativaRepository.findByCodFuncionarioAndDataJustificativaBetweenOrderByDataJustificativaAsc(codFuncionario, startDate, endDate);
        } else {
            justificativas = justificativaRepository.findByCodFuncionarioOrderByDataJustificativaAsc(codFuncionario);
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            float yPosition;

            // Declaração de variáveis para layout
            float margemEsquerda = 50;
            float larguraPagina = page.getMediaBox().getWidth();
            float larguraConteudo = larguraPagina - 2 * margemEsquerda;
            float espacoEntreLinhas = 15;
            // Definir posições fixas para as colunas: Data, Motivo e Tipo
            float colData = margemEsquerda;      // Coluna 1: Data
            float colMotivo = margemEsquerda + 120; // Coluna 2: Motivo
            float colTipo = margemEsquerda + 370;   // Coluna 3: Tipo

            // Cria o cabeçalho do documento
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Tenta adicionar o logo
                InputStream logoStream = getClass().getResourceAsStream("/static/img/logo.png");
                if (logoStream != null) {
                    PDImageXObject logo = PDImageXObject.createFromByteArray(document, logoStream.readAllBytes(), "logo");
                    float logoWidth = 70;
                    float logoHeight = logo.getHeight() * (logoWidth / logo.getWidth());
                    float logoX = (larguraPagina - logoWidth) / 2;
                    float logoY = page.getMediaBox().getHeight() - logoHeight - 20;
                    contentStream.drawImage(logo, logoX, logoY, logoWidth, logoHeight);
                    yPosition = logoY - 20;
                } else {
                    yPosition = page.getMediaBox().getHeight() - 50;
                }

                // Título: PASSAGGIO
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                String passaggio = "PASSAGGIO";
                float passaggioWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(passaggio) / 1000 * 16;
                contentStream.newLineAtOffset((larguraPagina - passaggioWidth) / 2, yPosition);
                contentStream.showText(passaggio);
                contentStream.endText();

                yPosition -= 20;

                // Título do relatório
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                String titulo = "RELATÓRIO DE JUSTIFICATIVA";
                float tituloWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(titulo) / 1000 * 16;
                contentStream.newLineAtOffset((larguraPagina - tituloWidth) / 2, yPosition);
                contentStream.showText(titulo);
                contentStream.endText();

                yPosition -= 15;

                // Cabeçalho do funcionário
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(margemEsquerda, yPosition);
                contentStream.showText("Funcionário: " + nomeFuncionario + " (ID: " + codFuncionario + ")");
                contentStream.endText();

                yPosition -= espacoEntreLinhas * 2;

                // Se houver período definido, exibe essa informação
                if (startDate != null && endDate != null) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    String periodo = "Período: " + startDate.format(outputFormatter) + " a " + endDate.format(outputFormatter);
                    contentStream.newLineAtOffset(margemEsquerda, yPosition);
                    contentStream.showText(periodo);
                    contentStream.endText();
                    yPosition -= espacoEntreLinhas * 2;
                }

                // Cabeçalho da tabela para os dados
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                contentStream.newLineAtOffset(colData, yPosition);
                contentStream.showText("Data");
                contentStream.newLineAtOffset(colMotivo - colData, 0);
                contentStream.showText("Motivo");
                contentStream.newLineAtOffset(colTipo - colMotivo, 0);
                contentStream.showText("Tipo");
                contentStream.endText();

                yPosition -= espacoEntreLinhas;
            }

            // Após desenhar o cabeçalho da tabela:
            yPosition -= espacoEntreLinhas -5;

            boolean isGray = true;
            for (Justificativa j : justificativas) {
                if (yPosition < 50) {
                    PDPage newPage = new PDPage();
                    document.addPage(newPage);
                    yPosition = newPage.getMediaBox().getHeight() - 50;
                    page = newPage;
                }
                try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                    if (isGray) {
                        cs.setNonStrokingColor(new Color(230, 230, 230));
                        cs.addRect(margemEsquerda, yPosition, larguraConteudo, espacoEntreLinhas);
                        cs.fill();
                        cs.setNonStrokingColor(Color.BLACK);
                    }
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 10);
                    // Centraliza o texto verticalmente na linha (ajuste conforme necessário)
                    cs.newLineAtOffset(colData, yPosition + (espacoEntreLinhas * 0.25f));
                    cs.showText(j.getDataJustificativa().format(outputFormatter));
                    cs.newLineAtOffset(colMotivo - colData, 0);
                    cs.showText(j.getMotivo());
                    cs.newLineAtOffset(colTipo - colMotivo, 0);
                    cs.showText(j.getTipoJustificativa());
                    cs.endText();
                    yPosition -= espacoEntreLinhas;
                    isGray = !isGray;
                }
            }


            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar relatório de justificativa individual", e);
        }
    }

}
