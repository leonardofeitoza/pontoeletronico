package com.PontoEletronico.pontoeletronico.services;

import com.PontoEletronico.pontoeletronico.models.Justificativa;
import com.PontoEletronico.pontoeletronico.repositories.JustificativaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class JustificativaService {

    @Autowired
    private JustificativaRepository justificativaRepository;

    public void salvarJustificativa(Justificativa justificativa) throws Exception {
        List<Justificativa> justificativasExistentes = justificativaRepository
                .findByCodFuncionarioAndDataJustificativa(justificativa.getCodFuncionario(), justificativa.getDataJustificativa());

        if (!justificativasExistentes.isEmpty()) {
            throw new Exception("Já existe uma justificativa para este funcionário nesta data.");
        }

        justificativaRepository.save(justificativa);
    }

    public void atualizarJustificativa(Justificativa justificativa) throws Exception {
        // Busca a justificativa com base em CodFuncionario e DataJustificativa
        List<Justificativa> justificativas = justificativaRepository
                .findByCodFuncionarioAndDataJustificativa(justificativa.getCodFuncionario(), justificativa.getDataJustificativa());

        // Verifica se há exatamente um registro encontrado
        if (justificativas.isEmpty()) {
            throw new Exception("Justificativa não encontrada para o funcionário e data especificados.");
        } else if (justificativas.size() > 1) {
            throw new Exception("Mais de uma justificativa encontrada para o funcionário e data especificados.");
        }

        // Obtém a justificativa existente
        Justificativa justificativaExistente = justificativas.get(0);

        // Atualiza os campos necessários
        justificativaExistente.setMotivo(justificativa.getMotivo());
        justificativaExistente.setTipoJustificativa(justificativa.getTipoJustificativa());

        // Salva as alterações no banco
        justificativaRepository.save(justificativaExistente);
    }

    public void excluirJustificativa(Integer codJustificativa) {
        justificativaRepository.deleteById(codJustificativa);
    }

    public List<Justificativa> listarJustificativasPorFuncionario(Integer codFuncionario) {
        return justificativaRepository.findByCodFuncionarioOrderByDataJustificativaAsc(codFuncionario);
    }

}
