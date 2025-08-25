package br.edu.ifpb.vagahub.services;

import br.edu.ifpb.vagahub.model.Avaliacao;
import br.edu.ifpb.vagahub.model.Processo;
import br.edu.ifpb.vagahub.model.Usuario;
import br.edu.ifpb.vagahub.repository.AvaliacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    public Avaliacao salvar(Avaliacao avaliacao) {
        return avaliacaoRepository.save(avaliacao);
    }

    public List<Avaliacao> buscarPorProcesso(Long processoId) {
        return avaliacaoRepository.findByProcessoId(processoId);
    }

    public Optional<Avaliacao> buscarPorProcessoEUsuario(Long processoId, Long usuarioId) {
        return avaliacaoRepository.findByProcessoIdAndUsuarioIdUsuario(processoId, usuarioId);
    }

    public boolean usuarioJaAvaliouProcesso(Long processoId, Long usuarioId) {
        return avaliacaoRepository.existsByProcessoIdAndUsuarioIdUsuario(processoId, usuarioId);
    }
    // Cria e salva avaliação com garantia de Integer de 1 a 5
    public Avaliacao criarAvaliacao(Processo processo, Usuario usuario, Integer avaliacaoGeral,
                                    String comentarios, String recomendacao) {
        if (avaliacaoGeral == null || avaliacaoGeral < 1 || avaliacaoGeral > 5) {
            throw new IllegalArgumentException("Avaliação geral deve estar entre 1 e 5");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setProcesso(processo);
        avaliacao.setUsuario(usuario);
        avaliacao.setAvaliacaoGeral(avaliacaoGeral);
        avaliacao.setComentarios(comentarios);
        avaliacao.setRecomendacao(recomendacao);

        return salvar(avaliacao);
    }
}
