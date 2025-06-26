package br.edu.ifpb.vagahub.services;
import br.edu.ifpb.vagahub.model.Usuario;
import br.edu.ifpb.vagahub.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Usuario salvar(Usuario usuario) {
        String senhaCriptografada = encoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);
        return usuarioRepository.save(usuario);
    }

    public boolean autenticar(String nomeUsuario, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNomeUsuario(nomeUsuario);
        if (usuarioOpt.isPresent()) {
            return new BCryptPasswordEncoder().matches(senha, usuarioOpt.get().getSenha());
        }
        return false;
    }

    public Optional<Usuario> buscarPorNomeUsuario(String nomeUsuario) {
        return usuarioRepository.findByNomeUsuario(nomeUsuario);
    }

    public boolean verificarSenha(String senhaDigitada, String senhaCriptografada) {
        return new BCryptPasswordEncoder().matches(senhaDigitada, senhaCriptografada);
    }

    // Novo método: Buscar por ID
    public Usuario buscarPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElse(null); // Retorna null se o usuário não for encontrado
    }

    public Usuario excluir(Long idUsuario) {
        Optional<Usuario> u = usuarioRepository.findById(idUsuario);
        if (u.isPresent()) {
            Usuario usuario = u.get();
            usuarioRepository.delete(usuario);
            return usuario;
        } else {
            return null;
        }
    }
}
