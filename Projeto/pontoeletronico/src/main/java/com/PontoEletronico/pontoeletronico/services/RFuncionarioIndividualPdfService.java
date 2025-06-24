package com.PontoEletronico.pontoeletronico.services;

import com.PontoEletronico.pontoeletronico.models.Funcionario;
import com.PontoEletronico.pontoeletronico.models.Turno;
import com.PontoEletronico.pontoeletronico.repositories.FuncionarioRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class RFuncionarioIndividualPdfService {

    // Adiciona o repositório com @Autowired
    @Autowired
    private FuncionarioRepository funcionarioRepository;

    /**
     * Gera um relatório individual de um funcionário.
     *
     * @param idFuncionario ID do funcionário.
     * @return Relatório em PDF no formato de bytes.
     */
    @Transactional(readOnly = true)
    public byte[] gerarRelatorioIndividual(Integer idFuncionario) {
        Funcionario funcionario = funcionarioRepository.findById(idFuncionario)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com o ID: " + idFuncionario));

        Turno turno = funcionario.getTurno();
        if (turno != null) {
            turno.getDescricaoTurno(); // Inicialização do proxy
        }

        // Formatar a data de admissão
        String dataAdmissaoFormatada = "Data não informada";
        if (funcionario.getDataAdmissao() != null) {
            Object dataAdmissao = funcionario.getDataAdmissao();

            if (dataAdmissao instanceof Date) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                dataAdmissaoFormatada = formatter.format((Date) dataAdmissao);
            } else if (dataAdmissao instanceof LocalDate) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                dataAdmissaoFormatada = ((LocalDate) dataAdmissao).format(formatter);
            } else if (dataAdmissao instanceof String) {
                try {
                    Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse((String) dataAdmissao);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    dataAdmissaoFormatada = formatter.format(parsedDate);
                } catch (Exception e) {
                    dataAdmissaoFormatada = "Formato de data inválido";
                }
            } else {
                throw new IllegalArgumentException("Tipo de dado inesperado para DataAdmissao: " + dataAdmissao.getClass().getName());
            }
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Adicionar a logo ao PDF com tamanho ajustado
                InputStream logoStream = getClass().getResourceAsStream("/static/img/logo.png");
                if (logoStream == null) {
                    throw new RuntimeException("Logo não encontrado no caminho /static/img/logo.png");
                }
                PDImageXObject logo = PDImageXObject.createFromByteArray(document, logoStream.readAllBytes(), "logo");


                // Ajustar dimensões da logo
                float logoWidth = 70; // Largura desejada
                float logoHeight = logo.getHeight() * (logoWidth / logo.getWidth()); // Altura proporcional

                // Centralizar logo na página
                float logoX = (page.getMediaBox().getWidth() - logoWidth) / 2;
                float logoY = page.getMediaBox().getHeight() - logoHeight - 50; // Ajuste da margem superior

                contentStream.drawImage(logo, logoX, logoY, logoWidth, logoHeight);

                // Adicionar o texto "PASSAGGIO" abaixo da logo
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                float passaggioTextWidth = PDType1Font.HELVETICA_BOLD.getStringWidth("PASSAGGIO") / 1000 * 16;
                contentStream.newLineAtOffset((page.getMediaBox().getWidth() - passaggioTextWidth) / 2, logoY - 20);
                contentStream.showText("PASSAGGIO");
                contentStream.endText();

                // Adicionar título do relatório abaixo do texto "PASSAGGIO"
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                float reportTitleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth("RELATÓRIO INDIVIDUAL DE FUNCIONÁRIO") / 1000 * 16;
                contentStream.newLineAtOffset((page.getMediaBox().getWidth() - reportTitleWidth) / 2, logoY - 50);
                contentStream.showText("RELATÓRIO INDIVIDUAL DE FUNCIONÁRIO");
                contentStream.endText();


                // Adicionar informações do funcionário
                float contentStartY = logoY - 100; // Começar abaixo do título
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.setLeading(20f); // Ajustar espaçamento entre linhas para melhorar a organização

                contentStream.newLineAtOffset(50, contentStartY); // Definir margem à esquerda

                // Adicionar uma linha de cabeçalho para destacar
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12); // Texto em negrito maior para categorias
                contentStream.showText("INFORMAÇÃO DO FUNCIONÁRIO:");
                contentStream.newLine();

                // Resetar para fonte normal
                contentStream.setFont(PDType1Font.HELVETICA, 10);

                // Adicionar informações detalhadas com palavras em negrito
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10); // Fonte negrito
                contentStream.showText("NOME: ");
                contentStream.setFont(PDType1Font.HELVETICA, 10); // Fonte normal
                contentStream.showText(funcionario.getNome().toUpperCase());
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                contentStream.showText("ID: ");
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                String idFormatado = String.format("%03d", funcionario.getCodFuncionario());
                contentStream.showText(idFormatado);
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                contentStream.showText("CARGO: ");
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.showText(funcionario.getCargo().toUpperCase());
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                contentStream.showText("SETOR: ");
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.showText(funcionario.getSetor().toUpperCase());
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                contentStream.showText("SALÁRIO: ");
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.showText("R$ " + String.format("%.2f", funcionario.getSalario()));
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                contentStream.showText("DATA DE ADMISSÃO: ");
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.showText(dataAdmissaoFormatada.toUpperCase());
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                contentStream.showText("TURNO: ");
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                if (turno != null) {
                    contentStream.showText(turno.getDescricaoTurno().toUpperCase());
                } else {
                    contentStream.showText("NÃO INFORMADO");
                }

                // Adicionar uma linha de rodapé opcional para separação
                contentStream.newLine();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                contentStream.showText("-----------------------------------------------------------------------------------------------------------------------------------------------------------");
                contentStream.newLine();

                contentStream.endText();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar o relatório PDF", e);
        }
    }
}
