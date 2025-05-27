package br.edu.ifpb.vagahub.repository;

import br.edu.ifpb.vagahub.model.Processo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessoRepository extends JpaRepository<Processo, Long> {}
