package br.edu.ifpb.vagahub.services;

import br.edu.ifpb.vagahub.model.Usuario;
import br.edu.ifpb.vagahub.repository.UsuarioRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
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

    public SupabaseUser getUser(String accessToken) {
        return webClient.get()
                .uri("/user")
                .header("apikey", anonKey)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> Mono.empty())
                .bodyToMono(SupabaseUser.class)
                .block();
    }

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

    // NOVO MÉTODO - Mais robusto, usa o ID do usuário
    public void updatePasswordByUserId(String userId, String newPassword) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("O ID do usuário do Supabase não pode ser nulo.");
        }
        if (newPassword == null || newPassword.isBlank()) {
            return; // Não faz nada se a senha for vazia
        }

        var body = String.format("{\"password\": \"%s\"}", newPassword);

        webClient.put()
                .uri("/admin/users/" + userId)
                .header("apikey", serviceKey)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceKey)
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new IllegalStateException("API do Supabase retornou um erro ao tentar atualizar a senha: " + errorBody)))
                )
                .bodyToMono(Void.class)
                .block(); // .block() irá propagar a exceção do onStatus, se ocorrer
    }

    // Método antigo modificado para usar o novo
    public boolean updatePasswordByEmail(String email, String newPassword) {
        try {
            var user = getUserByEmail(email);
            if (user == null || user.id == null || user.id.isBlank()) {
                return false;
            }
            updatePasswordByUserId(user.id, newPassword);
            return true;
        } catch (Exception e) {
            // Opcional: logar o erro
            // logger.error("Falha ao atualizar senha por e-mail", e);
            return false;
        }
    }

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
                        .queryParam("filter", "email = \"" + email + "\"")
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

    public Usuario findOrCreateLocalProfile(SupabaseUser supabaseUser) {
        Optional<Usuario> opt = usuarioRepository.findByEmail(supabaseUser.email);
        if (opt.isPresent()) {
            Usuario u = opt.get();
            if (u.getSupabaseUserId() == null || u.getSupabaseUserId().isBlank()) {
                u.setSupabaseUserId(supabaseUser.id);
                return usuarioRepository.save(u);
            }
            return u;
        }

        Map<String, Object> metadata = supabaseUser.user_metadata;
        String nomeCompleto = (String) metadata.getOrDefault("full_name", "");
        String telefone = (String) metadata.getOrDefault("phone", null);

        Usuario novo = Usuario.builder()
                .nomeCompleto(nomeCompleto)
                .email(supabaseUser.email)
                .telefone(telefone)
                .supabaseUserId(supabaseUser.id)
                .nomeUsuario("")
                .areaAtuacao("")
                .build();
        return usuarioRepository.save(novo);
    }

    public Usuario findOrCreateLocalProfileByEmail(String email) {
        Optional<Usuario> opt = usuarioRepository.findByEmail(email);
        if (opt.isPresent()) return opt.get();

        SupabaseUser su = getUserByEmail(email);
        String supabaseUserId = su != null ? su.id : null;

        Usuario novo = Usuario.builder()
                .nomeCompleto("")
                .nomeUsuario("")
                .email(email)
                .telefone(null)
                .linkedin(null)
                .areaAtuacao("")
                .supabaseUserId(supabaseUserId)
                .build();
        return usuarioRepository.save(novo);
    }

    public boolean isEmailConfirmed(String email) {
        SupabaseUser su = getUserByEmail(email);
        if (su == null) return false;
        if (su.email_confirmed_at != null && !su.email_confirmed_at.isBlank()) return true;
        if (su.confirmed_at != null && !su.confirmed_at.isBlank()) return true;
        return Boolean.TRUE.equals(su.email_confirmed);
    }

    // DTOs
    private static class SignUpResponse {
        public SupabaseUser user;
    }
    private static class SignInResponse {
        public String access_token;
        public SupabaseUser user;
    }
    private static class UserListResponse {
        public SupabaseUser[] users;
    }
    public static class SupabaseUser {
        public String id;
        public String email;
        @JsonProperty("email_confirmed_at")
        public String email_confirmed_at;
        @JsonProperty("confirmed_at")
        public String confirmed_at;
        @JsonProperty("user_metadata")
        public Map<String, Object> user_metadata;
        public Boolean email_confirmed;
    }
}