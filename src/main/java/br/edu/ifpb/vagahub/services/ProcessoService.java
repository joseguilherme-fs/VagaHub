package br.edu.ifpb.vagahub.services;

import br.edu.ifpb.vagahub.model.Empresa;
import br.edu.ifpb.vagahub.model.Processo;
import br.edu.ifpb.vagahub.repository.ProcessoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProcessoService {

    @Autowired
    private ProcessoRepository repository;

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private HabilidadesService habilidadesService;


    public Processo save(Processo processo) {
        return repository.save(processo);
    }

    public Processo criar(Processo processo, String empresaNome, String habilidadesTexto) {

        processo.setEmpresa(empresaService.findOrCreate(empresaNome));
        processo.setHabilidades(habilidadesService.findOrCreate(habilidadesTexto));
        save(processo);

        return processo;
    }

    public Processo deleteById(Long id) {
        Optional<Processo> p = repository.findById(id);
        if (p.isPresent()) {
            repository.deleteById(p.get().getId());
            return p.get();
        }
        return null;
    }

    public Processo findById(Long id) {
        return repository.findById(id).orElse(null);
    }


    public List<Processo> findAll() {
        return repository.findAll();
    }
}

