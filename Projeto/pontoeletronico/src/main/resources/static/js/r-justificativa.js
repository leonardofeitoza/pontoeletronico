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
                alert('Selecione um funcionário para visualizar o relatório.');
                return;
            }
            if (dataInicial && dataFinal && new Date(dataInicial) > new Date(dataFinal)) {
                alert('A data inicial não pode ser maior que a data final.');
                return;
            }
            try {
                const pdfViewer = document.getElementById('relatorio-pdf');
                let url = `/api/relatorio/justificativa/visualizar/${idFuncionario}`;
                if (dataInicial && dataFinal) {
                    url += `?dataInicial=${dataInicial}&dataFinal=${dataFinal}`;
                }
                pdfViewer.src = url;
                abrirModalPdf();
            } catch (error) {
                console.error('Erro ao visualizar o relatório:', error);
                alert('Erro ao carregar o relatório.');
            }
        }

        async function visualizarRelatorioGlobal() {
            const dataInicial = document.getElementById('data-inicial').value;
            const dataFinal = document.getElementById('data-final').value;
            if ((dataInicial && !dataFinal) || (!dataInicial && dataFinal)) {
                alert("Preencha ambos os campos de Data Inicial e Data Final ou deixe-os vazios para gerar o relatório geral.");
                return;
            }
            if (dataInicial && dataFinal && new Date(dataInicial) > new Date(dataFinal)) {
                alert("A data inicial não pode ser maior que a data final.");
                return;
            }
            try {
                const pdfViewer = document.getElementById('relatorio-pdf');
                let url = "/api/relatorio/justificativa/geral/visualizar";
                if (dataInicial && dataFinal) {
                    url += `?dataInicial=${dataInicial}&dataFinal=${dataFinal}`;
                }
                pdfViewer.src = url;
                abrirModalPdf();
            } catch (error) {
                console.error('Erro ao visualizar o relatório:', error);
                alert('Erro ao carregar o relatório.');
            }
        }

        function abrirModalPdf() {
            const modal = document.getElementById('pdf-modal');
            modal.style.display = 'flex';
        }

        function fecharModalPdf() {
            const modal = document.getElementById('pdf-modal');
            modal.style.display = 'none';
            document.getElementById('relatorio-pdf').src = '';
        }

        window.mostrarFuncionarios = mostrarFuncionarios;
        window.visualizarRelatorio = visualizarRelatorio;
        window.visualizarRelatorioGlobal = visualizarRelatorioGlobal;
        window.abrirModalPdf = abrirModalPdf;
        window.fecharModalPdf = fecharModalPdf;
    });

