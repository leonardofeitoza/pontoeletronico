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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RPontoGeralPdfService {

    @Autowired
    private PontoRepository pontoRepository;

    private static class AggregateData {
        String nome;
        double horasTrabalhadas;
        double horasExtras;

        AggregateData(String nome, double horasTrabalhadas, double horasExtras) {
            this.nome = nome;
            this.horasTrabalhadas = horasTrabalhadas;
            this.horasExtras = horasExtras;
        }
    }

    public byte[] gerarRelatorioPontoGeral(String dataInicial, String dataFinal) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        List<Ponto> todosPontos = pontoRepository.findAllWithFuncionario();

        if (dataInicial != null && dataFinal != null) {
            LocalDate inicio = LocalDate.parse(dataInicial, inputFormatter);
            LocalDate fim = LocalDate.parse(dataFinal, inputFormatter);
            todosPontos = todosPontos.stream()
                    .filter(p -> !p.getData().isBefore(inicio) && !p.getData().isAfter(fim))
                    .collect(Collectors.toList());
        }

        Map<Integer, AggregateData> map = new LinkedHashMap<>();
        for (Ponto p : todosPontos) {
            int idFunc = p.getFuncionario().getCodFuncionario();
            String nomeFunc = p.getFuncionario().getNome();

            AggregateData data = map.getOrDefault(idFunc, new AggregateData(nomeFunc, 0.0, 0.0));
            data.horasTrabalhadas += p.getTotalHorasTrabalhadas();
            data.horasExtras += p.getTotalHorasExtras();
            map.put(idFunc, data);
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            float col1 = 50;
            float col2 = 100;
            float col3 = 400;
            float col4 = 500;

            float yPosition;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                InputStream logoStream = getClass().getResourceAsStream("/static/img/logo.png");
                if (logoStream != null) {
                    PDImageXObject logo = PDImageXObject.createFromByteArray(document, logoStream.readAllBytes(), "logo");
                    float logoWidth = 70;
                    float logoHeight = logo.getHeight() * (logoWidth / logo.getWidth());
                    float logoX = (page.getMediaBox().getWidth() - logoWidth) / 2;
                    float logoY = page.getMediaBox().getHeight() - logoHeight - 20;
                    contentStream.drawImage(logo, logoX, logoY, logoWidth, logoHeight);
                    yPosition = logoY - 20;
                } else {
                    yPosition = page.getMediaBox().getHeight() - 50;
                }

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                String titulo = "RELATÓRIO PONTO GERAL";
                float tituloWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(titulo) / 1000 * 16;
                contentStream.newLineAtOffset((page.getMediaBox().getWidth() - tituloWidth) / 2, yPosition);
                contentStream.showText(titulo);
                contentStream.endText();

                if (dataInicial != null && dataFinal != null) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    String periodo = "Período: " + LocalDate.parse(dataInicial, inputFormatter).format(outputFormatter)
                            + " a " + LocalDate.parse(dataFinal, inputFormatter).format(outputFormatter);
                    contentStream.newLineAtOffset(50, yPosition - 30);
                    contentStream.showText(periodo);
                    contentStream.endText();
                    yPosition -= 50;
                } else {
                    yPosition -= 30;
                }

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(col1, yPosition);
                contentStream.showText("ID");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(col2, yPosition);
                contentStream.showText("Nome do Funcionário");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(col3, yPosition);
                contentStream.showText("H. Trabalhadas");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(col4, yPosition);
                contentStream.showText("H. Extras");
                contentStream.endText();

                yPosition -= 20;
            }

            boolean isGray = false;
            for (Map.Entry<Integer, AggregateData> entry : map.entrySet()) {
                if (yPosition < 50) {
                    PDPage newPage = new PDPage();
                    document.addPage(newPage);
                    yPosition = newPage.getMediaBox().getHeight() - 50;
                    page = newPage;
                }
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                    if (isGray) {
                        contentStream.setNonStrokingColor(new Color(230, 230, 230));
                        contentStream.addRect(50, yPosition - 3, page.getMediaBox().getWidth() - 100, 20);
                        contentStream.fill();
                        contentStream.setNonStrokingColor(Color.BLACK);
                    }

                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.newLineAtOffset(col1, yPosition);
                    contentStream.showText(String.format("%03d", entry.getKey()));
                    contentStream.endText();

                    contentStream.beginText();
                    contentStream.newLineAtOffset(col2, yPosition);
                    contentStream.showText(entry.getValue().nome);
                    contentStream.endText();

                    contentStream.beginText();
                    contentStream.newLineAtOffset(col3, yPosition);
                    contentStream.showText(String.format("%.2f", entry.getValue().horasTrabalhadas));
                    contentStream.endText();

                    contentStream.beginText();
                    contentStream.newLineAtOffset(col4, yPosition);
                    contentStream.showText(String.format("%.2f", entry.getValue().horasExtras));
                    contentStream.endText();

                    yPosition -= 20;
                    isGray = !isGray;
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar relatório de ponto geral", e);
        }
    }
}
