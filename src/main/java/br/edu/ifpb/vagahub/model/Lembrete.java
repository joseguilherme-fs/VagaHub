package br.edu.ifpb.vagahub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lembretes")

public class Lembrete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLembrete;

    @ManyToOne
    @JoinColumn(name = "id_processo_fk", nullable = false)
    private Processo processoSeletivo;

    private FrequenciaLembretes frequenciaLembretes;

    private LocalTime horarioLembrete;

    private LocalDate dataLembrete;

    private String descricaoData;

    private String diaDaSemana;
}
