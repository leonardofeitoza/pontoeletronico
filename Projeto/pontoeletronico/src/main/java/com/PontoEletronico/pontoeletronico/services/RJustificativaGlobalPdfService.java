package com.PontoEletronico.pontoeletronico.services;

import com.PontoEletronico.pontoeletronico.models.Justificativa;
import com.PontoEletronico.pontoeletronico.repositories.JustificativaRepository;
import com.PontoEletronico.pontoeletronico.services.FuncionarioService;
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
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Service
public class RJustificativaGlobalPdfService {

    @Autowired
    private JustificativaRepository justificativaRepository;

    @Autowired
    private FuncionarioService funcionarioService;

    /**
     * Gera o relatório geral de justificativas em PDF.
     * Se dataInicial e dataFinal forem informadas, filtra os registros para o período;
     * caso contrário, usa todos os registros (desde o início até a data atual).
     */
    public byte[] gerarRelatorioJustificativaGlobal(String dataInicial, String dataFinal) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate startDate = (dataInicial != null && !dataInicial.isEmpty()) ? LocalDate.parse(dataInicial, inputFormatter) : null;
        LocalDate endDate = (dataFinal != null && !dataFinal.isEmpty()) ? LocalDate.parse(dataFinal, inputFormatter) : null;

        // Busca todas as justificativas
        List<Justificativa> justificativas = justificativaRepository.findAll();

        // Filtra por intervalo de datas se informado
        if (startDate != null && endDate != null) {
            justificativas = justificativas.stream()
                    .filter(j -> !j.getDataJustificativa().isBefore(startDate) && !j.getDataJustificativa().isAfter(endDate))
                    .collect(Collectors.toList());
        }

        // Se não houver registros, gera um PDF com mensagem de aviso
        if (justificativas.isEmpty()) {
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);
                float yPosition = page.getMediaBox().getHeight() - 50;
                try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                    cs.newLineAtOffset(50, yPosition);
                    cs.showText("RELATÓRIO DE JUSTIFICATIVA GLOBAL");
                    cs.endText();
                    yPosition -= 30;
                    if (startDate != null && endDate != null) {
                        cs.beginText();
                        cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
                        String periodo = "Período: " + startDate.format(outputFormatter) + " a " + endDate.format(outputFormatter);
                        cs.newLineAtOffset(50, yPosition);
                        cs.showText(periodo);
                        cs.endText();
                        yPosition -= 30;
                    }
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 12);
                    cs.newLineAtOffset(50, yPosition);
                    cs.showText("Nenhuma justificativa encontrada para o período informado.");
                    cs.endText();
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                document.save(out);
                return out.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage(), e);
            }
        }

        // Agrupa as justificativas por funcionário (chave: codFuncionario)
        Map<Integer, List<Justificativa>> grupos = justificativas.stream()
                .collect(Collectors.groupingBy(Justificativa::getCodFuncionario, LinkedHashMap::new, Collectors.toList()));

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            float yPosition;
            // Cria o cabeçalho do documento
            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                // Tenta adicionar logo
                InputStream logoStream = getClass().getResourceAsStream("/static/img/logo.png");
                if (logoStream != null) {
                    PDImageXObject logo = PDImageXObject.createFromByteArray(document, logoStream.readAllBytes(), "logo");
                    float logoWidth = 70;
                    float logoHeight = logo.getHeight() * (logoWidth / logo.getWidth());
                    float logoX = (page.getMediaBox().getWidth() - logoWidth) / 2;
                    float logoY = page.getMediaBox().getHeight() - logoHeight - 50;
                    cs.drawImage(logo, logoX, logoY, logoWidth, logoHeight);
                    yPosition = logoY - 20;
                } else {
                    yPosition = page.getMediaBox().getHeight() - 50;
                }
                // Título do relatório
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                String titulo = "RELATÓRIO DE JUSTIFICATIVA GLOBAL";
                float tituloWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(titulo) / 1000 * 16;
                cs.newLineAtOffset((page.getMediaBox().getWidth() - tituloWidth) / 2, yPosition);
                cs.showText(titulo);
                cs.endText();
                // Exibe o período, se definido
                if (startDate != null && endDate != null) {
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    String periodo = "Período: " + startDate.format(outputFormatter) + " a " + endDate.format(outputFormatter);
                    cs.newLineAtOffset(50, yPosition - 30);
                    cs.showText(periodo);
                    cs.endText();
                    yPosition -= 50;
                } else {
                    yPosition -= 30;
                }
            }

            // Variáveis de layout para os dados
            float margemEsquerda = 50;
            float larguraPagina = page.getMediaBox().getWidth();
            float larguraConteudo = larguraPagina - 2 * margemEsquerda;
            float espacoEntreLinhas = 15;

            // Para cada grupo de justificativas por funcionário
            for (Map.Entry<Integer, List<Justificativa>> entry : grupos.entrySet()) {
                int codFuncionario = entry.getKey();
                String nomeFuncionario = funcionarioService.buscarNomeFuncionario(codFuncionario);
                List<Justificativa> lista = entry.getValue();

                if (yPosition < 100) {
                    PDPage newPage = new PDPage();
                    document.addPage(newPage);
                    yPosition = newPage.getMediaBox().getHeight() - 50;
                    page = newPage;
                }

                // Escreve o cabeçalho do funcionário e o cabeçalho da tabela
                try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    cs.newLineAtOffset(margemEsquerda, yPosition);
                    cs.showText("Funcionário: " + nomeFuncionario + " (ID: " + codFuncionario + ")");
                    cs.endText();
                    yPosition -= espacoEntreLinhas * 2;

                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
                    cs.newLineAtOffset(margemEsquerda, yPosition);
                    cs.showText("Data");
                    cs.newLineAtOffset(120, 0); // Coluna para Motivo
                    cs.showText("Motivo");
                    cs.newLineAtOffset(250, 0); // Coluna para Tipo
                    cs.showText("Tipo");
                    cs.endText();
                    // Adiciona um espaçamento extra (5 pontos) entre o cabeçalho da tabela e a primeira linha dos dados
                    yPosition -= espacoEntreLinhas;
                }

                yPosition -= espacoEntreLinhas -5;

                // Inicializa para que a primeira linha de dados tenha fundo cinza
                boolean isGray = true;
                // Escreve os dados das justificativas com fundo intercalado
                for (Justificativa j : lista) {
                    if (yPosition < 50) {
                        PDPage newPage = new PDPage();
                        document.addPage(newPage);
                        yPosition = newPage.getMediaBox().getHeight() - 50;
                        page = newPage;
                    }
                    try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                        if (isGray) {
                            cs.setNonStrokingColor(new Color(230, 230, 230));
                            cs.addRect(margemEsquerda, yPosition - 3, larguraConteudo, espacoEntreLinhas + 5);
                            cs.fill();
                            cs.setNonStrokingColor(Color.BLACK);
                        }
                        cs.beginText();
                        cs.setFont(PDType1Font.HELVETICA, 10);
                        // Ajusta verticalmente para centralizar o texto na linha
                        cs.newLineAtOffset(margemEsquerda, yPosition + (espacoEntreLinhas * 0.25f));
                        cs.showText(j.getDataJustificativa().format(outputFormatter));
                        cs.newLineAtOffset(120, 0);
                        cs.showText(j.getMotivo());
                        cs.newLineAtOffset(250, 0);
                        cs.showText(j.getTipoJustificativa());
                        cs.endText();
                        yPosition -= espacoEntreLinhas + 5;
                        isGray = !isGray;
                    }
                }
                // Adiciona espaço extra entre grupos de funcionários
                try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                    yPosition -= espacoEntreLinhas * 2;
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar relatório de justificativa global", e);
        }
    }
}
