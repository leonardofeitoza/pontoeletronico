package com.PontoEletronico.pontoeletronico.services;

import com.PontoEletronico.pontoeletronico.models.Funcionario;
import com.PontoEletronico.pontoeletronico.repositories.FuncionarioRepository;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class RFuncionarioGlobalPdfService {

    @Autowired
    private FuncionarioRepository funcionarioRepository; // Repositório para buscar dados dos funcionários

    public byte[] gerarRelatorioGlobal() {
        // Obtém todos os funcionários com informações de turno
        List<Funcionario> funcionarios = funcionarioRepository.findAllWithTurno();

        try (PDDocument document = new PDDocument()) { // Cria um novo documento PDF
            PDPage page = new PDPage(); // Adiciona uma nova página ao documento
            document.addPage(page);

            // Cria um fluxo de conteúdo para escrever na página
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Define margens e posições iniciais
            float marginTop = page.getMediaBox().getHeight() - 60;
            float marginBottom = 50;
            float marginLeft = 50;
            float marginRight = page.getMediaBox().getWidth() - 50;
            float currentY = marginTop; // Posição vertical atual

            int pageNumber = 1; // Inicializa o número da página

            try {
                // Adiciona uma imagem de logo no topo da página
                InputStream logoStream = getClass().getResourceAsStream("/static/img/logo.png");
                if (logoStream == null) {
                    throw new RuntimeException("Logo não encontrado no caminho /static/img/logo.png");
                }
                PDImageXObject logo = PDImageXObject.createFromByteArray(document, logoStream.readAllBytes(), "logo");

                float logoWidth = 70; // Largura da logo
                float logoHeight = logo.getHeight() * (logoWidth / logo.getWidth()); // Altura proporcional
                float logoX = (page.getMediaBox().getWidth() - logoWidth) / 2; // Centraliza a logo horizontalmente
                float logoY = currentY - logoHeight; // Define a posição da logo verticalmente

                contentStream.drawImage(logo, logoX, logoY, logoWidth, logoHeight);
                currentY -= (logoHeight + 50); // Move a posição vertical abaixo da logo

                // Adiciona o título do relatório
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16); // Define fonte e tamanho
                float titleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth("RELATÓRIO GLOBAL DE FUNCIONÁRIO") / 1000 * 16;
                contentStream.newLineAtOffset((page.getMediaBox().getWidth() - titleWidth) / 2, currentY); // Centraliza o texto
                contentStream.showText("RELATÓRIO GLOBAL DE FUNCIONÁRIO"); // Escreve o título
                contentStream.endText();
                currentY -= 50; // Move a posição para o próximo conteúdo

                // Inicia o bloco de texto para listar os funcionários
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12); // Define fonte para o conteúdo
                contentStream.setLeading(15f); // Define o espaçamento entre linhas
                contentStream.newLineAtOffset(marginLeft, currentY); // Define o ponto inicial do texto

                for (Funcionario funcionario : funcionarios) {
                    // Verifica se o espaço restante é suficiente para exibir mais informações
                    if (currentY <= marginBottom) {
                        contentStream.endText(); // Finaliza o texto da página atual
                        adicionarNumeroPagina(contentStream, pageNumber, page, marginLeft); // Adiciona o número da página
                        contentStream.close(); // Fecha o fluxo de conteúdo
                        page = new PDPage(); // Cria uma nova página
                        document.addPage(page); // Adiciona a nova página ao documento
                        contentStream = new PDPageContentStream(document, page); // Cria um novo fluxo de conteúdo
                        currentY = marginTop; // Reinicia a posição vertical
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA, 12); // Redefine fonte
                        contentStream.setLeading(15f); // Redefine espaçamento
                        contentStream.newLineAtOffset(marginLeft, currentY); // Redefine posição inicial
                        pageNumber++; // Incrementa o número da página
                    }

                    // Obtém as descrições do turno e data formatada
                    String turnoDescricao = funcionario.getTurno() != null
                            ? funcionario.getTurno().getDescricaoTurno()
                            : "Não informado";
                    String dataAdmissaoFormatada = formatarData(funcionario.getDataAdmissao());

                    // Escreve as informações do funcionário
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                    contentStream.showText("Nome: ");
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.showText(funcionario.getNome());
                    contentStream.newLine();

                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                    contentStream.showText("ID: ");
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    String idFormatado = String.format("%03d", funcionario.getCodFuncionario());
                    contentStream.showText(idFormatado);
                    contentStream.newLine();

                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                    contentStream.showText("Cargo: ");
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.showText(funcionario.getCargo());
                    contentStream.newLine();

                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                    contentStream.showText("Setor: ");
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.showText(funcionario.getSetor());
                    contentStream.newLine();

                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                    contentStream.showText("Salário: ");
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.showText("R$ " + String.format("%.2f", funcionario.getSalario()));
                    contentStream.newLine();

                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                    contentStream.showText("Data de Admissão: ");
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.showText(dataAdmissaoFormatada);
                    contentStream.newLine();

                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                    contentStream.showText("Turno: ");
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.showText(turnoDescricao);
                    contentStream.newLine();

                    contentStream.showText("-----------------------------------------------------------------------------------------------------------------------------------------------------------");
                    contentStream.newLine();

                    currentY -= 140; // Atualiza a posição vertical
                }

                contentStream.endText(); // Finaliza o bloco de texto
                adicionarNumeroPagina(contentStream, pageNumber, page, marginLeft); // Adiciona o número da última página
                contentStream.close(); // Fecha o fluxo de conteúdo
            } catch (IOException e) {
                throw new RuntimeException(e); // Lança exceção em caso de erro
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream(); // Cria um fluxo para salvar o PDF
            document.save(out); // Salva o documento no fluxo
            return out.toByteArray(); // Retorna o PDF como array de bytes
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar relatório PDF", e); // Lança exceção em caso de erro
        }
    }

    // Método para formatar a data
    private String formatarData(Object dataAdmissao) {
        if (dataAdmissao == null) {
            return "Não informada";
        }

        try {
            if (dataAdmissao instanceof Date) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                return formatter.format((Date) dataAdmissao);
            } else if (dataAdmissao instanceof LocalDate) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                return ((LocalDate) dataAdmissao).format(formatter);
            } else if (dataAdmissao instanceof String) {
                Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse((String) dataAdmissao);
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                return formatter.format(parsedDate);
            } else {
                return "Formato inválido";
            }
        } catch (Exception e) {
            return "Formato inválido";
        }
    }

    // Método para adicionar o número da página no rodapé
    private void adicionarNumeroPagina(PDPageContentStream contentStream, int pageNumber, PDPage page, float marginLeft) {
        try {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            String pageText = "Página " + pageNumber;
            float textWidth = PDType1Font.HELVETICA.getStringWidth(pageText) / 1000 * 10;
            float textX = (page.getMediaBox().getWidth() - textWidth) / 2; // Centraliza o texto
            float textY = 40; // Posição do rodapé
            contentStream.newLineAtOffset(textX, textY);
            contentStream.showText(pageText);
            contentStream.endText();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao adicionar número da página", e);
        }
    }
}
