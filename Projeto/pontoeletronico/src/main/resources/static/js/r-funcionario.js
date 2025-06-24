document.addEventListener('DOMContentLoaded', () => {
    // Evento para capturar a tecla ESC e fechar telas na sequência correta
    document.addEventListener("keyup", function(event) {
        if (event.key === "Escape") {
            const modalRelatorio = document.getElementById('pdf-modal');
            const listaFuncionariosContainer = document.getElementById('dropdown-container');

            if (modalRelatorio && modalRelatorio.style.display === 'flex') {
                // Se o modal de relatório estiver aberto, fecha-o
                fecharModalPdf();
                console.log("Modal de relatório fechado.");
            } else if (listaFuncionariosContainer && listaFuncionariosContainer.style.display === 'block') {
                // Se a lista de funcionários estiver aberta, fecha-a
                listaFuncionariosContainer.style.display = 'none';
                console.log("Lista de funcionários fechada.");
            } else {
                // Se nada mais estiver aberto, redireciona para a tela de relatórios
                console.log("Redirecionando para bt-relatorio.html");
                window.location.href = "/relatorio/bt-relatorio.html";
            }
        }
    });
});

// Função para exibir a lista de funcionários
async function mostrarFuncionarios() {
    const dropdown = document.getElementById('dropdown-container');

    if (dropdown.style.display === 'block') {
        dropdown.style.display = 'none';
        return;
    }

    try {
        const response = await fetch('/api/relatorio/funcionarios/individual/nomes-codigos');
        if (response.ok) {
            const funcionarios = await response.json();
            const ul = document.getElementById('lista-funcionarios-ul');
            ul.innerHTML = '';

            funcionarios.forEach(func => {
                const li = document.createElement('li');
                li.textContent = `${func.nome} (ID: ${func.id})`;
                li.dataset.nome = func.nome.toLowerCase();

                li.addEventListener('click', () => {
                    document.getElementById('nome-funcionario').value = func.nome;
                    document.getElementById('id-funcionario').value = func.id;
                    dropdown.style.display = 'none';
                });

                ul.appendChild(li);
            });

            dropdown.style.display = 'block';

            const filtroInput = document.getElementById('filtro-funcionarios');
            filtroInput.value = "";
            filtroInput.addEventListener('input', function() {
                const termo = this.value.toLowerCase();
                const itens = ul.getElementsByTagName('li');
                for (let i = 0; i < itens.length; i++) {
                    const nome = itens[i].dataset.nome;
                    itens[i].style.display = nome.indexOf(termo) !== -1 ? 'block' : 'none';
                }
            });
        } else {
            alert('Erro ao buscar funcionários.');
        }
    } catch (error) {
        console.error('Erro ao buscar funcionários:', error);
    }
}

// Função para visualizar relatório individual
async function visualizarRelatorio() {
    const idFuncionario = document.getElementById('id-funcionario').value;

    if (!idFuncionario) {
        alert('Selecione um funcionário para visualizar o relatório.');
        return;
    }

    try {
        const pdfViewer = document.getElementById('relatorio-pdf');
        pdfViewer.src = `/api/relatorio/funcionarios/individual/visualizar/${idFuncionario}`;
        abrirModalPdf();
    } catch (error) {
        console.error('Erro ao exibir relatório:', error);
    }
}

// Função para visualizar relatório global
async function visualizarRelatorioGlobal() {
    try {
        const pdfViewer = document.getElementById('relatorio-pdf');
        pdfViewer.src = `/api/relatorio/funcionarios/global/visualizar`;
        abrirModalPdf();
    } catch (error) {
        console.error('Erro ao exibir relatório global:', error);
    }
}

// Funções para abrir e fechar o modal
function abrirModalPdf() {
    const modal = document.getElementById('pdf-modal');
    modal.style.display = 'flex';
}

function fecharModalPdf() {
    const modal = document.getElementById('pdf-modal');
    modal.style.display = 'none';
    document.getElementById('relatorio-pdf').src = ''; // Opcional: limpa o src
}

// Expor funções globalmente
window.mostrarFuncionarios = mostrarFuncionarios;
window.visualizarRelatorio = visualizarRelatorio;
window.visualizarRelatorioGlobal = visualizarRelatorioGlobal;
window.abrirModalPdf = abrirModalPdf;
window.fecharModalPdf = fecharModalPdf;
