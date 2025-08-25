package br.edu.ifpb.vagahub.services;

import br.edu.ifpb.vagahub.model.Usuario;
import br.edu.ifpb.vagahub.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SupabaseAuthService supabaseAuthService;

    public Usuario salvar(Usuario usuario) {
        String email = usuario.getEmail();

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Esse e-mail já está cadastrado!");
        }

        if (usuario.getSupabaseUserId() == null || usuario.getSupabaseUserId().isBlank()) {
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email é obrigatório para registro no Supabase.");
            }
            String supabaseUserId = supabaseAuthService.signUp(email, usuario.getSenha());
            if (supabaseUserId != null && !supabaseUserId.isBlank()) {
                usuario.setSupabaseUserId(supabaseUserId);
            }
        }
        usuario.setSenha(null);
        return usuarioRepository.save(usuario);
    }

    public Usuario completarCadastro(Long id, Usuario dadosNovos) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado em nosso banco de dados."));

        // Atualiza os dados do perfil
        usuarioExistente.setNomeUsuario(dadosNovos.getNomeUsuario());
        usuarioExistente.setTelefone(dadosNovos.getTelefone());
        usuarioExistente.setLinkedin(dadosNovos.getLinkedin());
        usuarioExistente.setAreaAtuacao(dadosNovos.getAreaAtuacao());

        // CORREÇÃO: Chama o método de atualização de senha usando o ID do Supabase
        supabaseAuthService.updatePasswordByUserId(usuarioExistente.getSupabaseUserId(), dadosNovos.getSenha());

        // Salva as alterações no banco de dados local
        return usuarioRepository.save(usuarioExistente);
    }

    public boolean autenticar(String email, String senha) {
        return supabaseAuthService.signIn(email, senha) != null;
    }

    public boolean emailExiste(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    public Optional<Usuario> buscarPorNomeUsuario(String nomeUsuario) {
        return usuarioRepository.findByNomeUsuario(nomeUsuario);
    }

    public Usuario buscarPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario).orElse(null);
    }

    public Usuario excluir(Long idUsuario) {
        Optional<Usuario> u = usuarioRepository.findById(idUsuario);
        if (u.isPresent()) {
            Usuario usuario = u.get();
            try {
                supabaseAuthService.deleteUser(usuario.getSupabaseUserId(), usuario.getEmail());
            } catch (Exception ignored) {
            }
            usuarioRepository.delete(usuario);
            return usuario;
        } else {
            return null;
        }
    }

    public Usuario atualizarDadosPerfil(Long idUsuario, String nomeCompleto, String telefone, String linkedin, String areaAtuacao) {
        Optional<Usuario> u = usuarioRepository.findById(idUsuario);

        if (u.isPresent()) {
            Usuario usuario = u.get();
            usuario.setNomeCompleto(nomeCompleto);
            usuario.setTelefone(telefone);
            usuario.setLinkedin(linkedin);
            usuario.setAreaAtuacao(areaAtuacao);
            usuarioRepository.save(usuario);
            return usuario;
        } else {
            return null;
        }
    }

    public Usuario atualizarSenhaPorEmail(String email, String novaSenha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return null;
        }
        boolean sucesso = supabaseAuthService.updatePasswordByEmail(email, novaSenha);
        return sucesso ? usuarioOpt.get() : null;
    }
}