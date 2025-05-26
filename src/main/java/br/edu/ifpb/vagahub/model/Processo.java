package br.edu.ifpb.vagahub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Processo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCandidatura;
    private String tituloCandidatura;
    private String descricaoCandidatura;
    private String linkVaga;
    private String areaAtuacao;
    private String tipoContratacao;
    private String modeloDeAtuacao;
    private String formaCandidatura;
    private String estadoVaga;
    private String cidadeVaga;

    @ManyToOne
    @JoinColumn(name = "id_empresa_fk")
    private Empresa empresa;

    @ManyToMany
    @JoinTable(
            name = "processo_habilidade",
            joinColumns = @JoinColumn(name = "id_candidatura_fk"),
            inverseJoinColumns = @JoinColumn(name = "id_habilidade_fk")
    )
    private List<Habilidade> habilidades;
}

