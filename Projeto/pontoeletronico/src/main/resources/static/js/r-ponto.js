document.addEventListener('DOMContentLoaded', () => {
    const nomeInput = document.getElementById("nome-funcionario");
    const idInput = document.getElementById("id-funcionario");
    const listaFuncionariosContainer = document.getElementById("dropdown-container");
    const relatorioPdf = document.getElementById("relatorio-pdf");
    const modalRelatorio = document.getElementById("pdf-modal");

    // Captura a tecla ESC para fechar telas na sequência correta
    document.addEventListener("keyup", function(event) {
        if (event.key === "Escape") {
            if (modalRelatorio && modalRelatorio.style.display === "flex") {
                // Fecha o modal de relatório
                fecharModalPdf();
                console.log("Modal de relatório fechado.");
            } else if (listaFuncionariosContainer && listaFuncionariosContainer.style.display === "block") {
                // Fecha a lista de funcionários
                listaFuncionariosContainer.style.display = "none";
                console.log("Lista de funcionários fechada.");
            } else {
                // Se nada mais estiver aberto, redireciona para relatorios.html
                console.log("Redirecionando para bt-relatorio.html");
                window.location.href = "/relatorio/bt-relatorio.html";
            }
        }
    });


    async function mostrarFuncionarios() {
        const dropdown = document.getElementById('dropdown-container');
        // Se já estiver aberto, fecha e retorna
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
                    // Armazena o nome em minúsculas para o filtro
                    li.dataset.nome = func.nome.toLowerCase();
                    li.addEventListener('click', () => {
                        document.getElementById('nome-funcionario').value = func.nome;
                        document.getElementById('id-funcionario').value = func.id;
                        dropdown.style.display = 'none';
                    });
                    ul.appendChild(li);
                });
                dropdown.style.display = 'block';

                // Configura o campo de busca para filtrar os itens
                const filtroInput = document.getElementById('filtro-funcionarios');
                filtroInput.value = ""; // Limpa o campo de filtro a cada abertura
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

    async function visualizarRelatorio() {
        const idFuncionario = document.getElementById('id-funcionario').value;
        const dataInicial = document.getElementById('data-inicial').value;
        const dataFinal = document.getElementById('data-final').value;

        if (!idFuncionario) {
            alert("Selecione um funcionário para visualizar o relatório individual.");
            return;
        }
        // Se apenas um dos campos de data for preenchido, avisa o usuário
        if ((dataInicial && !dataFinal) || (!dataInicial && dataFinal)) {
            alert("Preencha ambos os campos de Data Inicial e Data Final ou deixe-os vazios.");
            return;
        }
        // Validação: se ambos preenchidos, a data inicial não pode ser maior que a data final
        if (dataInicial && dataFinal && new Date(dataInicial) > new Date(dataFinal)) {
            alert("A data inicial não pode ser maior que a data final.");
            return;
        }

        // Monta a URL para o relatório individual
        let url = `/api/relatorio/ponto/individual/visualizar/${idFuncionario}`;
        if (dataInicial && dataFinal) {
            url += `?dataInicial=${dataInicial}&dataFinal=${dataFinal}`;
        }

        // Define o src do iframe e exibe o modal em tela cheia
        const pdfViewer = document.getElementById('relatorio-pdf');
        pdfViewer.src = url;
        abrirModalPdf();
    }

    window.visualizarRelatorio = visualizarRelatorio;


    async function gerarRelatorio() {
        const idFuncionario = document.getElementById('id-funcionario').value;
        const dataInicial = document.getElementById('data-inicial').value;
        const dataFinal = document.getElementById('data-final').value;

        // Validação de datas
        if (dataInicial && dataFinal && new Date(dataInicial) > new Date(dataFinal)) {
            alert('A data inicial não pode ser maior que a data final.');
            return;
        }

        if (!idFuncionario) {
            alert('Selecione um funcionário para gerar o relatório.');
            return;
        }

        try {
            let url = `/api/relatorio/ponto/individual/gerar/${idFuncionario}`;
            if (dataInicial && dataFinal) {
                url += `?dataInicial=${dataInicial}&dataFinal=${dataFinal}`;
            }

            const response = await fetch(url, { method: 'GET' });

            if (response.ok) {
                const blob = await response.blob();
                const downloadUrl = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = downloadUrl;
                a.download = `relatorio_ponto_individual_${idFuncionario}.pdf`;
                a.click();
            } else {
                alert('Erro ao gerar relatório em PDF.');
            }
        } catch (error) {
            console.error('Erro ao gerar relatório em PDF:', error);
        }
    }

    window.mostrarFuncionarios = mostrarFuncionarios;
    window.visualizarRelatorio = visualizarRelatorio;
    window.gerarRelatorio = gerarRelatorio;
});


// Exemplo de função "visualizarRelatorioGlobal" que abre o modal:
async function visualizarRelatorioGlobal() {
    const dataInicial = document.getElementById('data-inicial').value;
    const dataFinal = document.getElementById('data-final').value;

    // Validações...
    if ((dataInicial && !dataFinal) || (!dataInicial && dataFinal)) {
        alert("Preencha ambos os campos de Data Inicial e Data Final ou deixe-os vazios para gerar o relatório geral.");
        return;
    }
    if (dataInicial && dataFinal && new Date(dataInicial) > new Date(dataFinal)) {
        alert("A data inicial não pode ser maior que a data final.");
        return;
    }

    // Monta a URL
    let url = "/api/relatorio/ponto/geral/visualizar";
    if (dataInicial && dataFinal) {
        url += `?dataInicial=${dataInicial}&dataFinal=${dataFinal}`;
    }

    // Define o src do iframe
    const pdfViewer = document.getElementById('relatorio-pdf');
    pdfViewer.src = url;

    // Exibe o modal em tela cheia
    abrirModalPdf();
}

function abrirModalPdf() {
    const modal = document.getElementById('pdf-modal');
    modal.style.display = 'flex'; // Exibe o modal
}

// Fecha o modal e limpa o src do iframe, se desejar
function fecharModalPdf() {
    const modal = document.getElementById('pdf-modal');
    modal.style.display = 'none';
    const pdfViewer = document.getElementById('relatorio-pdf');
    pdfViewer.src = ''; // Opcional: limpa o src para recarregar do zero quando abrir de novo
}

window.visualizarRelatorioGlobal = visualizarRelatorioGlobal;
window.abrirModalPdf = abrirModalPdf;
window.fecharModalPdf = fecharModalPdf;
