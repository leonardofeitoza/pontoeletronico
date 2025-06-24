document.getElementById('login-form').addEventListener('submit', async function (event) {
    event.preventDefault(); // Previne envio do formulário

    const usuario = document.getElementById('usuario').value.trim();
    const senha = document.getElementById('senha').value.trim();

    if (!usuario || !senha) {
        exibirErro("Usuário e senha são obrigatórios.");
        return;
    }

    try {
        const response = await fetch('/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ usuario, senha })
        });

        if (response.ok) {
            const dashboardPath = await response.text(); // Recebe o caminho do dashboard do servidor
            window.location.href = dashboardPath; // Redireciona para o caminho recebido
        } else {
            const errorMessage = await response.text();
            exibirErro(`Erro: ${errorMessage}`);
        }
    } catch (error) {
        exibirErro("Erro ao conectar com o servidor.");
        console.error("Erro:", error);
    }
});

function exibirErro(mensagem) {
    const errorMessage = document.getElementById('error-message');
    errorMessage.textContent = mensagem;
    errorMessage.style.display = 'block';
}

function confirmarSaida() {
    if (confirm("Você realmente deseja sair?")) {
        window.location.href = "/login/login.html"; // Ajuste o caminho
    }
}

