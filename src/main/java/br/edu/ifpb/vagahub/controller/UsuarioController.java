package br.edu.ifpb.vagahub.controller;

import br.edu.ifpb.vagahub.model.Processo;
import br.edu.ifpb.vagahub.model.Usuario;
import br.edu.ifpb.vagahub.services.EmailService;
import br.edu.ifpb.vagahub.services.UsuarioService;
import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    private EmailService emailService;

    private Map<String, String> codigosRecuperacao = new ConcurrentHashMap<>();

    @GetMapping("/perfil")
    public ModelAndView exibirPerfil() {
        return new ModelAndView("/usuarios/perfil");
    }

    @GetMapping("/editar-perfil")
    public ModelAndView exibirEditarPerfil() {
        return new ModelAndView("/usuarios/editar-perfil");
    }

    @GetMapping("/processos-finalizados")
    public ModelAndView exibirProcessosFinalizados() {
        return new ModelAndView("/usuarios/processos-finalizados");
    }

    @GetMapping("/recuperar-senha")
    public ModelAndView exibirRecuperarSenha() {
        return new ModelAndView("/usuarios/recuperar-senha");
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
    public ModelAndView exibirAlterarSenha() {
        return new ModelAndView("usuarios/alterar-senha");
    }


    @PostMapping("/recuperar/alterar-senha")
    public String alterarSenha(@RequestParam String email, @RequestParam String novaSenha, RedirectAttributes ra) {
        Usuario atualizado = usuarioService.atualizarSenhaPorEmail(email, novaSenha);

        if (atualizado != null) {
            ra.addFlashAttribute("mensagemSucesso", "Senha alterada com sucesso!");
            return "redirect:/login";
        } else {
            ra.addFlashAttribute("mensagemErro", "Não foi possível alterar a senha. Verifique o email.");
            return "redirect:/alterar-senha";
        }
    }


    @GetMapping("/recuperar/enviar-codigo")
    @ResponseBody
    public String enviarCodigo(@RequestParam String email) {
        String codigo = String.format("%04d", new Random().nextInt(10000));
        codigosRecuperacao.put(email, codigo);

        String assunto = "Código de recuperação de senha";
        String mensagem = "Seu código de verificação é: " + codigo;

        return emailService.enviarEmailTexto(email, assunto, mensagem);
    }

    @GetMapping("/recuperar/verificar-codigo")
    @ResponseBody
    public String verificarCodigo(@RequestParam String email, @RequestParam String code) {
        String codigoSalvo = codigosRecuperacao.get(email);
        if (codigoSalvo != null && codigoSalvo.equals(code)) {
            return "Código válido";
        } else {
            return "Código inválido";
        }
    }

}
