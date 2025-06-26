
// Funções de controle de status (indicador de cor)
function updateIndicatorColor() {
    const select = document.getElementById('status-select');
    const indicator = document.querySelector('.status-indicator');

    switch (select.value) {
        case 'Em Andamento':
            indicator.style.backgroundColor = '#8BEDB5'; // Verde
            break;
        case 'Banco de Talentos':
            indicator.style.backgroundColor = '#FFFF6E'; // Amarelo
            break;
        case 'Finalizado':
            indicator.style.backgroundColor = '#7490EB'; // Azul
            break;
        default:
            indicator.style.backgroundColor = '#ccc'; // Cinza padrão
    }
}

// Funções de edição do processo
function habilitarEdicao() {
    document.querySelectorAll(".input-group input").forEach(input => {
        input.style.display = "block";
    });
    document.querySelector(".input-group select").style.display = "block";
    document.querySelectorAll(".input-group p").forEach(info => {
        info.style.display = "none";
    });
    document.querySelector(".btn-edit").style.display = "none";
    document.querySelector(".btn-delete").style.display = "none";
    document.querySelector(".btn-save").style.display = "block";
    document.querySelector(".btn-cancel-edit").style.display = "block";
}

function cancelarEdicao() {
    document.querySelectorAll(".input-group input").forEach(input => {
        input.style.display = "none";
    });
    document.querySelector(".input-group select").style.display = "none";
    document.querySelectorAll(".input-group p").forEach(info => {
        info.style.display = "block";
    });
    document.querySelector(".btn-edit").style.display = "block";
    document.querySelector(".btn-delete").style.display = "block";
    document.querySelector(".btn-save").style.display = "none";
    document.querySelector(".btn-cancel-edit").style.display = "none";
}

function mostrarBotaoSalvarStatus(){
    document.querySelector(".btn-update-status").style.display = "block";
}

// Funções de exclusão (modal e ação)
function confirmarExclusao(id) {
    document.getElementById('btnConfirmarExclusao').onclick = function() {
        excluirProcesso(id);
    };
    document.getElementById('confirmModal').style.display = 'block';
}

function fecharModal() {
    document.getElementById('confirmModal').style.display = 'none';
}

function excluirProcesso(id) {
    // Implementar lógica de exclusão via AJAX ou redirecionamento
    console.log('Excluindo processo:', id);
    // Fechar o modal após a exclusão
    fecharModal();
}

// Inicialização quando o DOM estiver carregado
document.addEventListener('DOMContentLoaded', () => {
    // Configuração do indicador de status
    const select = document.getElementById('status-select');
    if (select) {
        select.addEventListener('change', updateIndicatorColor);
        updateIndicatorColor(); // Inicializa a cor
    }

    // Configuração dos modais de exclusão (unificando os modais)
    const deleteButtons = document.querySelectorAll('.delete, .btn-delete');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            const modalId = this.classList.contains('delete') ? 'deleteModal' : 'confirmModal';
            document.getElementById(modalId).style.display = 'flex';
        });
    });

    // Configuração dos botões de confirmação
    const confirmDelete = document.getElementById('confirmDelete');
    if (confirmDelete) {
        confirmDelete.addEventListener('click', function() {
            document.getElementById('deleteModal').style.display = 'none';
            alert('Candidatura excluída!'); // Apenas para demonstração
        });
    }

    // Fechar modais ao clicar fora ou no X
    window.addEventListener('click', function(event) {
        const modals = ['deleteModal', 'confirmModal'];
        modals.forEach(modalId => {
            const modal = document.getElementById(modalId);
            if (event.target === modal) {
                modal.style.display = 'none';
            }
        });
    });
});

// Aguarde o DOM carregar
document.addEventListener("DOMContentLoaded", function () {
    const mensagem = document.getElementById("mensagemSucesso");

    // Verifica se a mensagem existe
    if (mensagem) {
        setTimeout(() => {
            mensagem.style.transition = "opacity 0.5s ease";
            mensagem.style.opacity = "0"; // Transição para desvanecer

            // Remove completamente o elemento após a transição
            setTimeout(() => mensagem.remove(), 500);
        }, 3000); // A mensagem ficará visível por 3 segundos
    }
});

document.addEventListener("DOMContentLoaded", function () {
    const mensagem = document.getElementById("mensagemSucesso");

    if (mensagem) {
        mensagem.style.opacity = "1";
        mensagem.style.visibility = "visible"; // Mostra a mensagem imediatamente
        setTimeout(() => {
            mensagem.style.opacity = "0";
            mensagem.style.visibility = "hidden"; // Oculta a mensagem após 3 segundos
        }, 3000);
    }
});