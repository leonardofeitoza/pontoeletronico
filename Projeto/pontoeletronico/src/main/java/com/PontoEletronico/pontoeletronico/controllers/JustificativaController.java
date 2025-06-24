package com.PontoEletronico.pontoeletronico.controllers;

import com.PontoEletronico.pontoeletronico.models.Justificativa;
import com.PontoEletronico.pontoeletronico.services.JustificativaService;
import com.PontoEletronico.pontoeletronico.repositories.JustificativaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/justificativa")
public class JustificativaController {

    @Autowired
    private JustificativaService justificativaService;

    @Autowired
    private JustificativaRepository justificativaRepository;

    @PostMapping("/registrar")
    public ResponseEntity<String> registrarJustificativa(@RequestBody Justificativa justificativa) {
        try {
            // Verifica se já existe justificativa cadastrada na mesma data para o funcionário
            List<Justificativa> existente = justificativaRepository.findByCodFuncionarioAndDataJustificativa(
                    justificativa.getCodFuncionario(), justificativa.getDataJustificativa());

            if (!existente.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Já existe uma justificativa cadastrada para este funcionário nesta data.");
            }

            // Caso contrário, salva normalmente
            justificativaService.salvarJustificativa(justificativa);
            return ResponseEntity.ok("Justificativa registrada com sucesso!");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao registrar justificativa: " + e.getMessage());
        }
    }



    @GetMapping("/verificar/{codFuncionario}/data/{dataJustificativa}")
    public ResponseEntity<?> verificarJustificativaExistente(
            @PathVariable Integer codFuncionario,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataJustificativa) {
        List<Justificativa> justificativas = justificativaRepository
                .findByCodFuncionarioAndDataJustificativa(codFuncionario, dataJustificativa);

        if (!justificativas.isEmpty()) {
            return ResponseEntity.ok(justificativas.get(0)); // Retorna 200 OK se já existir uma justificativa
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma justificativa encontrada.");
        }
    }


    @GetMapping("/{codFuncionario}/data/{dataJustificativa}")
    public ResponseEntity<?> buscarPorFuncionarioEData(
            @PathVariable Integer codFuncionario,
            @PathVariable String dataJustificativa) {
        try {
            LocalDate data = LocalDate.parse(dataJustificativa);
            List<Justificativa> justificativas = justificativaRepository.findByCodFuncionarioAndDataJustificativaBetweenOrderByDataJustificativaAsc(
                    codFuncionario, data, data);

            if (justificativas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma justificativa encontrada para a data especificada.");
            }

            return ResponseEntity.ok(justificativas.get(0)); // Considera que há apenas uma justificativa por data
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao buscar justificativa: " + e.getMessage());
        }
    }

    @PutMapping("/atualizar")
    public ResponseEntity<String> atualizarJustificativa(@RequestBody Justificativa justificativa) {
        try {
            justificativaService.atualizarJustificativa(justificativa);
            return ResponseEntity.ok("Justificativa atualizada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar justificativa: " + e.getMessage());
        }
    }

    @DeleteMapping("/excluir/{codJustificativa}")
    public ResponseEntity<Map<String, String>> excluirJustificativa(@PathVariable Integer codJustificativa) {
        Map<String, String> resposta = new HashMap<>();

        try {
            System.out.println("CodJustificativa recebido para exclusão: " + codJustificativa);

            // Verifica se o ID existe antes de tentar excluir
            if (!justificativaRepository.existsById(codJustificativa)) {
                resposta.put("mensagem", "Justificativa não encontrada.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
            }

            // Exclui a justificativa
            justificativaService.excluirJustificativa(codJustificativa);

            System.out.println("Justificativa excluída com sucesso!");
            resposta.put("mensagem", "Justificativa excluída com sucesso!");
            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            e.printStackTrace();
            resposta.put("mensagem", "Erro ao excluir justificativa: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
        }
    }


    // Lista funcionários que possuem justificativas
    @GetMapping("/funcionarios-com-justificativa")
    public ResponseEntity<List<Map<String, Object>>> listarFuncionariosComJustificativa() {
        List<Object[]> resultado = justificativaRepository.findDistinctFuncionarios();
        List<Map<String, Object>> lista = new ArrayList<>();

        for (Object[] obj : resultado) {
            Map<String, Object> funcionario = new HashMap<>();
            // Formata o código do funcionário para três dígitos (exemplo: 1 -> "001", 10 -> "010")
            funcionario.put("id", String.format("%03d", obj[0]));
            funcionario.put("nome", obj[1]);
            lista.add(funcionario);
        }

        return ResponseEntity.ok(lista);
    }


    @GetMapping("/listar/{codFuncionario}")
    public ResponseEntity<List<Map<String, Object>>> listarJustificativasPorFuncionario(@PathVariable Integer codFuncionario) {
        List<Justificativa> justificativas = justificativaService.listarJustificativasPorFuncionario(codFuncionario);

        if (justificativas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
        }

        List<Map<String, Object>> response = justificativas.stream().map(just -> {
            Map<String, Object> map = new HashMap<>();
            map.put("codJustificativa", just.getCodJustificativa());
            // Formata o código do funcionário para três dígitos, por exemplo: 1 -> "001", 10 -> "010"
            map.put("codFuncionario", String.format("%03d", just.getCodFuncionario()));
            map.put("dataJustificativa", just.getDataJustificativa());
            map.put("motivo", just.getMotivo());
            map.put("tipoJustificativa", just.getTipoJustificativa());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    /*// Retorna todas as justificativas de um funcionário específico
    @GetMapping("/funcionario/{id}")
    public ResponseEntity<List<Justificativa>> buscarJustificativasPorFuncionario(@PathVariable Integer id) {
        List<Justificativa> justificativas = justificativaRepository.findByCodFuncionario(id);
        return ResponseEntity.ok(justificativas);
    }*/

}
