document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('btn-lista').addEventListener('click', mostrarFuncionariosComJustificativa);
    document.getElementById('btn-buscar-data').addEventListener('click', buscarJustificativaPorData);
});

async function mostrarFuncionarios() {
    const dropdownContainer = document.getElementById('dropdown-container');
    const listaFuncionarios = document.getElementById('lista-funcionarios');
    const filtroFuncionarios = document.getElementById('filtro-funcionarios');

    if (dropdownContainer.style.display === 'block') {
        dropdownContainer.style.display = 'none';
        return;
    }

    try {
        const response = await fetch('/api/justificativa/funcionarios-com-justificativa');
        if (response.ok) {
            const funcionarios = await response.json();
            listaFuncionarios.innerHTML = '';

            funcionarios.forEach(func => {
                const li = document.createElement('li');
                li.textContent = func.nome;
                li.dataset.nome = func.nome.toLowerCase();
                li.dataset.id = func.id;

                li.addEventListener('click', function () {
                    document.getElementById('nome-funcionario').value = this.textContent;
                    document.getElementById('id-funcionario').value = this.dataset.id;
                    dropdownContainer.style.display = 'none';
                });

                listaFuncionarios.appendChild(li);
            });

            dropdownContainer.style.display = 'block';
            filtroFuncionarios.addEventListener('input', function () {
                const termo = filtroFuncionarios.value.toLowerCase();
                const itens = listaFuncionarios.querySelectorAll('li');
                itens.forEach(item => {
                    item.style.display = item.dataset.nome.includes(termo) ? 'block' : 'none';
                });
            });
        } else {
            alert('Erro ao buscar funcionários com justificativas.');
        }
    } catch (error) {
        console.error('Erro ao buscar funcionários:', error);
        alert('Erro ao carregar funcionários.');
    }
}

// Busca a justificativa com base na data e funcionário
document.getElementById('btn-buscar-data').addEventListener('click', async function () {
    const codFuncionario = document.getElementById('id-funcionario').value.trim();
    const data = document.getElementById('data').value.trim();

    if (!codFuncionario || !data) {
        alert('Por favor, selecione um funcionário e insira uma data.');
        return;
    }

    try {
        const response = await fetch(`/api/justificativa/${codFuncionario}/data/${data}`);
        if (response.ok) {
            const justificativa = await response.json();
            if (justificativa) {
                document.getElementById('motivo-justificativa').value = justificativa.motivo;
                document.getElementById('tipo-justificativa').value = justificativa.tipoJustificativa;
                document.getElementById('cod-justificativa').value = justificativa.codJustificativa;
            }
        } else if (response.status === 404) {
            alert('Nenhuma justificativa encontrada para a data especificada.');
            document.getElementById('motivo-justificativa').value = '';
            document.getElementById('tipo-justificativa').value = '';
            document.getElementById('data').value = '';
        } else {
            const errorMessage = await response.text();
            alert(`Erro ao buscar justificativa: ${errorMessage}`);
        }
    } catch (error) {
        console.error('Erro ao buscar justificativa:', error);
        alert('Erro ao buscar justificativa: Problema inesperado.');
    }
});

// Evento para o botão "Lista" – abre o container independente
document.getElementById('btn-lista').addEventListener('click', mostrarFuncionariosComJustificativa);

// Função para buscar e exibir a lista de funcionários no container do botão "Lista"
async function mostrarFuncionariosComJustificativa() {
    try {
        const response = await fetch('/api/justificativa/funcionarios-com-justificativa');
        if (!response.ok) throw new Error('Erro ao buscar funcionários.');

        const funcionarios = await response.json();
        const listaContainer = document.getElementById('lista-funcionarios-btn-container');
        const listaFuncionariosBtn = document.getElementById('lista-funcionarios-btn');
        listaFuncionariosBtn.innerHTML = '';

        funcionarios.forEach(funcionario => {
            const li = document.createElement('li');
            li.textContent = `${funcionario.nome} (ID: ${funcionario.id})`;
            li.dataset.idFuncionario = funcionario.id;
            li.dataset.nomeFuncionario = funcionario.nome;

            li.addEventListener('click', async function () {
                await mostrarJustificativasFuncionario(this.dataset.idFuncionario, this.dataset.nomeFuncionario);
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
        console.error('Erro ao buscar funcionários:', error);
    }
}

// Função para exibir as justificativas de um funcionário no modal
async function mostrarJustificativasFuncionario(codFuncionario, nomeFuncionario) {
    try {
        if (!nomeFuncionario) {
            throw new Error("Nome do funcionário não foi recebido corretamente.");
        }

        const response = await fetch(`/api/justificativa/listar/${codFuncionario}`);
        if (!response.ok) {
            throw new Error('Erro ao buscar justificativas. Verifique se há registros.');
        }

        const justificativas = await response.json();
        if (justificativas.length === 0) {
            alert(`Nenhuma justificativa encontrada para ${nomeFuncionario}.`);
            return;
        }

        const tabelaBody = document.querySelector('#tabela-justificativas tbody');
        tabelaBody.innerHTML = '';

        document.getElementById('titulo-justificativas').textContent = `${nomeFuncionario}`;

        justificativas.forEach(justificativa => {
            const row = document.createElement('tr');
            const dataFormatada = formatarData(justificativa.dataJustificativa);

            row.innerHTML = `
                <td>${dataFormatada}</td>
                <td>${justificativa.motivo}</td>
                <td>${justificativa.tipoJustificativa}</td>
            `;

            row.addEventListener('click', function () {
                preencherCamposJustificativa(justificativa, codFuncionario, nomeFuncionario);
                fecharModal();
            });

            tabelaBody.appendChild(row);
        });

        abrirModal();
    } catch (error) {
        console.error("Erro ao buscar justificativas:", error);
        alert(`Erro ao buscar justificativas de ${nomeFuncionario}.`);
    }
}

// Preenche os campos do formulário ao selecionar uma justificativa
function preencherCamposJustificativa(justificativa, codFuncionario, nomeFuncionario) {
    document.getElementById('id-funcionario').value = codFuncionario;
    document.getElementById('nome-funcionario').value = nomeFuncionario;
    document.getElementById('data').value = justificativa.dataJustificativa;
    document.getElementById('motivo-justificativa').value = justificativa.motivo;
    document.getElementById('tipo-justificativa').value = justificativa.tipoJustificativa;
    document.getElementById('cod-justificativa').value = justificativa.codJustificativa;
}

// Formata a data de "AAAA-MM-DD" para "DD/MM/AAAA"
function formatarData(data) {
    if (!data) return 'Data inválida';
    const partes = data.split('-');
    return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

// Abre e fecha o modal de justificativas
function abrirModal() {
    document.getElementById('modal-justificativas').style.display = 'flex';
}

function fecharModal() {
    document.getElementById('modal-justificativas').style.display = 'none';
}

// Atualiza a justificativa no backend
document.getElementById('btn-atualizar').addEventListener('click', async function (event) {
    event.preventDefault();

    const codFuncionario = document.getElementById('id-funcionario').value.trim();
    const data = document.getElementById('data').value.trim();
    const motivo = document.getElementById('motivo-justificativa').value.trim();
    const tipo = document.getElementById('tipo-justificativa').value.trim();
    const codJustificativa = document.getElementById('cod-justificativa')?.value;

    if (!codJustificativa) {
        alert('Nenhuma justificativa foi selecionada para atualização.');
        return;
    }

    if (!codFuncionario || !data || !motivo || !tipo) {
        alert('Por favor, preencha todos os campos antes de atualizar.');
        return;
    }

    try {
        const dadosAtualizados = {
            codJustificativa: parseInt(codJustificativa),
            codFuncionario: parseInt(codFuncionario),
            dataJustificativa: data,
            motivo: motivo,
            tipoJustificativa: tipo
        };

        const response = await fetch('/api/justificativa/atualizar', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(dadosAtualizados),
        });

        if (response.ok) {
            alert('Justificativa atualizada com sucesso!');
            limparCamposJustificativa();
        } else {
            const mensagemErro = await response.text();
            alert(`Erro ao atualizar justificativa: ${mensagemErro}`);
        }
    } catch (error) {
        console.error('Erro ao atualizar justificativa:', error);
        alert('Erro ao atualizar: Ocorreu um problema inesperado.');
    }
});

// Exclui a justificativa selecionada
document.getElementById('btn-excluir').addEventListener('click', async function () {
    const codJustificativa = document.getElementById('cod-justificativa').value.trim();

    if (!codJustificativa) {
        alert('Nenhuma justificativa foi selecionada para exclusão.');
        return;
    }

    const confirmacao = confirm('Tem certeza de que deseja excluir esta justificativa?');
    if (!confirmacao) return;

    try {
        const response = await fetch(`/api/justificativa/excluir/${codJustificativa}`, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' },
        });

        const respostaJson = await response.json();

        if (response.ok) {
            alert('Justificativa excluida com sucesso!');
            limparCamposJustificativa();
        } else {
            console.error('Erro ao excluir justificativa:', respostaJson.mensagem);
            alert(`Erro ao excluir justificativa: ${respostaJson.mensagem}`);
        }
    } catch (error) {
        console.error(error);
        alert('Justificativa excluida com sucesso!');
    }
});

function limparCamposJustificativa() {
    document.getElementById('nome-funcionario').value = '';
    document.getElementById('id-funcionario').value = '';
    document.getElementById('data').value = '';
    document.getElementById('motivo-justificativa').value = '';
    document.getElementById('tipo-justificativa').value = '';
    document.getElementById('cod-justificativa').value = '';
}

// Validação para permitir somente 4 dígitos no ano do campo "Data"
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

// Captura a tecla "Esc" para fechar telas na sequência correta
document.addEventListener("keyup", function(event) {
    if (event.key === "Escape") {
        const modalJustificativas = document.getElementById('modal-justificativas');
        const listaFuncionariosContainer = document.getElementById('lista-funcionarios-btn-container');
        const dropdownContainer = document.getElementById('dropdown-container');

        if (modalJustificativas && modalJustificativas.style.display === 'flex') {
            // Se o modal de justificativas estiver aberto, fecha-o
            modalJustificativas.style.display = 'none';
            console.log("Modal de justificativas fechado.");
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
            window.location.href = "/justificativa/justificativa.html";
        }
    }
});



