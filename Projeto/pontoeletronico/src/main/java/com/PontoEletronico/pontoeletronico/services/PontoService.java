package com.PontoEletronico.pontoeletronico.services;

import com.PontoEletronico.pontoeletronico.models.Ponto;
import com.PontoEletronico.pontoeletronico.repositories.PontoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class PontoService {

    @Autowired
    private PontoRepository pontoRepository;

    /**
     * Registra um ponto para um funcionário.
     *
     * @param ponto Objeto Ponto a ser salvo.
     * @return Ponto salvo no banco de dados.
     */
    public Ponto registrarPonto(Ponto ponto) {
        return pontoRepository.save(ponto);
    }

    /**
     * Busca os pontos de um funcionário em uma data específica.
     *
     * @param codFuncionario ID do funcionário.
     * @param data Data do ponto.
     * @return Lista de pontos do funcionário.
     */
    public List<Ponto> buscarPontosPorData(int codFuncionario, LocalDate data) {
        return pontoRepository.findByFuncionarioCodFuncionarioAndData(codFuncionario, data);
    }

    /**
     * Busca todos os pontos de um funcionário.
     *
     * @param codFuncionario ID do funcionário.
     * @return Lista de pontos do funcionário.
     */
    public List<Ponto> buscarPontosPorFuncionario(int codFuncionario) {
        return pontoRepository.findByFuncionarioCodFuncionario(codFuncionario);
    }

    @Autowired
    private RegistroPontoService registroPontoService;

    public void atualizarPonto(Integer codFuncionario, LocalDate data, LocalTime horaEntrada, LocalTime horaSaida) throws Exception {
        // Busca o ponto existente
        List<Ponto> pontos = pontoRepository.findByFuncionarioCodFuncionarioAndData(codFuncionario, data);
        if (pontos.isEmpty()) {
            throw new Exception("Ponto não encontrado para o funcionário e data especificados.");
        }

        // Atualiza os campos necessários
        Ponto ponto = pontos.get(0); // Considerando que há apenas um ponto por data
        ponto.setHoraEntrada(horaEntrada);
        ponto.setHoraSaida(horaSaida);

        // Realiza o cálculo de horas trabalhadas e extras
        double[] horasCalculadas = registroPontoService.calcularHorasTrabalhadasEExtras(
                data.toString(),
                horaEntrada.toString(),
                horaSaida.toString(),
                obterCodTurno(codFuncionario) // Método para obter o turno do funcionário
        );

        // Atualiza os valores no ponto
        ponto.setTotalHorasTrabalhadas(horasCalculadas[0]);
        ponto.setTotalHorasExtras(horasCalculadas[1]);

        // Salva as alterações no banco de dados
        pontoRepository.save(ponto);
    }

    private int obterCodTurno(Integer codFuncionario) {
        // Implementar a lógica para buscar o turno do funcionário com base no codFuncionario.
        // Este é um exemplo básico:
        return 1; // Exemplo fixo, deve ser substituído pela lógica real.
    }

    public boolean excluirPonto(int codFuncionario, LocalDate data) {
        List<Ponto> pontos = pontoRepository.findByFuncionarioCodFuncionarioAndData(codFuncionario, data);

        if (pontos.isEmpty()) {
            return false; // Retorna falso se não encontrou o ponto
        }

        pontoRepository.delete(pontos.get(0)); // Exclui o primeiro ponto encontrado

        // Verifica se o ponto ainda existe no banco (para confirmar a exclusão)
        boolean aindaExiste = pontoRepository.findByFuncionarioCodFuncionarioAndData(codFuncionario, data).isEmpty();

        return aindaExiste; // Retorna true se foi realmente excluído
    }


}


