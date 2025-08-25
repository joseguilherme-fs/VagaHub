document.querySelector('form').addEventListener('submit', function(e) {
    const status = document.getElementById('statusProcesso').value;
    if (status === 'Finalizado' && !confirm('Tem certeza que deseja finalizar este processo? Esta ação irá marcar o processo como concluído.')) {
        e.preventDefault();
    } else if (status === 'Cancelado' && !confirm('Tem certeza que deseja cancelar este processo?')) {
        e.preventDefault();
    }
});