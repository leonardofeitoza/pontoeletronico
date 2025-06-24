document.addEventListener('DOMContentLoaded', () => {
    // Preenche a data com o valor atual do computador
    const dataAtual = new Date().toLocaleDateString('en-CA'); // Formato padrão ISO 'YYYY-MM-DD'
    document.getElementById('data').value = dataAtual;

    // Listener para o formulário de registro de ponto
    document.getElementById('form-registro-ponto').addEventListener('submit', async function (event) {
        event.preventDefault(); // Previne o comportamento padrão do formulário

        const idFuncionario = document.getElementById('id-funcionario').value.trim();
        const data = document.getElementById('data').value.trim();
        const horaEntrada = document.getElementById('hora-entrada').value.trim();
        const horaSaida = document.getElementById('hora-saida').value.trim();

        // Validação: Todos os campos devem ser preenchidos
        if (!idFuncionario || !data || !horaEntrada || !horaSaida) {
            alert('Todos os campos devem ser preenchidos!');
            return;
        }

        // Monta o objeto para o registro de ponto
        const registro = {
            codFuncionario: idFuncionario,
            data,
            horaEntrada,
            horaSaida,
        };

        try {
            // Envia o registro para a API
            const response = await fetch('/api/ponto/registrar', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams(registro),
            });

            if (response.ok) {
                // Exibe mensagem de sucesso e reseta o formulário
                alert('Ponto registrado com sucesso!');
                limparCampos();
            } else {
                const error = await response.text();
                alert(error);
                limparCampos(); // Chama a função para limpar os campos após o alerta de erro
            }
        } catch (error) {
            console.error('Erro ao registrar ponto:', error);
            alert('Erro ao tentar registrar o ponto. Tente novamente mais tarde.');
            limparCampos();
        }
    });
});

// Função para limpar todos os campos após a mensagem de erro ou sucesso
function limparCampos() {
    document.getElementById('id-funcionario').value = '';
    document.getElementById('nome-funcionario').value = '';
    document.getElementById('hora-entrada').value = '';
    document.getElementById('hora-saida').value = '';

    // Mantém a data preenchida com a data atual
    const dataAtual = new Date().toLocaleDateString('en-CA');
    document.getElementById('data').value = dataAtual;
}

// Função para exibir a lista de funcionários com barra de busca
async function mostrarFuncionarios() {
    const dropdownContainer = document.getElementById('dropdown-container');
    const listaFuncionarios = document.getElementById('lista-funcionarios');
    const filtroFuncionarios = document.getElementById('filtro-funcionarios');

    // Alterna a exibição da lista
    if (dropdownContainer.style.display === 'block') {
        dropdownContainer.style.display = 'none';
        return;
    }

    try {
        const response = await fetch('/api/funcionarios/nomes-codigos');
        if (response.ok) {
            const funcionarios = await response.json();
            listaFuncionarios.innerHTML = ''; // Limpa a lista anterior

            // Preenche a lista com os nomes e IDs dos funcionários
            funcionarios.forEach(func => {
                const li = document.createElement('li');
                li.textContent = func.nome; // Apenas o nome será exibido
                li.dataset.nome = func.nome.toLowerCase(); // Para a busca
                li.dataset.idFuncionario = func.codFuncionario; // Corrigido: Usar codFuncionario

                // Evento de clique para preencher os campos
                li.addEventListener('click', function () {
                    document.getElementById('nome-funcionario').value = this.textContent;
                    document.getElementById('id-funcionario').value = this.dataset.idFuncionario;
                    dropdownContainer.style.display = 'none';
                });

                listaFuncionarios.appendChild(li);
            });

            // Exibe a lista e ativa a filtragem
            dropdownContainer.style.display = 'block';

            filtroFuncionarios.addEventListener('input', function () {
                const termo = filtroFuncionarios.value.toLowerCase();
                const itens = listaFuncionarios.querySelectorAll('li');

                itens.forEach(item => {
                    if (item.dataset.nome.includes(termo)) {
                        item.style.display = 'block';
                    } else {
                        item.style.display = 'none';
                    }
                });
            });
        } else {
            alert('Erro ao buscar funcionários.');
        }
    } catch (error) {
        console.error('Erro ao buscar funcionários:', error);
        alert('Erro ao tentar carregar a lista de funcionários.');
    }
}

document.addEventListener("keyup", function(event) {
    if (event.key === "Escape") {
        const dropdownContainer = document.getElementById('dropdown-container');

        if (dropdownContainer && dropdownContainer.style.display === 'block') {
            // Se o dropdown estiver aberto, fecha apenas ele e permanece na tela
            dropdownContainer.style.display = 'none';
            console.log("Dropdown fechado.");
        } else {
            // Se o dropdown já estiver fechado, volta para a tela anterior
            console.log("Redirecionando para dashboard.html");
            window.location.href = "/dashboard/dashboard.html";
        }
    }
});