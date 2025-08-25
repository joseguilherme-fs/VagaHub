package br.edu.ifpb.vagahub.repository;

import br.edu.ifpb.vagahub.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNomeUsuario(String nomeUsuario);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findBySupabaseUserId(String supabaseUserId);

}

