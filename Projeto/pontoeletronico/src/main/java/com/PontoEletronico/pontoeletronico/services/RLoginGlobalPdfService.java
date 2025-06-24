package com.PontoEletronico.pontoeletronico.services;

import com.PontoEletronico.pontoeletronico.models.Login;
import com.PontoEletronico.pontoeletronico.repositories.LoginRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class RLoginGlobalPdfService {

    @Autowired
    private LoginRepository loginRepository;

    public byte[] gerarRelatorioGlobal() {
        // Recuperar todos os logins
        List<Login> logins = loginRepository.findAll();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            float marginLeft = 50;
            float pageWidth = page.getMediaBox().getWidth();
            float contentWidth = pageWidth - 2 * marginLeft;
            float rowHeight = 20;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Adicionar a logo ao PDF
                InputStream logoStream = getClass().getResourceAsStream("/static/img/logo.png");
                if (logoStream == null) {
                    throw new RuntimeException("Logo não encontrada no caminho /static/img/logo.png");
                }
                PDImageXObject logo = PDImageXObject.createFromByteArray(document, logoStream.readAllBytes(), "logo");

                float logoWidth = 70;
                float logoHeight = logo.getHeight() * (logoWidth / logo.getWidth());
                float logoX = (pageWidth - logoWidth) / 2;
                float logoY = page.getMediaBox().getHeight() - logoHeight - 50;

                contentStream.drawImage(logo, logoX, logoY, logoWidth, logoHeight);

                // Adicionar título "PASSAGGIO"
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.newLineAtOffset((pageWidth - getTextWidth("PASSAGGIO", PDType1Font.HELVETICA_BOLD, 16)) / 2, logoY - 20);
                contentStream.showText("PASSAGGIO");
                contentStream.endText();

                // Adicionar título do relatório
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.newLineAtOffset((pageWidth - getTextWidth("RELATÓRIO GERAL DE LOGIN", PDType1Font.HELVETICA_BOLD, 16)) / 2, logoY - 50);
                contentStream.showText("RELATÓRIO GERAL DE LOGIN");
                contentStream.endText();

                // Adicionar cabeçalho
                float headerY = logoY - 100;
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(marginLeft, headerY);

                float colWidth = contentWidth / 3;
                contentStream.showText(centerText("ID", colWidth, PDType1Font.HELVETICA_BOLD, 12));
                contentStream.newLineAtOffset(colWidth, 0);
                contentStream.showText(centerText("NOME DO FUNCIONÁRIO", colWidth, PDType1Font.HELVETICA_BOLD, 12));
                contentStream.newLineAtOffset(colWidth, 0);
                contentStream.showText(centerText("LOGIN", colWidth, PDType1Font.HELVETICA_BOLD, 12));
                contentStream.endText();

                // Adicionar dados dos logins
                float rowY = headerY - rowHeight;
                int rowIndex = 0;
                for (Login login : logins) {
                    if (login.getFuncionario() != null) {
                        boolean isGray = rowIndex % 2 == 0;

                        // Fundo cinza claro para linhas intercaladas
                        if (isGray) {
                            contentStream.setNonStrokingColor(220);
                            contentStream.addRect(marginLeft, rowY - rowHeight + 15, contentWidth, rowHeight);
                            contentStream.fill();
                        }

                        contentStream.setNonStrokingColor(0); // Resetar para preto
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA, 10);
                        contentStream.newLineAtOffset(marginLeft, rowY);

                        // Adicionar ID formatado com zeros à esquerda
                        String idFormatado = String.format("%03d", login.getFuncionario().getCodFuncionario());
                        contentStream.showText(centerText(idFormatado, colWidth, PDType1Font.HELVETICA, 10));
                        contentStream.newLineAtOffset(colWidth, 0);
                        contentStream.showText(centerText(login.getFuncionario().getNome(), colWidth, PDType1Font.HELVETICA, 10));
                        contentStream.newLineAtOffset(colWidth, 0);
                        contentStream.showText(centerText(login.getLogin(), colWidth, PDType1Font.HELVETICA, 10));

                        contentStream.endText();
                        rowY -= rowHeight;
                        rowIndex++;
                    }
                }
            }

            // Salva o documento em um ByteArrayOutputStream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar o relatório PDF", e);
        }
    }

    private float getTextWidth(String text, PDType1Font font, int fontSize) throws IOException {
        return font.getStringWidth(text) / 1000 * fontSize;
    }

    private String centerText(String text, float colWidth, PDType1Font font, int fontSize) throws IOException {
        float textWidth = getTextWidth(text, font, fontSize);
        float padding = (colWidth - textWidth) / 2;
        return " ".repeat(Math.max(0, (int) (padding / getTextWidth(" ", font, fontSize)))) + text;
    }
}
