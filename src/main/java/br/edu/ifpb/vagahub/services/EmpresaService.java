package br.edu.ifpb.vagahub.services;

import br.edu.ifpb.vagahub.model.Empresa;
import br.edu.ifpb.vagahub.model.Habilidade;
import br.edu.ifpb.vagahub.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository repository;

    public Empresa save(Empresa empresa) {
        return repository.save(empresa);
    }

    public Empresa findOrCreate(String nomeEmpresa){
        var empresaExistente = findByNomeIgnoreCase(nomeEmpresa.trim());
        if (empresaExistente.isPresent()) {
            return empresaExistente.get();
        } else {
            var novaEmpresa = new Empresa();
            novaEmpresa.setNomeEmpresa(nomeEmpresa.trim());
            save(novaEmpresa);
            return novaEmpresa;
        }
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Empresa findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Optional<Empresa> findByNomeIgnoreCase(String nome) {
        return repository.findByNomeEmpresaIgnoreCase(nome);
    }

    public List<Empresa> findAll() {
        return repository.findAll();
    }
}