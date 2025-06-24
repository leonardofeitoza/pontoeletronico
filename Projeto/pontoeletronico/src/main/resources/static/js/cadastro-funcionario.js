// Exemplo: Ao carregar a página, busca o próximo código
  document.addEventListener('DOMContentLoaded', async () => {
      try {
          const response = await fetch('/api/funcionarios/next-id');
          if (response.ok) {
              const nextId = await response.text();
              document.getElementById('id-funcionario').value = nextId;
          }
      } catch (error) {
          console.error('Erro ao buscar próximo ID:', error);
      }
  });

  // Listener para envio do formulário (já existente)
  document.getElementById('form-cadastro-funcionario').addEventListener('submit', async function (event) {
      event.preventDefault(); // Evita recarregar a página

      const nome = document.getElementById('nome').value.trim();
      const cpf = document.getElementById('cpf').value.trim();
      const cargo = document.getElementById('cargo').value.trim();
      const setor = document.getElementById('setor').value.trim();
      const dataAdmissao = document.getElementById('data-admissao').value.trim();
      const turnoDescricao = document.getElementById('turno').value;
      const salario = parseFloat(document.getElementById('salario').value.trim());

      // Validação do CPF
      if (!/^\d{11}$/.test(cpf)) {
          alert('CPF inválido. Deve conter exatamente 11 dígitos.');
          limparCamposExcetoID(); // Mantém o ID e limpa os demais campos
          return;
      }

      const funcionario = {
          nome,
          cpf,
          cargo,
          setor,
          dataAdmissao,
          salario,
          turno: { descricaoTurno: turnoDescricao }
      };

      try {
          const response = await fetch('/api/funcionarios', {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify(funcionario)
          });

          if (response.ok) {
              alert('Funcionário cadastrado com sucesso!');
              limparCamposExcetoID(); // Mantém o ID e limpa os demais campos
          } else {
              const error = await response.text();
              alert(`${error}`);
              limparCamposExcetoID(); // Mantém o ID e limpa os demais campos
          }
      } catch (error) {
          console.error('Erro na requisição:', error);
          alert('Erro ao tentar cadastrar. Tente novamente mais tarde.');
          limparCamposExcetoID(); // Mantém o ID e limpa os demais campos
      }
  });

  function limparCamposExcetoID() {
      const idAtual = document.getElementById('id-funcionario').value; // Salva o ID antes de limpar
      document.getElementById('form-cadastro-funcionario').reset(); // Limpa o formulário
      document.getElementById('id-funcionario').value = idAtual; // Restaura o ID original
  }