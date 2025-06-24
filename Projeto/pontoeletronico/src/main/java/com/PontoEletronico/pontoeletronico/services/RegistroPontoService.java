package com.PontoEletronico.pontoeletronico.services;

import com.PontoEletronico.pontoeletronico.models.RegistroPonto;
import com.PontoEletronico.pontoeletronico.repositories.PontoRepository;
import com.PontoEletronico.pontoeletronico.repositories.RegistroPontoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class RegistroPontoService {

    @Autowired
    private RegistroPontoRepository registroPontoRepository;

    public RegistroPonto registrarPonto(RegistroPonto registroPonto) {
        if (registroPonto.getHoraEntrada() == null || registroPonto.getHoraSaida() == null) {
            throw new IllegalArgumentException("Todos os campos devem ser preenchidos!");
        }

        // Verifica se já existe um registro de ponto para o funcionário na mesma data
        boolean existeRegistro = registroPontoRepository.existsByCodFuncionarioAndDataRegistro(
                registroPonto.getCodFuncionario(),
                registroPonto.getDataRegistro()
        );

        if (existeRegistro) {
            throw new IllegalArgumentException("Já existe um registro de ponto para este funcionário nesta data!");
        }

        // Calcula as horas trabalhadas e extras
        double[] horas = calcularHorasTrabalhadasEExtras(
                registroPonto.getDataRegistro().toString(),
                registroPonto.getHoraEntrada().toString(),
                registroPonto.getHoraSaida().toString(),
                obterCodTurno(registroPonto.getCodFuncionario())
        );

        registroPonto.setTotalHorasTrabalhadas(horas[0]);
        registroPonto.setTotalHorasExtras(horas[1]);

        return registroPontoRepository.save(registroPonto);
    }
    


    public double[] calcularHorasTrabalhadasEExtras(String dataPonto, String horaEntrada, String horaSaida, int codTurno) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        try {
            // Parse os horários de entrada e saída
            Date entrada = timeFormat.parse(horaEntrada);
            Date saida = timeFormat.parse(horaSaida);

            // Calcular diferença em milissegundos
            long diferenca = saida.getTime() - entrada.getTime();

            // Converter diferença para minutos trabalhados
            double minutosTrabalhados = diferenca / (1000.0 * 60);

            // Identificar o dia da semana
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date data = dateFormat.parse(dataPonto);
            Calendar cal = Calendar.getInstance();
            cal.setTime(data);
            int diaSemana = cal.get(Calendar.DAY_OF_WEEK);

            // Subtrair 1 hora (60 minutos) do horário de almoço, somente em dias úteis
            if (diaSemana >= Calendar.MONDAY && diaSemana <= Calendar.FRIDAY) {
                minutosTrabalhados -= 60; // Subtrai 1 hora do almoço para dias úteis
            }

            // Garantir que minutos trabalhados não sejam negativos
            minutosTrabalhados = Math.max(minutosTrabalhados, 0);

            // Calcular horas trabalhadas
            double horasTrabalhadas = Math.floor(minutosTrabalhados / 60);
            double minutosRestantes = minutosTrabalhados % 60;

            // Ajustar horas trabalhadas com base em minutos restantes
            if (minutosRestantes >= 20) {
                horasTrabalhadas++;
            }

            double horasExtras = 0;

            // Regras de cálculo para horas extras e trabalhadas
            if (diaSemana == Calendar.SATURDAY || diaSemana == Calendar.SUNDAY) {
                // Para fins de semana, todas as horas são contabilizadas como extras
                horasExtras = horasTrabalhadas;
                horasTrabalhadas = 0;
            } else if (codTurno == 1) { // Turno 1
                double horasNormais = (diaSemana == Calendar.FRIDAY) ? 8 : 9;
                if (horasTrabalhadas > horasNormais) {
                    horasExtras = horasTrabalhadas - horasNormais;
                    horasTrabalhadas = horasNormais;
                }
            } else if (codTurno == 2) { // Turno 2
                double horasNormais = 8;
                if (horasTrabalhadas > horasNormais) {
                    horasExtras = horasTrabalhadas - horasNormais;
                    horasTrabalhadas = horasNormais;
                }
            }

            return new double[]{horasTrabalhadas, horasExtras};
        } catch (ParseException e) {
            e.printStackTrace();
            return new double[]{0.0, 0.0};
        }
    }

    private int obterCodTurno(Integer codFuncionario) {
        // Implementar lógica para buscar o turno do funcionário baseado no CodFuncionario.
        // Exemplo simplificado (substitua pela consulta real):
        return 1; // Retorno fixo para turno 1; substituir por lógica real.
    }
}
