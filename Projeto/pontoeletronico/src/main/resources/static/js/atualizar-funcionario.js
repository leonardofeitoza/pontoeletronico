// Função para exibir uma lista de funcionários e permitir a busca
async function mostrarFuncionarios() {
    const dropdownContainer = document.getElementById('dropdown-container');
    const listaFuncionarios = document.getElementById('lista-funcionarios');
    const filtroFuncionarios = document.getElementById('filtro-funcionarios');

    // Alterna visibilidade da lista
    if (dropdownContainer.style.display === 'block') {
        dropdownContainer.style.display = 'none';
        return;
    }

    try {
        const response = await fetch('/api/funcionarios/nomes-codigos'); // API que retorna funcionários
        if (!response.ok) throw new Error('Erro ao buscar funcionários.');
        const funcionarios = await response.json();
        listaFuncionarios.innerHTML = ''; // Limpar a lista antes de preencher

        funcionarios.forEach(funcionario => {
            const li = document.createElement('li');
            li.textContent = funcionario.nome;
            li.dataset.nome = funcionario.nome.toLowerCase(); // Para filtro

            li.addEventListener('click', async function () {
                document.getElementById('nome-funcionario').value = funcionario.nome;
                document.getElementById('id-funcionario').value = funcionario.codFuncionario;
                dropdownContainer.style.display = 'none';

                // Buscar informações completas do funcionário, incluindo a foto
                const responseDados = await fetch(`/api/funcionarios/nome/${funcionario.nome}`);
                if (responseDados.ok) {
                    const dados = await responseDados.json();
                    preencherCampos(dados);
                } else {
                    alert('Erro ao buscar dados do funcionário.');
                }
            });

            listaFuncionarios.appendChild(li);
        });

        // Exibe menu suspenso e filtro ativo
        dropdownContainer.style.display = 'block';

        // Evento para filtrar funcionários na lista
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
    } catch (error) {
        console.error('Erro ao carregar funcionários:', error);
        alert('Erro ao carregar funcionários.');
    }
}

// Preenche os campos com os dados do funcionário, incluindo a foto
function preencherCampos(dados) {
    document.getElementById('id-funcionario').value = dados.codFuncionario || '';
    document.getElementById('nome-funcionario').value = dados.nome;
    document.getElementById('cpf').value = dados.cpf;
    document.getElementById('cpf-original').value = dados.cpf;
    document.getElementById('cargo').value = dados.cargo;
    document.getElementById('setor').value = dados.setor;
    document.getElementById('data-admissao').value = dados.dataAdmissao;
    document.getElementById('salario').value = dados.salario;
    document.getElementById('turno').value = dados.descricaoTurno || 'Manhã/Tarde';

    // Carregar a foto do funcionário, se existir
    const fotoPreview = document.getElementById('foto-preview');
    if (dados.foto) {
        fotoPreview.src = `data:image/jpeg;base64,${dados.foto}`;
        fotoPreview.style.display = 'block';
    } else {
        fotoPreview.src = '';
        fotoPreview.style.display = 'none';
    }

    tornarCamposEditaveis();
}

// Atualizar cadastro com suporte a foto
document.getElementById('form-alterar-funcionario').addEventListener('submit', async function (event) {
    event.preventDefault(); // Evita o comportamento padrão do formulário

    const formData = new FormData();
    formData.append('nome', document.getElementById('nome-funcionario').value.trim());
    formData.append('cpf', document.getElementById('cpf').value.trim());
    formData.append('cpfOriginal', document.getElementById('cpf-original').value.trim());
    formData.append('cargo', document.getElementById('cargo').value.trim());
    formData.append('setor', document.getElementById('setor').value.trim());
    formData.append('dataAdmissao', document.getElementById('data-admissao').value.trim());
    formData.append('salario', parseFloat(document.getElementById('salario').value.trim()));
    formData.append('turno', document.getElementById('turno').value.trim());

    const fotoInput = document.getElementById('foto');
    if (fotoInput.files[0]) {
        const compressedImage = await compressImage(fotoInput.files[0]);
        formData.append('foto', compressedImage);
    }

    try {
        const response = await fetch('/api/funcionarios', {
            method: 'PUT',
            body: formData
        });

        if (response.ok) {
            alert('Funcionário atualizado com sucesso!');
            window.location.href = '/cadastro/atualizar-funcionario.html';
        } else if (response.status === 409) {
            // CPF já cadastrado para outro funcionário
            const error = await response.text();
            alert(error); // Exibe: "O CPF informado já está cadastrado para outro funcionário no sistema."
        } else {
            const error = await response.text();
            alert('Erro ao atualizar funcionário: ' + error);
        }
    } catch (error) {
        console.error('Erro ao atualizar:', error);
        alert('Erro ao tentar atualizar o funcionário. Tente novamente mais tarde.');
    }
});

// Função para comprimir a imagem antes de enviar
async function compressImage(file) {
    const img = new Image();
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');

    return new Promise((resolve) => {
        const reader = new FileReader();
        reader.onload = function(e) {
            img.src = e.target.result;
            img.onload = function() {
                const MAX_WIDTH = 800;
                const MAX_HEIGHT = 600;
                let width = img.width;
                let height = img.height;

                if (width > height) {
                    if (width > MAX_WIDTH) {
                        height *= MAX_WIDTH / width;
                        width = MAX_WIDTH;
                    }
                } else {
                    if (height > MAX_HEIGHT) {
                        width *= MAX_HEIGHT / height;
                        height = MAX_HEIGHT;
                    }
                }

                canvas.width = width;
                canvas.height = height;
                ctx.drawImage(img, 0, 0, width, height);

                canvas.toBlob((blob) => {
                    resolve(new File([blob], file.name, {
                        type: 'image/jpeg',
                        lastModified: Date.now()
                    }));
                }, 'image/jpeg', 0.7);
            };
        };
        reader.readAsDataURL(file);
    });
}

// Excluir funcionário
document.getElementById('btn-excluir').addEventListener('click', async function () {
    const nome = document.getElementById('nome-funcionario').value.trim();
    if (!nome) {
        alert('Por favor, selecione um funcionário para excluir.');
        return;
    }

    if (!confirm(`Tem certeza que deseja excluir o funcionário ${nome}?`)) {
        return;
    }

    try {
        const response = await fetch(`/api/funcionarios/nome/${nome}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert('Funcionário excluído com sucesso!');
            limparCamposFuncionario();
        } else {
            const error = await response.text();
            if (response.status === 409) {
                alert("Não foi possível excluir o funcionário por existir registros relacionados.");
            } else {
                alert("Funcionário não excluído por existir registros relacionados.");
            }
            limparCamposFuncionario();
        }
    } catch (error) {
        console.error('Erro ao excluir:', error);
        alert('Erro ao tentar excluir o funcionário.');
        limparCamposFuncionario();
    }
});

// Função para limpar os campos após erro
function limparCamposFuncionario() {
    document.getElementById('id-funcionario').value = '';
    document.getElementById('nome-funcionario').value = '';
    document.getElementById('cpf').value = '';
    document.getElementById('cpf-original').value = '';
    document.getElementById('cargo').value = '';
    document.getElementById('setor').value = '';
    document.getElementById('data-admissao').value = '';
    document.getElementById('salario').value = '';
    document.getElementById('turno').value = 'Manhã/Tarde';
    document.getElementById('foto-preview').src = '';
    document.getElementById('foto-preview').style.display = 'none';
    document.getElementById('foto').value = ''; // Limpa a entrada do arquivo
}

// Torna os campos editáveis, exceto ID
function tornarCamposEditaveis() {
    const campos = document.querySelectorAll('#form-alterar-funcionario input, #form-alterar-funcionario select');
    campos.forEach(campo => {
        if (campo.id !== 'id-funcionario') {
            campo.removeAttribute('readonly');
            campo.removeAttribute('disabled');
        }
    });
}

// Desabilita todos os campos
function desabilitarCampos() {
    const campos = document.querySelectorAll('#form-alterar-funcionario input, #form-alterar-funcionario select');
    campos.forEach(campo => {
        if (campo.id !== 'id-funcionario' && campo.id !== 'filtro-funcionarios') {
            campo.setAttribute('readonly', true);
            campo.setAttribute('disabled', true);
        }
    });
}

desabilitarCampos();

// Armazenar os dados dos funcionários para filtragem
let todosFuncionarios = [];

// Evento para carregar funcionários no modal
document.getElementById('btn-lista').addEventListener('click', async function () {
    try {
        const response = await fetch('/api/funcionarios/todos');
        if (!response.ok) throw new Error('Erro ao carregar funcionários');
        todosFuncionarios = await response.json(); // Armazena os dados para filtragem
        preencherTabela(todosFuncionarios);

        abrirModal();

        // Adicionar evento de filtragem ao campo de pesquisa
        const pesquisarFuncionario = document.getElementById('pesquisar-funcionario');
        pesquisarFuncionario.addEventListener('input', function () {
            const termo = pesquisarFuncionario.value.toLowerCase();
            const funcionariosFiltrados = todosFuncionarios.filter(funcionario =>
                funcionario.nome.toLowerCase().includes(termo)
            );
            preencherTabela(funcionariosFiltrados);
        });
    } catch (error) {
        console.error('Erro ao carregar funcionários:', error);
        alert('Erro ao carregar funcionários.');
    }
});

// Função para preencher a tabela com os dados dos funcionários
function preencherTabela(funcionarios) {
    const tabelaBody = document.querySelector('#tabela-funcionarios tbody');
    tabelaBody.innerHTML = '';

    funcionarios.forEach(funcionario => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${funcionario.foto ? `<img src="data:image/jpeg;base64,${funcionario.foto}" alt="Foto de ${funcionario.nome}" />` : 'Sem foto'}</td>
            <td>${funcionario.codFuncionario}</td>
            <td>${funcionario.nome}</td>
            <td>${funcionario.cpf}</td>
            <td>${funcionario.cargo}</td>
            <td>${funcionario.setor}</td>
            <td>${funcionario.dataAdmissao}</td>
            <td>${funcionario.salario}</td>
            <td>${funcionario.descricaoTurno || 'N/D'}</td>
        `;
        row.addEventListener('click', function() {
            preencherCampos(funcionario);
            fecharModal();
        });
        tabelaBody.appendChild(row);
    });
}

function abrirModal() {
    const modal = document.getElementById('modal-funcionarios');
    const modalContent = modal.querySelector('.modal-content');
    modal.classList.add('active');
    modalContent.classList.add('active');
}

function fecharModal() {
    const modal = document.getElementById('modal-funcionarios');
    const modalContent = modal.querySelector('.modal-content');
    modal.classList.remove('active');
    modalContent.classList.remove('active');
    setTimeout(() => {
        modal.style.display = 'none';
    }, 300); // Tempo correspondente à transição
}

// Função para formatar data de AAAA-MM-DD para DD-MM-AAAA
function formatarData(data, paraInput = false) {
    if (!data) return '';
    const [ano, mes, dia] = data.split('-');
    return paraInput ? `${ano}-${mes}-${dia}` : `${dia}-${mes}-${ano}`;
}

// Captura a tecla "Esc" para fechar o menu suspenso ou voltar para a tela anterior
document.addEventListener("keyup", function(event) {
    if (event.key === "Escape") {
        const dropdownContainer = document.getElementById('dropdown-container');
        if (dropdownContainer && dropdownContainer.style.display === 'block') {
            dropdownContainer.style.display = 'none';
            console.log("Dropdown fechado.");
        } else {
            console.log("Redirecionando para bt-cadastro.html");
            window.location.href = "/cadastro/bt-cadastro.html";
        }
    }
});

// Evento para pré-visualizar uma nova foto selecionada
document.getElementById('foto').addEventListener('change', function(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const img = document.getElementById('foto-preview');
            img.src = e.target.result;
            img.style.display = 'block';
        };
        reader.readAsDataURL(file);
    }
});