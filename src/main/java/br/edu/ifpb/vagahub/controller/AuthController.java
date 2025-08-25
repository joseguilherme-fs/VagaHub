package br.edu.ifpb.vagahub.controller;

import br.edu.ifpb.vagahub.model.Usuario;
import br.edu.ifpb.vagahub.services.SupabaseAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SupabaseAuthService supabaseAuthService;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon-key}")
    private String supabaseAnonKey;

    @PostMapping("/callback")
    @ResponseBody
    public String handleCallback(@RequestBody Map<String, String> payload, HttpSession session, RedirectAttributes ra) {
        String accessToken = payload.get("accessToken");
        if (accessToken == null || accessToken.isBlank()) {
            ra.addFlashAttribute("mensagemErro", "Falha na autenticação com o Google.");
            return "/login";
        }

        SupabaseAuthService.SupabaseUser supabaseUser = supabaseAuthService.getUser(accessToken);
        if (supabaseUser == null) {
            ra.addFlashAttribute("mensagemErro", "Não foi possível obter os dados do usuário.");
            return "/login";
        }

        Usuario usuario = supabaseAuthService.findOrCreateLocalProfile(supabaseUser);

        if (usuario.getNomeUsuario() == null || usuario.getNomeUsuario().isBlank() ||
                usuario.getAreaAtuacao() == null || usuario.getAreaAtuacao().isBlank()) {

            session.setAttribute("usuarioIncompleto", usuario);
            session.setAttribute("supabaseAccessToken", accessToken);
            return "/registrar/google";
        } else {
            session.setAttribute("usuarioLogado", usuario);
            session.setAttribute("supabaseAccessToken", accessToken);
            return "/processos/listar";
        }
    }

    @GetMapping("/callback")
    public ModelAndView showCallbackPage() {
        return new ModelAndView("auth/callback");
    }
}
