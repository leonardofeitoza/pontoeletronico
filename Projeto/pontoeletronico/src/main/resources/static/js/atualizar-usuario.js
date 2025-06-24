// Função para exibir os nomes dos funcionários com login e senha no dropdown
async function mostrarFuncionarios() {
    const dropdownContainer = document.getElementById('dropdown-container');
    const listaFuncionarios = document.getElementById('lista-funcionarios');
    const filtroFuncionarios = document.getElementById('filtro-funcionarios');

    if (dropdownContainer.style.display === 'block') {
        dropdownContainer.style.display = 'none';
        return;
    }

    try {
        const response = await fetch('/api/login/listar'); // Busca os funcionários com login cadastrado
        if (!response.ok) throw new Error('Erro ao carregar funcionários.');

        const logins = await response.json();
        listaFuncionarios.innerHTML = ''; // Limpa a lista antes de preenchê-la

        logins.forEach(login => {
            const li = document.createElement('li');
            li.textContent = login.nomeFuncionario;
            li.dataset.idFuncionario = String(login.idFuncionario).padStart(3, '0'); // Formata o ID com três dígitos
            li.dataset.nome = login.nomeFuncionario.toLowerCase();

            li.addEventListener('click', function () {
                preencherCampos(login);
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

    } catch (error) {
        console.error('Erro ao carregar funcionários:', error);
        alert('Erro ao carregar funcionários.');
    }
}

// Atualiza os campos com os dados do funcionário selecionado
function preencherCampos(dados) {
    document.getElementById('nome-funcionario').value = dados.nomeFuncionario || '';
    document.getElementById('id-funcionario').value = String(dados.idFuncionario).padStart(3, '0'); // Aplica formatação
    document.getElementById('username').value = dados.login || '';
    document.getElementById('senha').value = '*'.repeat(8);
    document.getElementById('confirmar-senha').value = '*'.repeat(8);
}


// Função para resetar todos os campos ao acessar a página
function resetarCampos() {
    document.getElementById('nome-funcionario').value = '';
    document.getElementById('id-funcionario').value = '';
    document.getElementById('username').value = '';
    document.getElementById('senha').value = '';
    document.getElementById('confirmar-senha').value = '';
}

// Evento para resetar os campos ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    resetarCampos(); // Reseta os campos ao entrar na página
});



// Função para exibir a lista de logins dos funcionários
document.getElementById('btn-lista').addEventListener('click', async function () {
    try {
        const response = await fetch('/api/login/listar');
        if (!response.ok) throw new Error('Erro ao carregar logins.');

        const logins = await response.json();
        const tabelaBody = document.querySelector('#tabela-logins tbody');
        tabelaBody.innerHTML = ''; // Limpa a tabela antes de preencher

        logins.forEach(login => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${login.idFuncionario}</td>
                <td>${login.nomeFuncionario}</td>
                <td>${login.login}</td>
            `;
            row.addEventListener('click', function () {
                preencherCampos(login);
                fecharModal();
            });
            tabelaBody.appendChild(row);
        });

        abrirModal();
    } catch (error) {
        console.error('Erro ao carregar logins:', error);
        alert('Erro ao carregar logins.');
    }
});

function abrirModal() {
    document.getElementById('modal-logins').style.display = 'flex';
}

function fecharModal() {
    document.getElementById('modal-logins').style.display = 'none';
}

// Evento para resetar os campos ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    resetarCampos(); // Reseta os campos ao entrar na página
});

// Atualizar login e senha
document.getElementById('btn-atualizar').addEventListener('click', async function (event) {
    event.preventDefault(); // Evita o comportamento padrão do formulário

    const idFuncionario = document.getElementById('id-funcionario').value.trim();
    const login = document.getElementById('username').value.trim();
    const senha = document.getElementById('senha').value.trim();
    const confirmarSenha = document.getElementById('confirmar-senha').value.trim();

    if (senha !== confirmarSenha) {
        alert('As senhas não coincidem.');
        return;
    }

    try {
        const response = await fetch('/api/login/atualizar', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ idFuncionario, login, senha }),
        });

        const mensagem = await response.text();
        if (response.ok) {
            alert(mensagem);
            resetarCampos(); // Reseta os campos após atualizar
            window.location.href = '/cadastro/atualizar-usuario.html'; // Redireciona para a mesma tela
        } else {
            alert(`Erro ao atualizar: ${mensagem}`);
        }
    } catch (error) {
        console.error('Erro ao atualizar login:', error);
        alert('Erro ao atualizar: Ocorreu um erro inesperado.');
    }
});

// Função para excluir o login
document.getElementById('btn-excluir').addEventListener('click', async function (event) {
    event.preventDefault(); // Previne comportamento padrão do botão

    const idFuncionario = document.getElementById('id-funcionario').value.trim();

    if (!idFuncionario) {
        alert('Selecione um funcionário antes de excluir.');
        return;
    }

    const confirmacao = confirm('Tem certeza que deseja excluir o login deste funcionário?');
    if (!confirmacao) {
        return;
    }

    try {
        // Envia uma requisição DELETE ao backend
        const response = await fetch(`/api/login/excluir/${idFuncionario}`, {
            method: 'DELETE',
        });

        if (response.ok) {
            alert('Login excluído com sucesso!');
            // Limpa os campos do formulário
            resetarCampos();
        } else {
            const error = await response.text();
            alert(`Erro ao excluir login: ${error}`);
        }
    } catch (error) {
        console.error('Erro ao excluir login:', error);
        alert('Erro ao excluir login: Ocorreu um erro inesperado.');
    }
});

// Captura a tecla "Esc" para fechar o dropdown ou voltar para a tela anterior
document.addEventListener("keyup", function(event) {
    if (event.key === "Escape") {
        const dropdownContainer = document.getElementById('dropdown-container');

        if (dropdownContainer && dropdownContainer.style.display === 'block') {
            // Se o dropdown estiver aberto, fecha apenas ele e permanece na tela
            dropdownContainer.style.display = 'none';
            console.log("Dropdown fechado.");
        } else {
            // Se o dropdown já estiver fechado, volta para a tela anterior
            console.log("Redirecionando para bt-cadastro.html");
            window.location.href = "/cadastro/cadastro-usuario.html";
        }
    }
});


