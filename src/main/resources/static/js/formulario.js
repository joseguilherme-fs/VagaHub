let telaCandidatura = document.querySelector('.registro-candidatura');
let telaLembrete = document.querySelector('.configuracao-lembrete')
let telaConfirmacao = document.querySelector('.finalizar-candidatura')

//candidatura

document.querySelector('#btn-proximo-candidatura').addEventListener('click', (e) => {
    e.preventDefault();
    telaCandidatura.style.display = 'none';
    telaLembrete.style.display = 'flex';
});

//lembrete
document.getElementById('btn-voltar-lembrete').addEventListener('click', () => {
    telaLembrete.style.display = 'none';
    telaCandidatura.style.display = 'flex';
});
document.querySelector('.lembrete').addEventListener('submit', (e) => {
    e.preventDefault();
    telaLembrete.style.display = 'none';
    telaConfirmacao.style.display = 'flex';
});

//confirmação
document.getElementById('btn-voltar-finalizar').addEventListener('click', () => {
    telaConfirmacao.style.display = 'none';
    telaLembrete.style.display = 'flex';
});
document.getElementById('btn-voltar-finalizar').addEventListener('click', () => {
    telaConfirmacao.style.display = 'none';
    telaLembrete.style.display = 'flex';
});


function dataHoje() {
    let hoje = new Date();
    let ano = hoje.getFullYear();
    let mes = String(hoje.getMonth() + 1).padStart(2, '0');
    let dia = String(hoje.getDate()).padStart(2, '0');
    hoje = `${ano}-${mes}-${dia}`;
    return hoje
}

document.querySelectorAll('.direito').forEach(input => {
    input.min = dataHoje();
});

const divPorSemana = document.querySelector('.por-semana');
const divDatasEspecificas = document.querySelector('.datas-especificas');

function atualizarVisibilidade() {
    let selecionado = document.querySelector('input[name="receber-lembretes"]:checked').value;

    if (selecionado === 'por-semana') {
        divPorSemana.style.display = 'flex';
        divDatasEspecificas.style.display = 'none';
    } else {
        divDatasEspecificas.style.display = 'flex';
    }
}

atualizarVisibilidade();

