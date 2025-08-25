package br.edu.ifpb.vagahub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "processos_seletivos")

public class Processo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String status = "Em Andamento";

    @Column(nullable = false)
    private String descricao;

//    private String linkVaga;

    @Column(nullable = false)
    private String areaAtuacao;

    @Column(nullable = false)
    private String tipoContratacao;

    @Column(nullable = false)
    private String modeloDeAtuacao;

    @Column(nullable = false)
    private String formaCandidatura;

    private String estadoVaga;

    private String cidadeVaga;

    // Campo para armazenar a data de finalização
    private LocalDateTime dataFinalizacao;


    @ManyToOne
    @JoinColumn(name = "id_empresa_fk", nullable = false)
    private Empresa empresa;

    @ManyToMany
    @JoinTable(
            name = "processo_habilidade",
            joinColumns = @JoinColumn(name = "id_processo_fk"),
            inverseJoinColumns = @JoinColumn(name = "id_habilidade_fk")
    )
    private List<Habilidade> habilidades;

    // Método auxiliar para obter data de finalização formatada
    public String getDataFinalizacaoFormatada() {
        if (dataFinalizacao != null) {
            return dataFinalizacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        return "Não finalizado";
    }

    // Método que é chamado antes de salvar para atualizar a data de finalização
    @PreUpdate
    public void preUpdate() {
        if ("Finalizado".equals(this.status) && this.dataFinalizacao == null) {
            this.dataFinalizacao = LocalDateTime.now();
        }
    }


}