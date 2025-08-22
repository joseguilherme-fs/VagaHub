// Variáveis globais para controle de status
let statusAnterior = '';
let novoStatusSelecionado = '';
let processoId = '';

// Funções de controle de status (indicador de cor)
function updateIndicatorColor() {
    let select = document.getElementById('status-select');
    let indicator = document.getElementById('status-indicator');

    if (!select || !indicator) return;

    // Remove todas as classes de status anteriores
    indicator.className = 'status-indicator';

    switch (select.value) {
        case 'Em Andamento':
            indicator.classList.add('status-em-andamento');
            break;
        case 'Banco de Talentos':
            indicator.classList.add('status-banco-de-talentos');
            break;
        case 'Finalizado':
            indicator.classList.add('status-finalizado');
            break;
        default:
            indicator.classList.add('status-default');
    }
}

// Função para atualizar status via AJAX
function atualizarStatus(id, novoStatus) {
    fetch(`/processos/atualizar-status/${id}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `status=${encodeURIComponent(novoStatus)}`
    })
        .then(response => response.text())
        .then(data => {
            if (data === 'success') {
                // Atualiza a cor do indicador
                updateIndicatorColor();

                // Mostra mensagem de sucesso
                mostrarMensagem('Status atualizado com sucesso!', 'success');

                // Se foi finalizado, pode redirecionar ou recarregar para mostrar na página de finalizados
                if (novoStatus === 'Finalizado') {
                    setTimeout(() => {
                        // Opcional: redirecionar para a página de processos finalizados
                        // window.location.href = '/processos-finalizados';
                    }, 1500);
                }
            } else {
                // Reverte a seleção em caso de erro
                document.getElementById('status-select').value = statusAnterior;
                updateIndicatorColor();
                mostrarMensagem('Erro ao atualizar status. Tente novamente.', 'error');
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            // Reverte a seleção em caso de erro
            document.getElementById('status-select').value = statusAnterior;
            updateIndicatorColor();
            mostrarMensagem('Erro ao atualizar status. Tente novamente.', 'error');
        });
}

// Função para mostrar mensagens de feedback
function mostrarMensagem(texto, tipo) {
    // Remove mensagem anterior se existir
    const mensagemAnterior = document.querySelector('.mensagem-feedback');
    if (mensagemAnterior) {
        mensagemAnterior.remove();
    }

    // Cria nova mensagem
    const mensagem = document.createElement('div');
    mensagem.className = `mensagem-feedback ${tipo}`;
    mensagem.textContent = texto;
    mensagem.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 25px;
        border-radius: 5px;
        z-index: 1000;
        font-weight: bold;
        opacity: 0;
        transition: opacity 0.3s ease;
        ${tipo === 'success' ? 'background-color: #4CAF50; color: white;' : 'background-color: #f44336; color: white;'}
    `;

    document.body.appendChild(mensagem);

    // Fade in
    setTimeout(() => {
        mensagem.style.opacity = '1';
    }, 100);

    // Remove após 3 segundos
    setTimeout(() => {
        mensagem.style.opacity = '0';
        setTimeout(() => {
            if (mensagem.parentNode) {
                mensagem.parentNode.removeChild(mensagem);
            }
        }, 300);
    }, 3000);
}

// Funções de modal para confirmação de status
function abrirStatusModal(novoStatus) {
    document.getElementById('novo-status').textContent = novoStatus;
    document.getElementById('statusModal').style.display = 'flex';
}

function fecharStatusModal() {
    // Reverte a seleção
    document.getElementById('status-select').value = statusAnterior;
    document.getElementById('statusModal').style.display = 'none';
}

function confirmarMudancaStatus() {
    fecharStatusModal();
    atualizarStatus(processoId, novoStatusSelecionado);
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
    console.log('Excluindo processo:', id);
    fecharModal();
}

// Inicialização quando o DOM estiver carregado
document.addEventListener('DOMContentLoaded', () => {
    // Configuração do indicador de status
    const select = document.getElementById('status-select');
    if (select) {
        // Armazena o status inicial
        statusAnterior = select.value;
        processoId = select.getAttribute('data-processo-id');

        // Atualiza a cor inicial
        updateIndicatorColor();

        // Adiciona listener para mudanças
        select.addEventListener('change', function() {
            novoStatusSelecionado = this.value;

            // Se está mudando para "Finalizado", pede confirmação
            if (novoStatusSelecionado === 'Finalizado' && statusAnterior !== 'Finalizado') {
                abrirStatusModal(novoStatusSelecionado);
            } else {
                // Para outras mudanças, atualiza diretamente
                atualizarStatus(processoId, novoStatusSelecionado);
                statusAnterior = novoStatusSelecionado;
            }
        });
    }

    // Configuração dos modais de exclusão
    const deleteButton = document.getElementById('botao-modal');
    if (deleteButton) {
        deleteButton.addEventListener('click', function() {
            document.getElementById("confirmModal").style.display = 'flex';
        });
    }

    // Fechar modais ao clicar fora
    window.addEventListener('click', function(event) {
        const modals = ['deleteModal', 'confirmModal', 'statusModal'];
        modals.forEach(modalId => {
            const modal = document.getElementById(modalId);
            if (event.target === modal) {
                modal.style.display = 'none';
            }
        });
    });

    // Configuração de mensagens de sucesso/erro
    const mensagem = document.getElementById("mensagemSucesso");
    if (mensagem) {
        mensagem.style.opacity = "1";
        mensagem.style.visibility = "visible";
        setTimeout(() => {
            mensagem.style.opacity = "0";
            mensagem.style.visibility = "hidden";
        }, 3000);
    }
});