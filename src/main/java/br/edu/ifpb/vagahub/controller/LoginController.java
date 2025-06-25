package br.edu.ifpb.vagahub.controller;

import br.edu.ifpb.vagahub.model.Usuario;
import br.edu.ifpb.vagahub.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public ModelAndView exibirFormularioLogin() {
        return new ModelAndView("usuarios/login").addObject("erro", false);
    }

    @PostMapping("/login")
    public ModelAndView realizarLogin(@RequestParam String nomeUsuario,
                                      @RequestParam String senha,
                                      HttpSession session) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorNomeUsuario(nomeUsuario);

        if (usuarioOpt.isPresent() && usuarioService.verificarSenha(senha, usuarioOpt.get().getSenha())) {
            session.setAttribute("usuarioLogado", usuarioOpt.get());
            return new ModelAndView("redirect:/processos/listar");
        }

        return new ModelAndView("usuarios/login").addObject("erro", true);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }


}
