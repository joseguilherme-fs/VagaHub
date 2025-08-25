package br.edu.ifpb.vagahub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @NotBlank(message = "O nome completo é obrigatório.")
    private String nomeCompleto;

    @NotBlank(message = "O nome de usuário é obrigatório.")
    private String nomeUsuario;

    @Transient
    private String senha;

    @Column(unique = true)
    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "O email deve ser válido.")
    private String email;

    private String telefone;

    @URL(message = "Informe um LinkedIn válido.")
    private String linkedin;

    @NotBlank(message = "A área de atuação é obrigatória.")
    private String areaAtuacao;

    // ID do usuário no Supabase Auth
    private String supabaseUserId;
}