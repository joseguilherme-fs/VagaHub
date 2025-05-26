package br.edu.ifpb.vagahub.repository;

import br.edu.ifpb.vagahub.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {}
