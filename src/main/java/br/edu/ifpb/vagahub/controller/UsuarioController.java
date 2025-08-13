package br.edu.ifpb.vagahub.controller;

import br.edu.ifpb.vagahub.model.Usuario;
import br.edu.ifpb.vagahub.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/perfil")
    public ModelAndView exibirPerfil() {
        return new ModelAndView("/usuarios/perfil");
    }

    @GetMapping("/editar-perfil")
    public ModelAndView exibirEditarPerfil() {
        return new ModelAndView("/usuarios/editar-perfil");
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

    @PostMapping("/usuario/atualizar/{id}")
    public String atualizarUsuario(@PathVariable Long id, @RequestParam String nomeCompleto, @RequestParam String email, RedirectAttributes ra, HttpSession session) {

        Usuario atualizado = usuarioService.atualizarNomeEmail(id, nomeCompleto, email);

        if (atualizado != null) {
            session.setAttribute("usuarioLogado", atualizado);
            ra.addFlashAttribute("mensagemSucesso", "Dados atualizados com sucesso!");
        } else {
            ra.addFlashAttribute("mensagemErro", "Não foi possível atualizar os dados.");
        }

        return "redirect:/perfil";
    }

    @GetMapping("/alterar-senha")
    public ModelAndView alterarSenha() {
        return new ModelAndView("/usuarios/alterar-senha");
    }

    @PostMapping("/recuperar/alterar-senha")
    public String alterarSenha(@RequestParam String email, @RequestParam String novaSenha, RedirectAttributes ra) {
        Usuario atualizado = usuarioService.atualizarSenhaPorEmail(email, novaSenha);

        if (atualizado != null) {
            ra.addFlashAttribute("mensagemSucesso", "Senha alterada com sucesso!");
            return "redirect:/login";
        } else {
            ra.addFlashAttribute("mensagemErro", "Não foi possível alterar a senha. Verifique o email.");
            return "redirect:/recuperar/alterar-senha";
        }
    }

}
