package br.edu.ifpb.vagahub.controller;

import br.edu.ifpb.vagahub.model.Processo;
import br.edu.ifpb.vagahub.repository.ProcessoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/processos")
public class ProcessoController {

    @Autowired
    private ProcessoRepository repositorio;

    @GetMapping
    public List<Processo> listarTodos() {
        return repositorio.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Processo criar(@RequestBody Processo processo) {
        return repositorio.save(processo);
    }

    @GetMapping("/{id}")
    public Processo buscar(@PathVariable Long id) {
        return repositorio.findById(id).get();
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        repositorio.deleteById(id);
    }
}
