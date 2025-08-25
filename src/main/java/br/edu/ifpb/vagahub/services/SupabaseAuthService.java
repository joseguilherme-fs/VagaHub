package br.edu.ifpb.vagahub.services;

import br.edu.ifpb.vagahub.model.Usuario;
import br.edu.ifpb.vagahub.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class SupabaseAuthService {

    private final WebClient webClient;
    private final String anonKey;
    private final String serviceKey;
    private final UsuarioRepository usuarioRepository;

    public SupabaseAuthService(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.anon-key}") String anonKey,
            @Value("${supabase.service-key}") String serviceKey,
            UsuarioRepository usuarioRepository
    ) {
        this.anonKey = anonKey;
        this.serviceKey = serviceKey;
        this.usuarioRepository = usuarioRepository;
        this.webClient = WebClient.builder()
                .baseUrl(supabaseUrl + "/auth/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    // Realiza cadastro no Supabase Auth:

    public String signUp(String email, String password) {
        var body = """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);

        var response = webClient.post()
                .uri("/signup")
                .header("apikey", anonKey)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + anonKey)
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    String lower = errorBody == null ? "" : errorBody.toLowerCase();
                                    if (lower.contains("already registered")
                                            || lower.contains("user already registered")
                                            || lower.contains("already exists")) {
                                        return Mono.error(new IllegalArgumentException("Esse e-mail já está cadastrado!"));
                                    }
                                    return Mono.error(new IllegalStateException("Erro no signup Supabase: " + errorBody));
                                })
                )
                .onStatus(status -> status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new IllegalStateException("Erro no signup Supabase: " + errorBody)))
                )
                .bodyToMono(SignUpResponse.class)
                .onErrorResume(IllegalArgumentException.class, e -> Mono.error(e))
                .onErrorResume(e -> Mono.empty())
                .block();

        if (response != null && response.user != null && response.user.id != null && !response.user.id.isBlank()) {
            return response.user.id;
        }

        return null;
    }

    // Faz login, retorna access_token se sucesso:
    public String signIn(String email, String password) {
        var body = """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);

        var response = webClient.post()
                .uri("/token?grant_type=password")
                .header("apikey", anonKey)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + anonKey)
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new IllegalStateException("Erro no sign-in Supabase: " + errorBody)))
                )
                .bodyToMono(SignInResponse.class)
                .onErrorResume(e -> Mono.empty())
                .block();

        return response != null ? response.access_token : null;
    }

    public boolean updatePasswordByEmail(String email, String newPassword) {
        var user = getUserByEmail(email);
        if (user == null || user.id == null || user.id.isBlank()) return false;

        var body = """
                {
                  "password": "%s"
                }
                """.formatted(newPassword);

        var updated = webClient.put()
                .uri("/admin/users/" + user.id)
                .header("apikey", serviceKey)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceKey)
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new IllegalStateException("Erro ao atualizar senha (admin): " + errorBody)))
                )
                .bodyToMono(Object.class)
                .onErrorResume(e -> Mono.empty())
                .block();

        return updated != null;
    }

    // Exclui usuário no Supabase Auth. Tenta por ID; se não houver, tenta buscar por e-mail.
    public boolean deleteUser(String supabaseUserId, String email) {
        String targetId = supabaseUserId;
        if (targetId == null || targetId.isBlank()) {
            if (email == null || email.isBlank()) {
                return false;
            }
            SupabaseUser su = getUserByEmail(email);
            targetId = (su != null) ? su.id : null;
            if (targetId == null || targetId.isBlank()) {
                return false;
            }
        }

        webClient.delete()
                .uri("/admin/users/" + targetId)
                .header("apikey", serviceKey)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceKey)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new IllegalStateException("Erro ao excluir usuário no Supabase: " + errorBody)))
                )
                .bodyToMono(Void.class)
                .onErrorResume(e -> Mono.empty())
                .block();

        return true;
    }

    private SupabaseUser getUserByEmail(String email) {
        var list = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/admin/users")
                        .queryParam("email", email)
                        .build()
                )
                .header("apikey", serviceKey)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceKey)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new IllegalStateException("Erro ao listar usuários (admin): " + errorBody)))
                )
                .bodyToMono(UserListResponse.class)
                .onErrorResume(e -> Mono.empty())
                .block();

        if (list == null || list.users == null || list.users.length == 0) return null;
        return list.users[0];
    }

    public boolean existsUserByEmail(String email) {
        SupabaseUser su = getUserByEmail(email);
        return su != null && su.id != null && !su.id.isBlank();
    }

    public Usuario findOrCreateLocalProfileByEmail(String email) {
        Optional<Usuario> opt = usuarioRepository.findByEmail(email);
        if (opt.isPresent()) return opt.get();

        SupabaseUser su = getUserByEmail(email);
        String supabaseUserId = su != null ? su.id : null;

        Usuario novo = Usuario.builder()
                .nomeCompleto("")
                .nomeUsuario(email.split("@")[0])
                .email(email)
                .telefone(null)
                .linkedin(null)
                .areaAtuacao("")
                .supabaseUserId(supabaseUserId)
                .build();
        return usuarioRepository.save(novo);
    }

    // Verifica se o e-mail já foi confirmado no Supabase Auth (GoTrue)
    public boolean isEmailConfirmed(String email) {
        SupabaseUser su = getUserByEmail(email);
        if (su == null) return false;
        if (su.email_confirmed_at != null && !su.email_confirmed_at.isBlank()) return true;
        if (su.confirmed_at != null && !su.confirmed_at.isBlank()) return true;
        if (Boolean.TRUE.equals(su.email_confirmed)) return true;
        return false;
    }

    // DTOs
    private static class SignUpResponse {
        public SupabaseUser user;
    }
    private static class SignInResponse {
        public String access_token;
        public String token_type;
        public Long expires_in;
        public String refresh_token;
        public SupabaseUser user;
    }
    private static class UserListResponse {
        public SupabaseUser[] users;
    }
    private static class SupabaseUser {
        public String id;
        public String email;
        public String email_confirmed_at;
        public String confirmed_at;
        public Boolean email_confirmed;
    }
}