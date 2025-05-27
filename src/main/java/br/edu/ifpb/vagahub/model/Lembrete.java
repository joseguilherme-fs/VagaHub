package br.edu.ifpb.vagahub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lembretes")

public class Lembrete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLembrete;

    @Column(nullable = false)
    private String tipoLembrete;

    @Column(nullable = false)
    private LocalTime horarioLembrete;

    @OneToOne
    @JoinColumn(name = "id_processo_fk", nullable = false, unique = true)
    private Processo processoSeletivo;
}
