package br.edu.ifpb.vagahub.repository;

import br.edu.ifpb.vagahub.model.Processo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessoRepository extends JpaRepository<Processo, Long> {
    // Método para buscar processos por status
    List<Processo> findByStatus(String status);

    // Método para buscar processos ordenados por status
    List<Processo> findByStatusOrderByIdDesc(String status);

}
