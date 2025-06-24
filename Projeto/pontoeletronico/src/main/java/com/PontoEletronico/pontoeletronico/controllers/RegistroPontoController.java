package com.PontoEletronico.pontoeletronico.controllers;

import com.PontoEletronico.pontoeletronico.models.RegistroPonto;
import com.PontoEletronico.pontoeletronico.services.RegistroPontoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/ponto")
public class RegistroPontoController {

    @Autowired
    private RegistroPontoService registroPontoService;

    @PostMapping("/registrar")
    public ResponseEntity<String> registrarPonto(
            @RequestParam("codFuncionario") Integer codFuncionario,
            @RequestParam("data") String data,
            @RequestParam("horaEntrada") String horaEntrada,
            @RequestParam("horaSaida") String horaSaida) {
        try {
            if (codFuncionario == null || data == null || horaEntrada == null || horaSaida == null ||
                    horaEntrada.isEmpty() || horaSaida.isEmpty()) {
                return ResponseEntity.badRequest().body("Todos os campos devem ser preenchidos!");
            }

            LocalDate dataRegistro = LocalDate.parse(data);
            LocalTime entrada = LocalTime.parse(horaEntrada);
            LocalTime saida = LocalTime.parse(horaSaida);

            RegistroPonto registroPonto = new RegistroPonto();
            registroPonto.setCodFuncionario(codFuncionario);
            registroPonto.setDataRegistro(dataRegistro);
            registroPonto.setHoraEntrada(entrada);
            registroPonto.setHoraSaida(saida);

            registroPontoService.registrarPonto(registroPonto);
            return ResponseEntity.ok("Ponto registrado com sucesso!");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao registrar ponto: " + e.getMessage());
        }
    }

}
