// Funções de controle de status (indicador de cor)
function updateIndicatorColor() {
    const select = document.getElementById('status-select');
    const indicator = document.getElementById('status-indicator');

    switch (select.value) {
        case 'em-andamento':
            indicator.style.backgroundColor = '#FFFF6E'; // Amarelo
            break;
        case 'banco-de-talentos':
            indicator.style.backgroundColor = '#8BEDB5'; // Verde
            break;
        case 'finalizado':
            indicator.style.backgroundColor = '#7490EB'; // Azul
            break;
        default:
            indicator.style.backgroundColor = '#ccc'; // Cinza padrão
    }
}

// Funções de edição do processo
function editarProcesso() {
    document.querySelectorAll(".info-group input").forEach(input => {
        input.style.display = "block";
    });
    document.querySelector("#select-tipo-contratacao").style.display = "block";
    document.querySelectorAll(".info-group p").forEach(info => {
        info.style.display = "none";
    });
    document.querySelector(".btn-edit").style.display = "none";
    document.querySelector(".btn-delete").style.display = "none";
    document.querySelector(".btn-save").style.display = "block";
    document.querySelector(".btn-cancel-edit").style.display = "block";
}

function cancelarEdicao() {
    document.querySelectorAll(".info-group input").forEach(input => {
        input.style.display = "none";
    });
    document.querySelector("#select-tipo-contratacao").style.display = "none";
    document.querySelectorAll(".info-group p").forEach(info => {
        info.style.display = "block";
    });
    document.querySelector(".btn-edit").style.display = "block";
    document.querySelector(".btn-delete").style.display = "block";
    document.querySelector(".btn-save").style.display = "none";
    document.querySelector(".btn-cancel-edit").style.display = "none";
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

    // Configuração dos botões de edição
    const btnEdit = document.querySelector(".btn-edit");
    if (btnEdit) {
        btnEdit.addEventListener('click', editarProcesso);
    }

    const btnCancelEdit = document.querySelector(".btn-cancel-edit");
    if (btnCancelEdit) {
        btnCancelEdit.addEventListener('click', cancelarEdicao);
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