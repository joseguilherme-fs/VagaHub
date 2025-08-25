package br.edu.ifpb.vagahub.controller;

import br.edu.ifpb.vagahub.model.Processo;
import br.edu.ifpb.vagahub.model.Usuario;
import br.edu.ifpb.vagahub.services.EmailService;
import br.edu.ifpb.vagahub.services.ProcessoService;
import br.edu.ifpb.vagahub.services.UsuarioService;
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
    private UsuarioService usuarioService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ProcessoService processoService;

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
        ModelAndView mv = new ModelAndView("/usuarios/processos-finalizados");
        List<Processo> processosFinalizados = processoService.findProcessosFinalizados();
        mv.addObject("processos", processosFinalizados);
        return mv;
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
    public String atualizarUsuario(@PathVariable Long id, @RequestParam String nomeCompleto, @RequestParam String telefone, @RequestParam String linkedin, @RequestParam String areaAtuacao, RedirectAttributes ra, HttpSession session) {
        Usuario atualizado = usuarioService.atualizarDadosPerfil(id, nomeCompleto, telefone, linkedin, areaAtuacao);
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
    public ModelAndView alterarSenha(@RequestParam String email, @RequestParam String novaSenha) {
        ModelAndView mv = new ModelAndView("usuarios/alterar-senha");

        if (novaSenha.length() < 6) {
            mv.addObject("erro", "A senha deve ter pelo menos 6 caracteres.");
            mv.addObject("step", 2);
            return mv;
        }

        Usuario atualizado = usuarioService.atualizarSenhaPorEmail(email, novaSenha);
        if (atualizado != null) {
            mv.setViewName("redirect:/login");
        } else {
            mv.addObject("erro", "Não foi possível alterar a senha. Verifique o email.");
            mv.addObject("step", 2);
        }

        return mv;
    }

    @GetMapping("/recuperar/enviar-codigo")
    @ResponseBody
    public String enviarCodigo(@RequestParam String email) {
        if (!usuarioService.emailExiste(email)) {
            return "Email não cadastrado ❌";
        }

        String codigo = String.format("%04d", new Random().nextInt(10000));
        codigosRecuperacao.put(email, codigo);

        String assunto = "Código de recuperação de senha";
        String mensagem = "Seu código de verificação é: " + codigo;

        return emailService.enviarEmailTexto(email, assunto, mensagem);
    }

    @PostMapping("/recuperar/verificar-codigo")
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