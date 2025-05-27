package br.edu.ifpb.vagahub.services;

import br.edu.ifpb.vagahub.model.Habilidade;
import br.edu.ifpb.vagahub.repository.HabilidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HabilidadesService {

    @Autowired
    private HabilidadeRepository repository;

    public Habilidade save(Habilidade habilidade) {
        return repository.save(habilidade);
    }

    public List<Habilidade> findOrCreate(String nomes){
        List<Habilidade> habilidadesAssociadas = new ArrayList<>();
        String[] nomesHabilidades = nomes.split(",");

        for (String nomeHabilidade : nomesHabilidades) {
            String nomeTrimado = nomeHabilidade.trim();
            if (!nomeTrimado.isEmpty()) {
                Optional<Habilidade> habilidadeExistente = findByNomeIgnoreCase(nomeTrimado);
                if (habilidadeExistente.isPresent()) {
                    habilidadesAssociadas.add(habilidadeExistente.get());
                } else {
                    Habilidade novaHabilidade = new Habilidade();
                    novaHabilidade.setNomeHabilidade(nomeTrimado);
                    save(novaHabilidade);
                    habilidadesAssociadas.add(novaHabilidade);
                }

            }
        }
        return habilidadesAssociadas;
    }

    public Optional<Habilidade> findByNomeIgnoreCase(String nome) {
        return repository.findByNomeHabilidadeIgnoreCase(nome);
    }
}
