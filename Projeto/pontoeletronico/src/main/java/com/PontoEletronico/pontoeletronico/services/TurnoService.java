package com.PontoEletronico.pontoeletronico.services;

import com.PontoEletronico.pontoeletronico.models.Turno;
import com.PontoEletronico.pontoeletronico.repositories.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurnoService {

    @Autowired
    private TurnoRepository turnoRepository;

    /**
     * Cadastra um novo turno.
     *
     * @param turno Objeto Turno a ser salvo.
     * @return Turno salvo no banco de dados.
     */
    public Turno cadastrarTurno(Turno turno) {
        if (turnoRepository.existsByDescricaoTurno(turno.getDescricaoTurno())) {
            throw new IllegalArgumentException("Erro: Turno com essa descrição já existe.");
        }
        return turnoRepository.save(turno);
    }

    /**
     * Lista todos os turnos cadastrados.
     *
     * @return Lista de turnos.
     */
    public List<Turno> listarTodos() {
        return turnoRepository.findAll();
    }

    /**
     * Atualiza as informações de um turno existente.
     *
     * @param turno Objeto Turno com os novos dados.
     * @return Turno atualizado.
     */
    public Turno atualizarTurno(Turno turno) {
        if (!turnoRepository.existsById(turno.getCodTurno())) {
            throw new IllegalArgumentException("Erro: Turno não encontrado para atualização.");
        }
        return turnoRepository.save(turno);
    }

    /**
     * Remove um turno pelo ID.
     *
     * @param codTurno ID do turno a ser removido.
     */
    public void removerTurno(int codTurno) {
        if (!turnoRepository.existsById(codTurno)) {
            throw new IllegalArgumentException("Erro: Turno não encontrado para remoção.");
        }
        turnoRepository.deleteById(codTurno);
    }
}

