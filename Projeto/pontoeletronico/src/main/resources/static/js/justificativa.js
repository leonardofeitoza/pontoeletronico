document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('form-justificativas').addEventListener('submit', async function (event) {
        event.preventDefault();

        const codFuncionario = document.getElementById('id-funcionario').value.trim();
        const dataJustificativa = document.getElementById('data-justificativa').value.trim();
        const motivo = document.getElementById('motivo-justificativa').value.trim();
        const tipoJustificativa = document.getElementById('tipo-justificativa').value.trim();

        if (!codFuncionario || !dataJustificativa || !motivo || !tipoJustificativa) {
            alert("Todos os campos são obrigatórios!");
            return;
        }

        try {
            const justificativa = {
                codFuncionario: parseInt(codFuncionario),
                dataJustificativa,
                motivo,
                tipoJustificativa,
            };

            const response = await fetch('/api/justificativa/registrar', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(justificativa),
            });

            if (response.ok) {
                alert('Justificativa registrada com sucesso!');
                limparCamposJustificativa();
            } else if (response.status === 409) {
                // Erro 409 -> Justificativa já cadastrada
                alert('Já existe uma justificativa cadastrada para este funcionário nesta data.');
                setTimeout(() => limparCamposJustificativa(), 100); // Aguarda antes de limpar os campos
            } else {
                const error = await response.text();
                alert(`Erro ao registrar justificativa: ${error}`);
            }
        } catch (error) {
            console.error('Erro ao registrar justificativa:', error);
            alert('Erro ao tentar registrar a justificativa. Tente novamente mais tarde.');
        }
    });
});

// Função para limpar todos os campos corretamente
function limparCamposJustificativa() {
    document.getElementById('nome-funcionario').value = '';
    document.getElementById('id-funcionario').value = '';
    document.getElementById('data-justificativa').value = '';
    document.getElementById('motivo-justificativa').value = '';
    document.getElementById('tipo-justificativa').value = '';

    // Reseta o formulário para garantir que os campos sejam limpos corretamente
    document.getElementById('form-justificativas').reset();
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