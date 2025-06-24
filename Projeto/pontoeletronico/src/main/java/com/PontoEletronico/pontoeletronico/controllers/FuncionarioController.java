package com.PontoEletronico.pontoeletronico.controllers;

import com.PontoEletronico.pontoeletronico.dto.FuncionarioComFotoDTO;
import com.PontoEletronico.pontoeletronico.dto.FuncionarioResponseDTO;
import com.PontoEletronico.pontoeletronico.models.Funcionario;
import com.PontoEletronico.pontoeletronico.models.Turno;
import com.PontoEletronico.pontoeletronico.repositories.FuncionarioRepository;
import com.PontoEletronico.pontoeletronico.services.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private FuncionarioService funcionarioService;

    @GetMapping("/next-id")
    public ResponseEntity<String> getNextId() {
        Integer maxId = funcionarioRepository.findMaxCodFuncionario();
        int nextId = (maxId == null ? 1 : maxId + 1);
        return ResponseEntity.ok(String.format("%03d", nextId));
    }

    @GetMapping("/nomes-codigos")
    public ResponseEntity<List<Map<String, Object>>> listarNomesEIds() {
        List<Funcionario> funcionarios = funcionarioRepository.findAll();
        List<Map<String, Object>> resposta = new ArrayList<>();
        for (Funcionario funcionario : funcionarios) {
            Map<String, Object> mapa = new HashMap<>();
            mapa.put("nome", funcionario.getNome());
            mapa.put("codFuncionario", String.format("%03d", funcionario.getCodFuncionario()));
            resposta.add(mapa);
        }
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{cpf:[0-9]{11}}")
    public ResponseEntity<Funcionario> buscarPorCpf(@PathVariable String cpf) {
        Funcionario funcionario = funcionarioService.buscarFuncionarioPorCpf(cpf);
        return funcionario != null ? ResponseEntity.ok(funcionario) : ResponseEntity.notFound().build();
    }

    @GetMapping("/nomes")
    public ResponseEntity<List<String>> listarNomes() {
        return ResponseEntity.ok(funcionarioRepository.findAllNomes());
    }

    @GetMapping
    public ResponseEntity<List<Funcionario>> listarTodos() {
        List<Funcionario> funcionarios = funcionarioService.listarTodos();
        return ResponseEntity.ok(funcionarios);
    }

    @PostMapping
    public ResponseEntity<String> cadastrarFuncionario(
            @RequestParam("nome") String nome,
            @RequestParam("cpf") String cpf,
            @RequestParam("cargo") String cargo,
            @RequestParam("setor") String setor,
            @RequestParam("dataAdmissao") String dataAdmissao,
            @RequestParam("salario") Double salario,
            @RequestParam("turno") String turno,
            @RequestParam(value = "foto", required = false) MultipartFile foto) {
        try {
            if (cpf == null || cpf.isEmpty()) {
                return ResponseEntity.badRequest().body("O CPF é obrigatório.");
            }
            java.time.LocalDate dataAtual = java.time.LocalDate.now();
            if (dataAdmissao.compareTo(String.valueOf(dataAtual)) > 0) {
                return ResponseEntity.badRequest().body("A data de admissão não pode ser superior à data atual.");
            }
            if (cargo == null || cargo.isEmpty()) {
                return ResponseEntity.badRequest().body("O cargo é obrigatório.");
            }

            Funcionario funcionario = new Funcionario();
            funcionario.setNome(nome);
            funcionario.setCpf(cpf);
            funcionario.setCargo(cargo);
            funcionario.setSetor(setor);
            funcionario.setDataAdmissao(dataAdmissao);
            funcionario.setSalario(salario);

            // Configura o turno
            Turno turnoObj = new Turno();
            turnoObj.setDescricaoTurno(turno);
            funcionario.setTurno(turnoObj);

            // Configura a foto, se fornecida
            if (foto != null && !foto.isEmpty()) {
                try {
                    funcionario.setFoto(foto.getBytes());
                } catch (IOException e) {
                    return ResponseEntity.badRequest().body("Erro ao processar a imagem: " + e.getMessage());
                }
            }

            Funcionario novoFuncionario = funcionarioService.cadastrarFuncionario(funcionario);
            return ResponseEntity.ok("Funcionário cadastrado com sucesso! ID: " + novoFuncionario.getCodFuncionario());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao cadastrar funcionário: " + e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<String> atualizarFuncionario(
            @RequestParam("nome") String nome,
            @RequestParam("cpf") String cpf,
            @RequestParam("cpfOriginal") String cpfOriginal,
            @RequestParam("cargo") String cargo,
            @RequestParam("setor") String setor,
            @RequestParam("dataAdmissao") String dataAdmissao,
            @RequestParam("salario") Double salario,
            @RequestParam("turno") String turno,
            @RequestParam(value = "foto", required = false) MultipartFile foto) {
        try {
            Funcionario funcionario = funcionarioService.buscarFuncionarioPorCpf(cpfOriginal);
            if (funcionario == null) {
                return ResponseEntity.badRequest().body("Funcionário não encontrado.");
            }

            // Verificar se o CPF foi alterado e se já existe no banco para outro funcionário
            if (!cpf.equals(cpfOriginal)) {
                Funcionario funcionarioComNovoCpf = funcionarioService.buscarFuncionarioPorCpf(cpf);
                if (funcionarioComNovoCpf != null) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("O CPF informado já está cadastrado para outro funcionário no sistema.");
                }
            }

            funcionario.setNome(nome);
            funcionario.setCpf(cpf);
            funcionario.setCargo(cargo);
            funcionario.setSetor(setor);
            funcionario.setDataAdmissao(dataAdmissao);
            funcionario.setSalario(salario);

            // Configura o turno
            Turno turnoObj = funcionarioRepository.findByDescricaoTurno(turno);
            if (turnoObj == null) {
                turnoObj = new Turno();
                turnoObj.setDescricaoTurno(turno);
            }
            funcionario.setTurno(turnoObj);

            // Atualiza a foto, se fornecida
            if (foto != null && !foto.isEmpty()) {
                try {
                    funcionario.setFoto(foto.getBytes());
                } catch (IOException e) {
                    return ResponseEntity.badRequest().body("Erro ao processar a imagem: " + e.getMessage());
                }
            }

            funcionarioService.atualizarFuncionario(funcionario);
            return ResponseEntity.ok("Funcionário atualizado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar funcionário: " + e.getMessage());
        }
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<String> excluirFuncionarioPorCpf(@PathVariable String cpf) {
        try {
            funcionarioService.excluirFuncionarioPorCpf(cpf);
            return ResponseEntity.ok("Funcionário excluído com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao tentar excluir o funcionário.");
        }
    }

    @GetMapping("/todos")
    public ResponseEntity<List<FuncionarioComFotoDTO>> listarTodosFuncionarios() {
        List<Funcionario> funcionarios = funcionarioRepository.findAllWithTurno();
        List<FuncionarioComFotoDTO> resposta = funcionarios.stream()
                .map(funcionario -> new FuncionarioComFotoDTO(
                        String.format("%03d", funcionario.getCodFuncionario()),
                        funcionario.getNome(),
                        funcionario.getCpf(),
                        funcionario.getCargo(),
                        funcionario.getSetor(),
                        funcionario.getDataAdmissao(),
                        funcionario.getSalario(),
                        funcionario.getTurno() != null ? funcionario.getTurno().getDescricaoTurno() : null,
                        funcionario.getFoto() != null ? Base64.getEncoder().encodeToString(funcionario.getFoto()) : null))
                .toList();
        return ResponseEntity.ok(resposta);
    }
}