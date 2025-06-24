package com.PontoEletronico.pontoeletronico.services;

import com.PontoEletronico.pontoeletronico.models.Funcionario;
import com.PontoEletronico.pontoeletronico.models.Login;
import com.PontoEletronico.pontoeletronico.repositories.FuncionarioRepository;
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
public class RLoginIndividualPdfService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private LoginRepository loginRepository;

    public byte[] gerarRelatorioIndividual(Integer idFuncionario) {
        Funcionario funcionario = funcionarioRepository.findById(idFuncionario)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com o ID: " + idFuncionario));

        List<Login> logins = loginRepository.findByFuncionarioCodFuncionario(idFuncionario);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            float marginLeft = 50;
            float pageWidth = page.getMediaBox().getWidth();
            float contentWidth = pageWidth - 2 * marginLeft;
            float rowHeight = 20;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
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

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.newLineAtOffset((pageWidth - getTextWidth("PASSAGGIO", PDType1Font.HELVETICA_BOLD, 16)) / 2, logoY - 20);
                contentStream.showText("PASSAGGIO");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.newLineAtOffset((pageWidth - getTextWidth("RELATÓRIO INDIVIDUAL DE LOGIN", PDType1Font.HELVETICA_BOLD, 16)) / 2, logoY - 50);
                contentStream.showText("RELATÓRIO INDIVIDUAL DE LOGIN");
                contentStream.endText();

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

                float rowY = headerY - rowHeight;
                for (int i = 0; i < Math.max(1, logins.size()); i++) {
                    boolean isGray = i % 2 == 0;

                    if (isGray) {
                        contentStream.setNonStrokingColor(220);
                        contentStream.addRect(marginLeft, rowY - rowHeight + 15, contentWidth, rowHeight);
                        contentStream.fill();
                    }

                    contentStream.setNonStrokingColor(0); // Reset color to black
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.newLineAtOffset(marginLeft, rowY);

                    String idText = String.format("%03d", funcionario.getCodFuncionario()); // FORMATA O ID COM ZEROS À ESQUERDA
                    String nameText = funcionario.getNome();
                    String loginText = (i < logins.size()) ? logins.get(i).getLogin() : "SEM LOGIN";

                    contentStream.showText(centerText(idText, colWidth, PDType1Font.HELVETICA, 10));
                    contentStream.newLineAtOffset(colWidth, 0);
                    contentStream.showText(centerText(nameText, colWidth, PDType1Font.HELVETICA, 10));
                    contentStream.newLineAtOffset(colWidth, 0);
                    contentStream.showText(centerText(loginText, colWidth, PDType1Font.HELVETICA, 10));

                    contentStream.endText();
                    rowY -= rowHeight;
                }
            }

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
