package br.edu.ifpb.vagahub.repository;

import br.edu.ifpb.vagahub.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByProcessoId(Long processoId);

    // Aqui aponta para Usuario.idUsuario
    Optional<Avaliacao> findByProcessoIdAndUsuarioIdUsuario(Long processoId, Long idUsuario);

    // Aqui também precisa do caminho correto
    boolean existsByProcessoIdAndUsuarioIdUsuario(Long processoId, Long idUsuario);
}
