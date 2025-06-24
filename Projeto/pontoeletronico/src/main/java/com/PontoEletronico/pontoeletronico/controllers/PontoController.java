package com.PontoEletronico.pontoeletronico.controllers;

import com.PontoEletronico.pontoeletronico.dto.AtualizarPontoDTO;
import com.PontoEletronico.pontoeletronico.models.Ponto;
import com.PontoEletronico.pontoeletronico.repositories.PontoRepository;
import com.PontoEletronico.pontoeletronico.services.PontoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pontos")
public class PontoController {

    @Autowired
    private PontoService pontoService;

    @Autowired
    private PontoRepository pontoRepository;


    /**
     * Endpoint para registrar um ponto.
     *
     * @param ponto Objeto Ponto enviado no corpo da requisição.
     * @return Ponto registrado.
     */
    @PostMapping
    public Ponto registrarPonto(@RequestBody Ponto ponto) {
        return pontoService.registrarPonto(ponto);
    }

    /**
     * Endpoint para buscar todos os pontos de um funcionário.
     *
     * @param codFuncionario ID do funcionário.
     * @return Lista de pontos do funcionário.
     */
    @GetMapping("/{codFuncionario}")
    public List<Ponto> buscarPontosPorFuncionario(@PathVariable int codFuncionario) {
        return pontoService.buscarPontosPorFuncionario(codFuncionario);
    }

    @GetMapping("/{codFuncionario}/data/{data}")
    public ResponseEntity<?> buscarPontosPorData(@PathVariable int codFuncionario, @PathVariable String data) {
        List<Ponto> pontos = pontoService.buscarPontosPorData(codFuncionario, LocalDate.parse(data));
        if (pontos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Nenhum ponto encontrado para a data especificada.");
        }
        return ResponseEntity.ok(pontos);
    }

    @PutMapping("/atualizar")
    public ResponseEntity<String> atualizarPonto(@RequestBody AtualizarPontoDTO alterarPontoDTO) {
        try {
            pontoService.atualizarPonto(
                    alterarPontoDTO.getCodFuncionario(),
                    alterarPontoDTO.getData(),
                    alterarPontoDTO.getHoraEntrada(),
                    alterarPontoDTO.getHoraSaida()
            );
            return ResponseEntity.ok("Ponto atualizado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao atualizar ponto: " + e.getMessage());
        }
    }

    @DeleteMapping("/{codFuncionario}/data/{data}")
    public ResponseEntity<String> excluirPontoPorData(@PathVariable int codFuncionario, @PathVariable String data) {
        try {
            boolean excluido = pontoService.excluirPonto(codFuncionario, LocalDate.parse(data));

            if (excluido) {
                return ResponseEntity.ok("Ponto excluído com sucesso!");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ponto não encontrado para exclusão.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir ponto: " + e.getMessage());
        }
    }


    @GetMapping("/funcionarios-com-ponto")
    public ResponseEntity<List<Map<String, Object>>> listarFuncionariosComPonto() {
        List<Object[]> funcionarios = pontoRepository.findFuncionariosComPonto();

        if (funcionarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Object[] obj : funcionarios) {
            Map<String, Object> funcionario = new HashMap<>();
            // Formata o ID para três dígitos com zeros à esquerda
            Integer id = (Integer) obj[0];
            String idFormatado = String.format("%03d", id);
            funcionario.put("id", idFormatado);
            funcionario.put("nome", obj[1]); // Apenas o nome será mostrado
            resultado.add(funcionario);
        }

        return ResponseEntity.ok(resultado);
    }

}

