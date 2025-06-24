document.addEventListener('DOMContentLoaded', () => {
    async function mostrarFuncionarios() {
        const dropdownContainer = document.getElementById('dropdown-container');
        const listaFuncionarios = document.getElementById('lista-funcionarios');
        const filtroFuncionarios = document.getElementById('filtro-funcionarios');

        if (dropdownContainer.style.display === 'block') {
            dropdownContainer.style.display = 'none';
            return;
        }

        try {
            const response = await fetch('/api/funcionarios/nomes-codigos');
            if (response.ok) {
                const funcionarios = await response.json();
                listaFuncionarios.innerHTML = '';

                funcionarios.forEach(func => {
                    if (!func.temLogin) {
                        const li = document.createElement('li');
                        li.textContent = func.nome;
                        // Corrigido: Usar func.codFuncionario em vez de func.id
                        li.dataset.idFuncionario = func.codFuncionario; // Já está formatado como "001", "002", etc.
                        li.dataset.nome = func.nome.toLowerCase();

                        li.addEventListener('click', function () {
                            document.getElementById('nome-funcionario').value = this.textContent;
                            document.getElementById('id-funcionario').value = this.dataset.idFuncionario;
                            dropdownContainer.style.display = 'none';
                        });

                        listaFuncionarios.appendChild(li);
                    }
                });

                if (listaFuncionarios.innerHTML === '') {
                    alert('Todos os funcionários já possuem login cadastrado.');
                } else {
                    dropdownContainer.style.display = 'block';
                }

                filtroFuncionarios.addEventListener('input', function () {
                    const termo = filtroFuncionarios.value.toLowerCase();
                    const itens = listaFuncionarios.querySelectorAll('li');
                    itens.forEach(item => {
                        item.style.display = item.dataset.nome.includes(termo) ? 'block' : 'none';
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

    window.mostrarFuncionarios = mostrarFuncionarios;

    document.getElementById('form-cadastro-login').addEventListener('submit', async function (event) {
        event.preventDefault();

        const login = document.getElementById('username').value.trim();
        const senha = document.getElementById('senha').value.trim();
        const confirmarSenha = document.getElementById('confirmar-senha').value.trim();
        const codFuncionario = document.getElementById('id-funcionario').value;

        if (!codFuncionario) {
            alert('Por favor, selecione um funcionário.');
            return;
        }

        if (senha !== confirmarSenha) {
            alert('As senhas não coincidem.');
            return;
        }

        const loginData = {
            login: login,
            senha: senha,
            codFuncionario: codFuncionario
        };

        try {
            const response = await fetch('/api/login/cadastrar', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(loginData)
            });

            if (response.ok) {
                alert('Login cadastrado com sucesso!');
                document.getElementById('form-cadastro-login').reset();
            } else {
                const errorText = await response.text();
                alert(`Erro ao cadastrar: ${errorText}`);
            }
        } catch (error) {
            console.error('Erro na requisição:', error);
            alert('Erro ao tentar cadastrar. Tente novamente mais tarde.');
        }
    });
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
            window.location.href = "/cadastro/bt-cadastro.html";
        }
    }
});