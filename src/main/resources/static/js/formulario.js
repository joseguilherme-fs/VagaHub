let telaCandidatura = document.querySelector('.registro-candidatura');
let telaLembrete = document.querySelector('.configuracao-lembrete');
let telaConfirmacao = document.querySelector('.finalizar-candidatura');

//candidatura
function camposCandidaturaValidos() {
    const campos = document.querySelectorAll('.registro-candidatura .campo,.registro-candidatura .form-select');
    for (let campo of campos) {
        if (!campo.value.trim()) {
            return false;
        }
    }
    return true;
}

document.querySelector('#btn-proximo-candidatura').addEventListener('click', (e) => {
    if (camposCandidaturaValidos()) {
        telaCandidatura.style.display = 'none';
        telaLembrete.style.display = 'flex';
    } else {
        alert("Por favor, preencha todos os campos obrigatórios.");
    }

});

//lembrete
let campoPorSemana = document.getElementById("campoPorSemana");
let campoHorario = document.getElementById("campoHorario")

function camposLembretesValidos() {
    const campos = document.querySelectorAll('.configuracao-lembrete .campo,.configuracao-lembrete .form-select');
    if (campoPorSemana.checked) {
        if (!campoHorario.value.trim()) {
            return false;
        }
    } else {
        for (let campo of campos) {
            if (!campo.value.trim()) {
                return false;
            }
        }
    }
    return true;
}

document.getElementById('btn-voltar-lembrete').addEventListener('click', () => {
    telaLembrete.style.display = 'none';
    telaCandidatura.style.display = 'flex';
});
document.querySelector('#btn-proximo-lembrete').addEventListener('click', (e) => {
    if (camposLembretesValidos()) {
        telaLembrete.style.display = 'none';
        telaConfirmacao.style.display = 'flex';
    } else {
        alert("Por favor, preencha todos os campos obrigatórios.");
    }

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

