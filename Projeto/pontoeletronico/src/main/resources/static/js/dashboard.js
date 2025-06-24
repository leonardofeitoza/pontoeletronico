document.getElementById("logout").addEventListener("click", function (event) {
    event.preventDefault();

    if (confirm("Deseja realmente sair?")) {
        // Redireciona para a página de login
        window.location.href = "/login/login.html";
    }
});
