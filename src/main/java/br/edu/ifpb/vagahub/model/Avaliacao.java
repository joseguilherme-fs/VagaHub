package br.edu.ifpb.vagahub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "avaliacoes")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A avaliação geral é obrigatória")
    @Min(1) @Max(5)
    private Integer avaliacaoGeral;

    private String comentarios;

    private String recomendacao;

    private LocalDateTime dataAvaliacao;

    @ManyToOne
    @JoinColumn(name = "id_processo_fk", nullable = false)
    private Processo processo;

    @ManyToOne
    @JoinColumn(name = "id_usuario_fk", nullable = false)
    private Usuario usuario;


    @PrePersist
    public void prePersist() {
        this.dataAvaliacao = LocalDateTime.now();
    }
}
