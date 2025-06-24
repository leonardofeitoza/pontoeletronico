package com.PontoEletronico.pontoeletronico.controllers;

import com.PontoEletronico.pontoeletronico.models.Turno;
import com.PontoEletronico.pontoeletronico.services.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turnos")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    /**
     * Endpoint para listar todos os turnos.
     *
     * @return Lista de turnos cadastrados.
     */
    @GetMapping
    public List<Turno> listarTodos() {
        return turnoService.listarTodos();
    }

    /**
     * Endpoint para cadastrar um novo turno.
     *
     * @param turno Objeto Turno enviado no corpo da requisição.
     * @return Turno cadastrado.
     */
    @PostMapping
    public Turno cadastrarTurno(@RequestBody Turno turno) {
        return turnoService.cadastrarTurno(turno);
    }

    /**
     * Endpoint para atualizar um turno existente.
     *
     * @param turno Objeto Turno com os dados atualizados.
     * @return Turno atualizado.
     */
    @PutMapping
    public Turno atualizarTurno(@RequestBody Turno turno) {
        return turnoService.atualizarTurno(turno);
    }

    /**
     * Endpoint para remover um turno pelo ID.
     *
     * @param codTurno ID do turno a ser removido.
     * @return Mensagem de sucesso após a remoção.
     */
    @DeleteMapping("/{codTurno}")
    public String removerTurno(@PathVariable int codTurno) {
        turnoService.removerTurno(codTurno);
        return "Turno com ID " + codTurno + " removido com sucesso!";
    }
}

