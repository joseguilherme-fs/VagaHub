package br.edu.ifpb.vagahub.repository;

import br.edu.ifpb.vagahub.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByNomeEmpresaIgnoreCase(String nome);
}
