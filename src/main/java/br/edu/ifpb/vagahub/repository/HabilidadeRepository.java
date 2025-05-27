package br.edu.ifpb.vagahub.repository;

import br.edu.ifpb.vagahub.model.Habilidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HabilidadeRepository extends JpaRepository<Habilidade, Long> {
    Optional<Habilidade> findByNomeHabilidadeIgnoreCase(String nomeTrimado);
}
