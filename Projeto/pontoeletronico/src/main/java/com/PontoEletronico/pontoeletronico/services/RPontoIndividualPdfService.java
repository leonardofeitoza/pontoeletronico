package com.PontoEletronico.pontoeletronico.services;

import com.PontoEletronico.pontoeletronico.models.Ponto;
import com.PontoEletronico.pontoeletronico.repositories.PontoRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RPontoIndividualPdfService {

    @Autowired
    private PontoRepository pontoRepository;

    public byte[] gerarRelatorioPontoIndividual(Integer codFuncionario, String nomeFuncionario, String dataInicial, String dataFinal) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate startDate = dataInicial != null ? LocalDate.parse(dataInicial, inputFormatter) : null;
        LocalDate endDate = dataFinal != null ? LocalDate.parse(dataFinal, inputFormatter) : null;

        List<Ponto> pontos;

        if (startDate != null && endDate != null) {
            pontos = pontoRepository.findByFuncionarioCodFuncionarioAndDataBetweenOrderByDataAsc(codFuncionario, startDate, endDate);
        } else {
            pontos = pontoRepository.findByFuncionarioCodFuncionarioOrderByDataAsc(codFuncionario);
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            float yPosition = 750; // Posição inicial na página

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Adicionar logo
                InputStream logoStream = getClass().getResourceAsStream("/static/img/logo.png");
                if (logoStream == null) {
                    throw new RuntimeException("Logo não encontrado no caminho /static/img/logo.png");
                }
                PDImageXObject logo = PDImageXObject.createFromByteArray(document, logoStream.readAllBytes(), "logo");

                float logoWidth = 70;
                float logoHeight = logo.getHeight() * (logoWidth / logo.getWidth());
                float logoX = (page.getMediaBox().getWidth() - logoWidth) / 2;
                float logoY = page.getMediaBox().getHeight() - logoHeight - 50;
                contentStream.drawImage(logo, logoX, logoY, logoWidth, logoHeight);

                // Adicionar título "PASSAGGIO"
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                float passaggioTextWidth = PDType1Font.HELVETICA_BOLD.getStringWidth("PASSAGGIO") / 1000 * 16;
                contentStream.newLineAtOffset((page.getMediaBox().getWidth() - passaggioTextWidth) / 2, logoY - 20);
                contentStream.showText("PASSAGGIO");
                contentStream.endText();

                // Adicionar título "RELATÓRIO PONTO INDIVIDUAL"
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                float reportTitleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth("RELATÓRIO PONTO INDIVIDUAL") / 1000 * 16;
                contentStream.newLineAtOffset((page.getMediaBox().getWidth() - reportTitleWidth) / 2, logoY - 50);
                contentStream.showText("RELATÓRIO PONTO INDIVIDUAL");
                contentStream.endText();

                // Adicionar nome do funcionário
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(50, logoY - 100);
                contentStream.showText("Funcionário: " + nomeFuncionario);
                contentStream.endText();

                // Adicionar período do relatório
                if (dataInicial != null && dataFinal != null) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.newLineAtOffset(50, logoY - 140);
                    contentStream.showText("Período: " + startDate.format(outputFormatter) + " a " + endDate.format(outputFormatter));
                    contentStream.endText();
                }

                // Espaço antes do cabeçalho da tabela
                yPosition = logoY - 180;

                // Adicionar cabeçalho da tabela
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Data                          Entrada                  Saída                  H. Trabalhadas                    H. Extras");
                contentStream.endText();

                yPosition -= 20; // Avançar posição
            }

            boolean isGray = false; // Variável para alternar cores
            BigDecimal totalHorasExtras = BigDecimal.ZERO; // Total de horas extras acumuladas

            if (pontos.isEmpty()) {
                // Caso não haja registros de ponto, exibir mensagem
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, yPosition);
                    contentStream.showText("Nenhum registro de ponto encontrado.");
                    contentStream.endText();
                }
            } else {
                for (Ponto ponto : pontos) {
                    if (yPosition < 50) { // Adiciona nova página se o espaço for insuficiente
                        page = new PDPage();
                        document.addPage(page);
                        yPosition = 750;
                    }

                    try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                        isGray = !isGray; // Alternar cor para próxima linha

                        // Adicionar fundo cinza claro para linhas intercaladas
                        if (isGray) {
                            contentStream.setNonStrokingColor(new Color(230, 230, 230)); // Cinza claro
                            contentStream.addRect(50, yPosition - 10, page.getMediaBox().getWidth() - 100, 20);
                            contentStream.fill();
                            contentStream.setNonStrokingColor(Color.BLACK); // Resetar cor para preto
                        }

                        contentStream.setFont(PDType1Font.HELVETICA, 10);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, yPosition);
                        contentStream.showText(
                                ponto.getData().format(outputFormatter) + "                         " +
                                        ponto.getHoraEntrada() + "                           " +
                                        ponto.getHoraSaida() + "                                    " +
                                        ponto.getTotalHorasTrabalhadas() + "                                              " +
                                        ponto.getTotalHorasExtras()
                        );
                        contentStream.endText();

                        // Acumula horas extras
                        totalHorasExtras = totalHorasExtras.add(new BigDecimal(ponto.getTotalHorasExtras()));

                        yPosition -= 20; // Avança para a próxima linha
                    }
                }

                // Adicionar total de horas extras no final
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                    yPosition -= 20; // Adiciona espaço antes do total
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.newLineAtOffset(50, yPosition);
                    contentStream.showText("Total de Horas Extras no Período: " + totalHorasExtras + " horas");
                    contentStream.endText();
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar relatório de ponto individual", e);
        }
    }
}
