package com.PontoEletronico.pontoeletronico.services;

import com.PontoEletronico.pontoeletronico.models.Funcionario;
import com.PontoEletronico.pontoeletronico.models.Turno;
import com.PontoEletronico.pontoeletronico.repositories.FuncionarioRepository;
import com.PontoEletronico.pontoeletronico.repositories.PontoRepository;
import com.PontoEletronico.pontoeletronico.repositories.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FuncionarioService {

    @Autowired
    private TurnoRepository turnoRepository;

    private final FuncionarioRepository funcionarioRepository;
    private final PontoRepository pontoRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository, PontoRepository pontoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.pontoRepository = pontoRepository;
    }

    public Funcionario buscarFuncionarioPorCpf(String cpf) {
        return funcionarioRepository.findByCpf(cpf);
    }

    public String buscarNomeFuncionario(Integer codFuncionario) {
        Funcionario funcionario = funcionarioRepository.findById(codFuncionario)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com o ID: " + codFuncionario));
        return funcionario.getNome();
    }

    public Funcionario cadastrarFuncionario(Funcionario funcionario) {
        validarFuncionario(funcionario);

        if (funcionarioRepository.existsByCpf(funcionario.getCpf())) {
            throw new IllegalArgumentException("O CPF informado já está cadastrado no sistema.");
        }

        java.time.LocalDate dataAtual = java.time.LocalDate.now();
        if (funcionario.getDataAdmissao().compareTo(String.valueOf(dataAtual)) > 0) {
            throw new IllegalArgumentException("A data de admissão não pode ser superior à data atual.");
        }

        Turno turno = funcionario.getTurno();
        if (turno != null) {
            Turno turnoExistente = turnoRepository.findByDescricaoTurno(turno.getDescricaoTurno());
            if (turnoExistente != null) {
                funcionario.setTurno(turnoExistente);
            } else {
                Turno novoTurno = turnoRepository.save(turno);
                funcionario.setTurno(novoTurno);
            }
        }

        return funcionarioRepository.save(funcionario);
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    public Funcionario atualizarFuncionario(Funcionario funcionario) {
        Funcionario funcionarioExistente = funcionarioRepository.findByCpf(funcionario.getCpf());
        if (funcionarioExistente == null) {
            throw new IllegalArgumentException("Funcionário não encontrado com o CPF: " + funcionario.getCpf());
        }

        funcionarioExistente.setNome(funcionario.getNome());
        funcionarioExistente.setCargo(funcionario.getCargo());
        funcionarioExistente.setSetor(funcionario.getSetor());
        funcionarioExistente.setDataAdmissao(funcionario.getDataAdmissao());
        funcionarioExistente.setSalario(funcionario.getSalario());
        funcionarioExistente.setFoto(funcionario.getFoto());

        if (funcionario.getTurno() != null) {
            Turno turno = turnoRepository.findByDescricaoTurno(funcionario.getTurno().getDescricaoTurno());
            if (turno != null) {
                funcionarioExistente.setTurno(turno);
            } else {
                throw new IllegalArgumentException("Turno não encontrado: " + funcionario.getTurno().getDescricaoTurno());
            }
        }

        return funcionarioRepository.save(funcionarioExistente);
    }

    public void removerFuncionario(int codFuncionario) {
        if (funcionarioRepository.existsById(codFuncionario)) {
            funcionarioRepository.deleteById(codFuncionario);
        } else {
            throw new IllegalArgumentException("Funcionário não encontrado para remoção.");
        }
    }

    private void validarFuncionario(Funcionario funcionario) {
        if (funcionario.getCpf() == null || funcionario.getCpf().length() != 11) {
            throw new IllegalArgumentException("CPF inválido. Deve conter 11 dígitos.");
        }
        if (funcionario.getNome() == null || funcionario.getNome().isEmpty()) {
            throw new IllegalArgumentException("Nome do funcionário não pode estar vazio.");
        }
        if (funcionario.getTurno() == null) {
            throw new IllegalArgumentException("O turno do funcionário não pode estar vazio.");
        }
    }

    public void removerFuncionarioPorNome(String nome) {
        Funcionario funcionario = buscarPorNome(nome);
        if (funcionario == null) {
            throw new IllegalArgumentException("Funcionário não encontrado com o nome: " + nome);
        }
        funcionarioRepository.deleteById(funcionario.getCodFuncionario());
    }

    public Funcionario buscarPorNome(String nome) {
        return funcionarioRepository.findByNomeWithTurno(nome);
    }

    @Transactional
    public void excluirFuncionarioPorCpf(String cpf) {
        Funcionario funcionario = funcionarioRepository.findByCpf(cpf);
        if (funcionario == null) {
            throw new IllegalArgumentException("Funcionário não encontrado com o CPF: " + cpf);
        }

        if (pontoRepository.existsByFuncionario_Cpf(cpf)) {
            throw new IllegalStateException("Não foi possível excluir o funcionário por existir registros vinculados.");
        }

        try {
            funcionarioRepository.delete(funcionario);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Não foi possível excluir o funcionário por existir registros vinculados.");
        }
    }
}