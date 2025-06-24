package com.PontoEletronico.pontoeletronico;

import com.PontoEletronico.pontoeletronico.repositories.*;
import com.PontoEletronico.pontoeletronico.models.*;
import com.PontoEletronico.pontoeletronico.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class SistemaControlePonto {

    @Autowired
    private FuncionarioRepository funcionarioRepository;
    @Autowired
    private TurnoRepository turnoRepository;
    @Autowired
    private RelatorioRepository relatorioRepository;
    @Autowired
    private JustificativaRepository justificativaRepository;
    @Autowired
    private PontoRepository pontoRepository;
    @Autowired
    private LoginRepository loginRepository;

    public static void main(String[] args) {
        var context = SpringApplication.run(SistemaControlePonto.class, args);
        SistemaControlePonto sistema = context.getBean(SistemaControlePonto.class);
        Scanner scanner = new Scanner(System.in);
        //sistema.executar(scanner);
        scanner.close();
    }

    /*public void executar(Scanner scanner) {
        System.out.println("=== Sistema de Controle de Ponto ===");
        System.out.println("====== =Login no Sistema =======");

        boolean loginRealizado = realizarLogin(scanner);
        if (loginRealizado) {
            while (true) {
                System.out.println("\n=== Sistema de Controle de Ponto ===");
                System.out.println("1. Cadastro");
                System.out.println("2. Registro de Ponto");
                System.out.println("3. Cadastro de Justificativa");
                System.out.println("4. Geração de Relatório");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");

                int opcao;
                try {
                    opcao = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Erro: Opção inválida. Tente novamente.");
                    continue;
                }

                if (opcao == 0) {
                    System.out.println("Saindo do sistema...");
                    break;
                }

                switch (opcao) {
                    case 1:
                        exibirMenuCadastro(scanner);
                        break;
                    case 2:
                        registrarPonto(scanner);
                        break;
                    case 3:
                        cadastrarJustificativa(scanner);
                        break;
                    case 4:
                        gerarRelatorio(scanner);
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                        break;
                }
            }
        }
    }

    private boolean realizarLogin(Scanner scanner) {
        while (true) {
            System.out.print("Digite seu login: ");
            String login = scanner.nextLine().toUpperCase();

            System.out.print("Digite sua senha: ");
            String senha = scanner.nextLine();

            if ("PASSAGGIO".equals(login) && "159753".equals(senha)) {
                System.out.println("Login realizado com sucesso! Bem-vindo, " + login + "!");
                return true;
            }

            Login usuario = loginRepository.findByLoginAndSenha(login, senha);
            if (usuario != null) {
                System.out.println("Login realizado com sucesso! Bem-vindo, " + usuario.getLogin() + "!");
                return true;
            }

            System.out.println("Erro: Login ou senha inválidos. Tente novamente.");
        }
    }

    private void exibirMenuCadastro(Scanner scanner) {
        System.out.println("\n=== Cadastro ===");
        System.out.println("1. Cadastro de Funcionário e Turno");
        System.out.println("2. Cadastro de Login e Senha");
        System.out.print("Escolha uma opção: ");

        int opcaoCadastro;
        try {
            opcaoCadastro = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Erro: Opção inválida. Tente novamente.");
            return;
        }

        if (opcaoCadastro == 1) {
            cadastrarFuncionarioETurno(scanner);
        } else if (opcaoCadastro == 2) {
            cadastrarLoginSenha(scanner);
        } else {
            System.out.println("Opção inválida.");
        }
    }

    @Autowired
    private LoginService loginService;

    private void cadastrarLoginSenha(Scanner scanner) {
        System.out.println("=== Cadastro de Login e Senha ===");

        System.out.print("Digite o código do funcionário: ");
        Integer codFuncionario = Integer.parseInt(scanner.nextLine());

        System.out.print("Digite o login: ");
        String login = scanner.nextLine();

        System.out.print("Digite a senha: ");
        String senha = scanner.nextLine();

        System.out.print("Confirme a senha: ");
        String confirmaSenha = scanner.nextLine();

        if (!senha.equals(confirmaSenha)) {
            System.out.println("Erro: As senhas não conferem.");
            return;
        }

        try {
            loginService.cadastrarLogin(codFuncionario, login, senha);
            System.out.println("Login cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar login: " + e.getMessage());
        }
    }



    private void cadastrarFuncionarioETurno(Scanner scanner) {
        System.out.println("=== Cadastro de Funcionário ===");

        String cpf;
        while (true) {
            System.out.print("CPF do funcionário (somente números): ");
            cpf = scanner.nextLine();
            if (funcionarioRepository.findByCpf(cpf) != null) {
                System.out.println("Erro: CPF já cadastrado. Tente novamente.");
            } else if (!cpf.matches("\\d{11}")) {
                System.out.println("Erro: CPF deve conter 11 dígitos. Tente novamente.");
            } else {
                break;
            }
        }

        System.out.print("Nome do funcionário: ");
        String nome = scanner.nextLine();

        System.out.print("Setor do funcionário: ");
        String setor = scanner.nextLine();

        System.out.print("Cargo do funcionário: ");
        String cargo = scanner.nextLine();

        LocalDate dataAdmissao;
        while (true) {
            System.out.print("Data de Admissão (DD-MM-AAAA): ");
            String dataAdmissaoStr = scanner.nextLine();
            dataAdmissao = convertDateFormat(dataAdmissaoStr);
            if (dataAdmissao == null) {
                System.out.println("Erro: Data inválida. Use o formato DD-MM-AAAA.");
            } else {
                break;
            }
        }

        System.out.print("Salário do funcionário: ");
        double salario = Double.parseDouble(scanner.nextLine());

        System.out.println("=== Cadastro de Turno ===");
        System.out.println("Escolha um turno:");
        System.out.println("1. Manhã/Tarde (08:00 - 17:45)");
        System.out.println("2. Noite (22:00 - 06:00)");
        int turnoOpcao = Integer.parseInt(scanner.nextLine());

        String descricaoTurno = turnoOpcao == 1 ? "Manhã/Tarde" : "Noite";
        String horaInicio = turnoOpcao == 1 ? "08:00:00" : "22:00:00";
        String horaTermino = turnoOpcao == 1 ? "17:45:00" : "06:00:00";

        Turno turno = new Turno(descricaoTurno, horaInicio, horaTermino);
        turnoRepository.save(turno);

        Funcionario funcionario = new Funcionario(nome, cpf, setor, cargo, dataAdmissao.toString(), salario, turno);
        funcionarioRepository.save(funcionario);

        System.out.println("Funcionário e turno cadastrados com sucesso!");
    }

    private void registrarPonto(Scanner scanner) {
        System.out.println("=== Registro de Ponto ===");
        System.out.print("CPF do funcionário (somente números): ");
        String cpf = scanner.nextLine();

        Funcionario funcionario = funcionarioRepository.findByCpf(cpf);
        if (funcionario != null) {
            LocalDate dataPonto = LocalDate.now();
            System.out.println("Data do ponto: " + dataPonto);

            System.out.print("Hora de entrada (HH:MM:SS): ");
            String horaEntrada = scanner.nextLine();

            System.out.print("Hora de saída (HH:MM:SS): ");
            String horaSaida = scanner.nextLine();

            double[] horas = calcularHorasTrabalhadasEExtras(dataPonto.toString(), horaEntrada, horaSaida, funcionario.getTurno().getCodTurno());
            double horasTrabalhadas = horas[0];
            double horasExtras = horas[1];

            Ponto ponto = new Ponto(dataPonto, LocalTime.parse(horaEntrada), LocalTime.parse(horaSaida), horasTrabalhadas, horasExtras, funcionario);
            pontoRepository.save(ponto);

            System.out.println("Ponto registrado com sucesso!");
            System.out.printf("Horas Trabalhadas: %.2f | Horas Extras: %.2f%n", horasTrabalhadas, horasExtras);
        } else {
            System.out.println("Erro: Funcionário não encontrado.");
        }
    }

    private double[] calcularHorasTrabalhadasEExtras(String dataPonto, String horaEntrada, String horaSaida, int codTurno) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        try {
            Date entrada = timeFormat.parse(horaEntrada);
            Date saida = timeFormat.parse(horaSaida);
            long diferenca = saida.getTime() - entrada.getTime();
            double minutosTrabalhados = diferenca / (1000 * 60) - 60;

            double horasTrabalhadas = Math.floor(minutosTrabalhados / 60);
            double minutosRestantes = minutosTrabalhados % 60;
            if (minutosRestantes >= 20) {
                horasTrabalhadas++;
            }

            double horasExtras = 0;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date data = dateFormat.parse(dataPonto);
            Calendar cal = Calendar.getInstance();
            cal.setTime(data);
            int diaSemana = cal.get(Calendar.DAY_OF_WEEK);

            if (diaSemana == Calendar.SATURDAY || diaSemana == Calendar.SUNDAY) {
                horasExtras = horasTrabalhadas;
                horasTrabalhadas = 0;
            } else if (codTurno == 1) {
                double horasNormais = (diaSemana == Calendar.FRIDAY) ? 8 : 9;
                if (horasTrabalhadas > horasNormais) {
                    horasExtras = horasTrabalhadas - horasNormais;
                    horasTrabalhadas = horasNormais;
                }
            } else if (codTurno == 2) {
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

    private void cadastrarJustificativa(Scanner scanner) {
        System.out.println("=== Cadastro de Justificativa ===");
        System.out.print("CPF do funcionário (somente números): ");
        String cpf = scanner.nextLine();

        Funcionario funcionario = funcionarioRepository.findByCpf(cpf);
        if (funcionario != null) {
            System.out.print("Data da justificativa (DD-MM-AAAA): ");
            LocalDate dataJustificativa = convertDateFormat(scanner.nextLine());

            System.out.print("Motivo da justificativa: ");
            String motivo = scanner.nextLine();

            System.out.print("Tipo de justificativa: ");
            String tipo = scanner.nextLine();

            /*Justificativa justificativa = new Justificativa(dataJustificativa, motivo, tipo,funcionario);
            justificativaRepository.save(justificativa);*/

            /*System.out.println("Justificativa cadastrada com sucesso!");
        } else {
            System.out.println("Erro: Funcionário não encontrado.");
        }
    }

    private void gerarRelatorio(Scanner scanner) {
        System.out.println("=== Geração de Relatório ===");
        System.out.println("1. Relatório de Funcionário");
        System.out.println("2. Relatório de Ponto");
        System.out.print("Escolha uma opção: ");
        int opcaoRelatorio;
        try {
            opcaoRelatorio = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Erro: Opção inválida. Tente novamente.");
            return;
        }

        if (opcaoRelatorio == 1) {
            // Relatório de Funcionário
            System.out.println("\n=== Relatório de Funcionário ===");
            System.out.println("1. Individual");
            System.out.println("2. Geral");
            System.out.print("Escolha uma opção: ");
            int opcaoFuncionario;
            try {
                opcaoFuncionario = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Erro: Opção inválida. Tente novamente.");
                return;
            }

            if (opcaoFuncionario == 1) {
                // Relatório de Funcionário Individual
                System.out.print("Informe o CPF do funcionário: ");
                String cpf = scanner.nextLine();
                Funcionario funcionario = funcionarioRepository.findByCpf(cpf);
                if (funcionario != null) {
                    System.out.println("\n=== Relatório Individual ===");
                    System.out.println("Nome: " + funcionario.getNome());
                    System.out.println("CPF: " + funcionario.getCpf());
                    System.out.println("Setor: " + funcionario.getSetor());
                    System.out.println("Cargo: " + funcionario.getCargo());
                    System.out.println("Data de Admissão: " + funcionario.getDataAdmissao());
                    System.out.println("Salário: " + funcionario.getSalario());
                    System.out.println("Turno: " + funcionario.getTurno().getDescricaoTurno());
                    System.out.println("---");
                } else {
                    System.out.println("Erro: Funcionário não encontrado.");
                }
            } else if (opcaoFuncionario == 2) {
                // Relatório de Funcionário Geral
                System.out.println("\n=== Relatório Geral ===");
                funcionarioRepository.findAllWithTurno().forEach(funcionario -> {
                    System.out.println("Nome: " + funcionario.getNome());
                    System.out.println("CPF: " + funcionario.getCpf());
                    System.out.println("Setor: " + funcionario.getSetor());
                    System.out.println("Cargo: " + funcionario.getCargo());
                    System.out.println("Data de Admissão: " + funcionario.getDataAdmissao());
                    System.out.println("Salário: " + funcionario.getSalario());
                    System.out.println("Turno: " + funcionario.getTurno().getDescricaoTurno());
                    System.out.println("---");
                });
            } else {
                System.out.println("Opção inválida.");
            }
        } else if (opcaoRelatorio == 2) {
            // Relatório de Ponto
            System.out.println("\n=== Relatório de Ponto ===");
            System.out.println("1. Individual");
            System.out.println("2. Geral");
            System.out.print("Escolha uma opção: ");
            int opcaoPonto;
            try {
                opcaoPonto = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Erro: Opção inválida. Tente novamente.");
                return;
            }

            if (opcaoPonto == 1) {
                // Relatório de Ponto Individual
                System.out.print("Informe o CPF do funcionário: ");
                String cpf = scanner.nextLine();
                Funcionario funcionario = funcionarioRepository.findByCpf(cpf);
                if (funcionario != null) {
                    List<Ponto> pontos = pontoRepository.findByFuncionario(funcionario);
                    if (pontos.isEmpty()) {
                        System.out.println("Não há pontos registrados para este funcionário.");
                    } else {
                        System.out.println("\n=== Relatório de Ponto Individual ===");
                        pontos.forEach(ponto -> {
                            System.out.println("Data: " + ponto.getData());
                            System.out.println("Hora de Entrada: " + ponto.getHoraEntrada());
                            System.out.println("Hora de Saída: " + ponto.getHoraSaida());
                            System.out.println("Horas Trabalhadas: " + ponto.getTotalHorasTrabalhadas());
                            System.out.println("Horas Extras: " + ponto.getTotalHorasExtras());
                            System.out.println("---");
                        });
                    }
                } else {
                    System.out.println("Erro: Funcionário não encontrado.");
                }
            } else if (opcaoPonto == 2) {
                // Relatório de Ponto Geral
                System.out.println("\n=== Relatório de Ponto Geral ===");
                pontoRepository.findAll().forEach(ponto -> {
                    System.out.println("Nome: " + ponto.getFuncionario().getNome());
                    System.out.println("CPF: " + ponto.getFuncionario().getCpf());
                    System.out.println("Data: " + ponto.getData());
                    System.out.println("Hora de Entrada: " + ponto.getHoraEntrada());
                    System.out.println("Hora de Saída: " + ponto.getHoraSaida());
                    System.out.println("Horas Trabalhadas: " + ponto.getTotalHorasTrabalhadas());
                    System.out.println("Horas Extras: " + ponto.getTotalHorasExtras());
                    System.out.println("---");
                });
            } else {
                System.out.println("Opção inválida.");
            }
        } else {
            System.out.println("Opção inválida.");
        }

        System.out.println("\nPressione Enter para voltar ao menu principal...");
        scanner.nextLine(); // Aguarda o usuário pressionar Enter
    }

    private LocalDate convertDateFormat(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Erro de formato de data. Use DD-MM-AAAA.");
            return null;
        }
    }*/
}
