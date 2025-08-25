package br.edu.ifpb.vagahub.controller;

import br.edu.ifpb.vagahub.model.Usuario;
import br.edu.ifpb.vagahub.services.SupabaseAuthService;
import br.edu.ifpb.vagahub.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SupabaseAuthService supabaseAuthService;

    @GetMapping("/login")
    public ModelAndView exibirFormularioLogin() {
        return new ModelAndView("usuarios/login")
                .addObject("erro", false)
                .addObject("erroConfirmacao", false);
    }

    @PostMapping("/login")
    public ModelAndView realizarLogin(@RequestParam String email,
                                      @RequestParam String senha,
                                      HttpSession session) {
        String emailTrimmed = email == null ? null : email.trim();

        // Tenta autenticar primeiro
        String accessToken = supabaseAuthService.signIn(emailTrimmed, senha);
        if (accessToken != null) {
            Usuario usuarioSessao = supabaseAuthService.findOrCreateLocalProfileByEmail(emailTrimmed);
            session.setAttribute("usuarioLogado", usuarioSessao);
            session.setAttribute("supabaseAccessToken", accessToken);
            return new ModelAndView("redirect:/processos/listar");
        }

        // Se falhou o login, verifica se o usuário existe e ainda não confirmou o e-mail
        if (supabaseAuthService.existsUserByEmail(emailTrimmed) && !supabaseAuthService.isEmailConfirmed(emailTrimmed)) {
            return new ModelAndView("usuarios/login")
                    .addObject("erroConfirmacao", true)
                    .addObject("erro", false);
        }

        // Caso contrário, credenciais inválidas
        return new ModelAndView("usuarios/login")
                .addObject("erro", true)
                .addObject("erroConfirmacao", false);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}