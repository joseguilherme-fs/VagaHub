package br.edu.ifpb.vagahub.controller;

import br.edu.ifpb.vagahub.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/perfil")
    public ModelAndView exibirPerfil() {
        return new ModelAndView("usuarios/perfil");
    }

    @PostMapping("/usuario/excluir/{id}")
    public String excluirConta(@PathVariable Long id, RedirectAttributes ra, HttpSession session) {
        if(usuarioService.excluir(id) != null) {
            session.invalidate();
            ra.addFlashAttribute("mensagemSucesso", "Sua conta foi excluída com sucesso. Até breve!");
            return "redirect:/";
        } else {
            ra.addFlashAttribute("mensagemErro", "Não foi possível excluir sua conta.");
            return "redirect:/processos/listar";
        }
    }
}
