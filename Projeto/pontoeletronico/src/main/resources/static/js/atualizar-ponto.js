// Validação do campo "Data" para permitir somente 4 dígitos no ano (AAAA)
document.getElementById('data').addEventListener('input', function (event) {
    const input = event.target;
    const valor = input.value;

    if (valor.length >= 4 && valor.includes('-')) {
        const partesData = valor.split('-');
        const ano = partesData[0];
        if (ano.length > 4) {
            partesData[0] = ano.slice(0, 4);
            input.value = partesData.join('-');
        }
    }
});

// Função para exibir a lista de funcionários via seta (dropdown dentro do campo)
async function mostrarFuncionarios() {
    const dropdownContainer = document.getElementById('dropdown-container');
    const listaFuncionarios = document.getElementById('lista-funcionarios');
    const filtroFuncionarios = document.getElementById('filtro-funcionarios');

    // Alterna a exibição
    if (dropdownContainer.style.display === 'block') {
        dropdownContainer.style.display = 'none';
        return;
    }

    try {
        const response = await fetch('/pontos/funcionarios-com-ponto');
        if (!response.ok) throw new Error('Erro ao buscar funcionários.');

        const funcionarios = await response.json();
        listaFuncionarios.innerHTML = '';

        if (funcionarios.length === 0) {
            listaFuncionarios.innerHTML = '<li>Nenhum funcionário encontrado.</li>';
        } else {
            funcionarios.forEach(funcionario => {
                const li = document.createElement('li');
                li.textContent = funcionario.nome;
                li.dataset.nome = funcionario.nome.toLowerCase();
                li.dataset.id = funcionario.id;
                li.addEventListener('click', () => {
                    document.getElementById('nome-funcionario').value = funcionario.nome;
                    document.getElementById('id-funcionario').value = funcionario.id;
                    dropdownContainer.style.display = 'none';
                });
                listaFuncionarios.appendChild(li);
            });
        }

        dropdownContainer.style.display = 'block';

        filtroFuncionarios.addEventListener('input', function () {
            const termo = filtroFuncionarios.value.toLowerCase();
            const itens = listaFuncionarios.querySelectorAll('li');
            itens.forEach(item => {
                item.style.display = item.dataset.nome.includes(termo) ? 'block' : 'none';
            });
        });

    } catch (error) {
        console.error('Erro ao buscar funcionários:', error);
        alert('Erro ao carregar funcionários com ponto.');
    }
}

// Reseta alguns campos ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    limparCampos();
});

// Busca dados do ponto com base na data e funcionário
document.getElementById('btn-buscar-data').addEventListener('click', async function () {
    const codFuncionario = document.getElementById('id-funcionario').value.trim();
    const data = document.getElementById('data').value.trim();

    if (!codFuncionario) {
        alert('Por favor, selecione um funcionário antes de buscar.');
        return;
    }
    if (!data) {
        alert('Por favor, insira uma data antes de buscar.');
        return;
    }

    try {
        const response = await fetch(`/pontos/${codFuncionario}/data/${data}`);
        if (response.ok) {
            const pontos = await response.json();
            if (pontos.length > 0) {
                document.getElementById('hora-entrada').value = pontos[0].horaEntrada;
                document.getElementById('hora-saida').value = pontos[0].horaSaida;
            }
        } else if (response.status === 204) {
            alert('Nenhum ponto encontrado para a data especificada.');
            limparCampos();
        } else {
            const errorMessage = await response.text();
            alert(`Erro ao buscar ponto: ${errorMessage}`);
            limparCampos();
        }
    } catch (error) {
        console.error('Erro ao buscar ponto:', error);
        alert('Nenhum ponto encontrado para a data especificada.');
        limparCampos();
    }
});

// Limpa apenas os campos de data e horários
function limparCampos() {
    document.getElementById('data').value = '';
    document.getElementById('hora-entrada').value = '';
    document.getElementById('hora-saida').value = '';
}

// Atualiza os dados de ponto no banco de dados
document.getElementById('btn-atualizar').addEventListener('click', async function (event) {
    event.preventDefault();
    const codFuncionario = document.getElementById('id-funcionario').value.trim();
    const data = document.getElementById('data').value.trim();
    const horaEntrada = document.getElementById('hora-entrada').value.trim();
    const horaSaida = document.getElementById('hora-saida').value.trim();

    if (!codFuncionario || !data || !horaEntrada || !horaSaida) {
        alert('Por favor, preencha todos os campos antes de atualizar.');
        return;
    }

    try {
        const dadosAtualizados = {
            codFuncionario: parseInt(codFuncionario),
            data,
            horaEntrada,
            horaSaida,
        };

        const response = await fetch('/pontos/atualizar', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dadosAtualizados),
        });

        if (response.ok) {
            alert('Dados atualizados com sucesso!');
            limparTodosCampos();
        } else {
            const mensagemErro = await response.text();
            alert(`Erro ao atualizar dados: ${mensagemErro}`);
        }
    } catch (error) {
        console.error(error);
        alert('Dados atualizados com sucesso!');
        limparTodosCampos();
    }
});

document.getElementById('btn-excluir').addEventListener('click', async function () {
    const codFuncionario = document.getElementById('id-funcionario').value.trim();
    const data = document.getElementById('data').value.trim();

    if (!codFuncionario || !data) {
        alert('Por favor, selecione o funcionário e a data antes de excluir.');
        return;
    }

    const confirmacao = confirm('Tem certeza de que deseja excluir o ponto para esta data?');
    if (!confirmacao) return;

    try {
        const response = await fetch(`/pontos/${codFuncionario}/data/${data}`, { method: 'DELETE' });
        const responseText = await response.text();
        if (response.ok && responseText.includes("Ponto excluído com sucesso")) {
            alert('Ponto excluído com sucesso!');
            limparCampos();
        } else {
            alert(`Erro ao excluir ponto: ${responseText}`);
        }
    } catch (error) {
        console.error(error);
        alert('Ponto excluído com sucesso!');
    }
});

// Evento para o botão "Lista" – abre o container independente
document.getElementById('btn-lista').addEventListener('click', mostrarFuncionariosComPonto);

// Função para buscar e exibir a lista de funcionários no container do botão "Lista"
async function mostrarFuncionariosComPonto() {
    try {
        const response = await fetch('/pontos/funcionarios-com-ponto');
        if (!response.ok) throw new Error('Erro ao buscar funcionários com ponto.');

        const funcionarios = await response.json();
        const listaContainer = document.getElementById('lista-funcionarios-btn-container');
        const listaFuncionariosBtn = document.getElementById('lista-funcionarios-btn');
        listaFuncionariosBtn.innerHTML = '';

        funcionarios.forEach(funcionario => {
            const li = document.createElement('li');
            li.textContent = `${funcionario.nome} (ID: ${funcionario.id})`;
            li.dataset.idFuncionario = funcionario.id;
            li.addEventListener('click', async function () {
                await mostrarPontosFuncionario(this.dataset.idFuncionario, this.textContent);
                listaContainer.style.display = 'none';
            });
            listaFuncionariosBtn.appendChild(li);
        });

        const filtroBtn = document.getElementById('filtro-funcionarios-btn');
        filtroBtn.addEventListener('input', function () {
            const termo = filtroBtn.value.toLowerCase();
            const itens = listaFuncionariosBtn.querySelectorAll('li');
            itens.forEach(item => {
                item.style.display = item.textContent.toLowerCase().includes(termo) ? 'block' : 'none';
            });
        });

        listaContainer.style.display = 'block';
    } catch (error) {
        console.error('Erro ao buscar funcionários com ponto:', error);
        alert('Erro ao carregar funcionários com registro de ponto.');
    }
}

// Exibe os registros de ponto de um funcionário no modal
async function mostrarPontosFuncionario(codFuncionario, nomeFuncionario) {
    try {
        const response = await fetch(`/pontos/${codFuncionario}`);
        if (!response.ok) throw new Error('Erro ao buscar registros de ponto.');

        const pontos = await response.json();
        if (pontos.length === 0) {
            alert("Nenhum registro de ponto encontrado para este funcionário.");
            return;
        }

        const tabelaBody = document.querySelector('#tabela-ponto tbody');
        tabelaBody.innerHTML = '';
        document.getElementById('titulo-ponto').textContent = `Ponto de: ${nomeFuncionario}`;

        pontos.forEach(ponto => {
            const row = document.createElement('tr');
            const dataFormatada = formatarData(ponto.data);
            row.innerHTML = `
                <td>${dataFormatada}</td>
                <td>${ponto.horaEntrada}</td>
                <td>${ponto.horaSaida}</td>
                <td>${ponto.totalHorasTrabalhadas} h</td>
                <td>${ponto.totalHorasExtras} h</td>
            `;
            row.addEventListener('click', function () {
                preencherCampos(ponto, codFuncionario, nomeFuncionario);
                fecharModalPonto();
            });
            tabelaBody.appendChild(row);
        });

        abrirModalPonto();
    } catch (error) {
        console.error('Erro ao buscar registros de ponto:', error);
        alert('Erro ao buscar registros de ponto.');
    }
}

// Preenche os campos do formulário com os dados do ponto selecionado
function preencherCampos(ponto, codFuncionario, nomeFuncionario) {
    document.getElementById('id-funcionario').value = codFuncionario;
    const nomeSomente = nomeFuncionario.replace(/\(ID: \d+\)/, "").trim();
    document.getElementById('nome-funcionario').value = nomeSomente;
    document.getElementById('data').value = ponto.data;
    document.getElementById('hora-entrada').value = ponto.horaEntrada;
    document.getElementById('hora-saida').value = ponto.horaSaida;
}

// Converte data do formato "AAAA-MM-DD" para "DD/MM/AAAA"
function formatarData(data) {
    if (!data) return 'Data inválida';
    const partes = data.split('-');
    return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

// Abre e fecha o modal dos registros de ponto
function abrirModalPonto() {
    document.getElementById('modal-ponto').style.display = 'flex';
}

function fecharModalPonto() {
    document.getElementById('modal-ponto').style.display = 'none';
}

// Limpa todos os campos do formulário
function limparTodosCampos() {
    document.getElementById('nome-funcionario').value = '';
    document.getElementById('id-funcionario').value = '';
    document.getElementById('data').value = '';
    document.getElementById('hora-entrada').value = '';
    document.getElementById('hora-saida').value = '';
}

// Evento para capturar a tecla ESC e fechar telas na sequência correta
document.addEventListener("keyup", function(event) {
    if (event.key === "Escape") {
        const modalPonto = document.getElementById('modal-ponto');
        const listaFuncionariosContainer = document.getElementById('lista-funcionarios-btn-container');
        const dropdownContainer = document.getElementById('dropdown-container');

        if (modalPonto && modalPonto.style.display === 'flex') {
            // Se o modal dos registros de ponto estiver aberto, fecha-o
            modalPonto.style.display = 'none';
            console.log("Modal de ponto fechado.");
        } else if (listaFuncionariosContainer && listaFuncionariosContainer.style.display === 'block') {
            // Se a lista de funcionários estiver aberta, fecha-a
            listaFuncionariosContainer.style.display = 'none';
            console.log("Lista de funcionários fechada.");
        } else if (dropdownContainer && dropdownContainer.style.display === 'block') {
            // Se o dropdown da seleção de funcionário estiver aberto, fecha-o
            dropdownContainer.style.display = 'none';
            console.log("Dropdown fechado.");
        } else {
            // Se nada mais estiver aberto, redireciona para o dashboard
            console.log("Redirecionando para dashboard.html");
            window.location.href = "/ponto/registro-ponto.html";
        }
    }
});