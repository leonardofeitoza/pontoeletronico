document.addEventListener("DOMContentLoaded", () => {
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

    // Função para exibir a lista de funcionários
    async function mostrarFuncionarios() {
        if (listaFuncionariosContainer.style.display === "block") {
            listaFuncionariosContainer.style.display = "none";
            return;
        }

        try {
            const response = await fetch("/api/relatorio/funcionarios/individual/nomes-codigos");
            if (response.ok) {
                const funcionarios = await response.json();
                const ul = document.getElementById("lista-funcionarios-ul");
                ul.innerHTML = "";

                funcionarios.forEach(func => {
                    const li = document.createElement("li");
                    li.textContent = `${func.nome} (ID: ${func.id})`;
                    li.dataset.nome = func.nome.toLowerCase();

                    li.addEventListener("click", () => {
                        nomeInput.value = func.nome;
                        idInput.value = func.id;
                        listaFuncionariosContainer.style.display = "none";
                    });

                    ul.appendChild(li);
                });

                listaFuncionariosContainer.style.display = "block";

                const filtroInput = document.getElementById("filtro-funcionarios");
                filtroInput.value = "";
                filtroInput.addEventListener("input", function() {
                    const termo = this.value.toLowerCase();
                    const itens = ul.getElementsByTagName("li");
                    for (let i = 0; i < itens.length; i++) {
                        const nome = itens[i].dataset.nome;
                        itens[i].style.display = nome.includes(termo) ? "block" : "none";
                    }
                });
            } else {
                alert("Erro ao buscar funcionários.");
            }
        } catch (error) {
            console.error("Erro ao buscar funcionários:", error);
        }
    }

    // Função para gerar relatório individual e exibir no modal
    async function visualizarRelatorio() {
        const id = idInput.value;
        if (!id) {
            alert("Selecione um funcionário.");
            return;
        }
        abrirModalPdf(`/api/relatorio/login/individual/visualizar/${id}`);
    }

    // Função para gerar relatório geral e exibir no modal
    async function visualizarRelatorioGlobal() {
        abrirModalPdf(`/api/relatorio/login/global/visualizar`);
    }

    // Função para abrir o modal e definir o src do iframe
    function abrirModalPdf(url) {
        relatorioPdf.src = url;
        modalRelatorio.style.display = "flex";
    }

    // Função para fechar o modal
    function fecharModalPdf() {
        modalRelatorio.style.display = "none";
        relatorioPdf.src = "";
    }

    // Expor funções globalmente
    window.mostrarFuncionarios = mostrarFuncionarios;
    window.visualizarRelatorio = visualizarRelatorio;
    window.visualizarRelatorioGlobal = visualizarRelatorioGlobal;
    window.fecharModalPdf = fecharModalPdf;
});
