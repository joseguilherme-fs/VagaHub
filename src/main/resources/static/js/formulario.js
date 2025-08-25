let telaCandidatura = document.querySelector('.registro-candidatura');
let telaLembrete = document.querySelector('.configuracao-lembrete');
let telaConfirmacao = document.querySelector('.finalizar-candidatura');

//verifica se os campos required estão preenchidos
function camposValidos(containerSelector) {
    const campos = document.querySelectorAll(
        `${containerSelector} .campo, ${containerSelector} .form-select`
    );

    for (let campo of campos) {
        if (campo.required && !campo.value.trim()) {
            return false;
        }
    }
    return true;
}

//confirma se os campos de cadastro do processo estão preenchidos
document.querySelector('#btn-proximo-candidatura').addEventListener('click', (e) => {
    if (camposValidos('.registro-candidatura')) {
        telaCandidatura.style.display = 'none';
        telaLembrete.style.display = 'flex';
    } else {
        alert("Por favor, preencha todos os campos obrigatórios.");
    }

});

//confirma se os campos do lembrete estão preenchidos (caso o usuário queira um lembrete)
document.querySelector('#btn-proximo-lembrete').addEventListener('click', (e) => {
    if (camposValidos('.configuracao-lembrete')) {
        telaLembrete.style.display = 'none';
        telaConfirmacao.style.display = 'flex';
    } else {
        alert("Por favor, preencha todos os campos obrigatórios.");
    }

});

//volta para inputs do cadastro
document.getElementById('btn-voltar-lembrete').addEventListener('click', () => {
    telaLembrete.style.display = 'none';
    telaCandidatura.style.display = 'flex';
});

//finaliza o cadastro do processo
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

function atualizarVisibilidade() {
    //divs dos campos de lembrete
    let divPorSemana = document.querySelector('.por-semana');
    let divDatasEspecificas = document.querySelector('.datas-especificas');
    let divNuncaReceber = document.querySelector('.nunca');
    let divDiaSemana = document.querySelector('.dia-semana');

    let selecionado = document.querySelector('input[name="frequenciaLembretes"]:checked').value;

    if (selecionado === 'por-semana') {
        divPorSemana.style.display = 'flex';
        divDiaSemana.style.display = 'flex';
        divDatasEspecificas.style.display = 'none';
        divNuncaReceber.style.display = 'none';
    } else if (selecionado === 'datas-especificas') {
        divDatasEspecificas.style.display = 'flex';
        divPorSemana.style.display = 'flex';
        divNuncaReceber.style.display = 'none';
        divDiaSemana.style.display = 'none';
    } else {
        divDatasEspecificas.style.display = 'none';
        divPorSemana.style.display = 'none';
        divDiaSemana.style.display = 'none';
        divNuncaReceber.style.display = 'flex';
    }
}

//retira a obrigatoriedade de preenchimento dos campos de lembrete se o usuário não quiser receber lembretes
function alterarRequired(selecao){
    if (selecao === "nunca"){
        document.getElementById("horarioLembrete").required = false;
        document.getElementById('diaDaSemana').required = false;
    } else if (selecao === "datas-especificas") {
        document.getElementById("horarioLembrete").required = true;
        document.getElementById('diaDaSemana').required = false;
    } else if (selecao === "por-semana"){
        document.getElementById("horarioLembrete").required = true;
        document.getElementById('diaDaSemana').required = true;
    }
}

atualizarVisibilidade();

