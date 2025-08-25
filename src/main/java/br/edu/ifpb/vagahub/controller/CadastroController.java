package br.edu.ifpb.vagahub.controller;

import br.edu.ifpb.vagahub.model.Usuario;
import br.edu.ifpb.vagahub.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CadastroController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registrar")
    public ModelAndView exibirFormulario() {
        ModelAndView mv = new ModelAndView("usuarios/formulario");
        mv.addObject("usuario", new Usuario());
        return mv;
    }

    @GetMapping("/registrar/google")
    public ModelAndView exibirFormularioGoogle(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioIncompleto");
        if (usuario == null) {
            return new ModelAndView("redirect:/login");
        }
        ModelAndView mv = new ModelAndView("usuarios/formulario-google");
        mv.addObject("usuario", usuario);
        return mv;
    }

    @PostMapping("/registrar/google")
    public ModelAndView registrarGoogle(@ModelAttribute Usuario usuario, BindingResult result, HttpSession session, RedirectAttributes ra) {
        if (usuario.getNomeUsuario() == null || usuario.getNomeUsuario().isBlank()){
            result.rejectValue("nomeUsuario", "NotBlank", "O nome de usuário é obrigatório.");
        }
        if (usuario.getAreaAtuacao() == null || usuario.getAreaAtuacao().isBlank()){
            result.rejectValue("areaAtuacao", "NotBlank", "A área de atuação é obrigatória.");
        }
        if (usuario.getSenha() != null && !usuario.getSenha().isBlank() && usuario.getSenha().length() < 6) {
            result.rejectValue("senha", "Size", "A senha deve ter pelo menos 6 caracteres.");
        }

        if (result.hasErrors()) {
            return new ModelAndView("usuarios/formulario-google", "usuario", usuario);
        }

        try {
            Usuario usuarioIncompleto = (Usuario) session.getAttribute("usuarioIncompleto");
            Usuario usuarioCompleto = usuarioService.completarCadastro(usuarioIncompleto.getIdUsuario(), usuario);

            session.setAttribute("usuarioLogado", usuarioCompleto);
            session.removeAttribute("usuarioIncompleto");
            ra.addFlashAttribute("mensagemSucesso", "Cadastro finalizado com sucesso!");
            return new ModelAndView("redirect:/processos/listar");
        } catch (Exception ex) {
            ModelAndView mv = new ModelAndView("usuarios/formulario-google");
            mv.addObject("usuario", usuario);
            mv.addObject("erroCadastro", "Ocorreu um erro ao finalizar seu cadastro: " + ex.getMessage());
            return mv;
        }
    }

    @PostMapping("/usuarios")
    public ModelAndView registrar(@Valid @ModelAttribute Usuario usuario, BindingResult result, RedirectAttributes ra) {
        if (usuario.getSenha() == null || usuario.getSenha().isBlank() || usuario.getSenha().length() < 6) {
            result.rejectValue("senha", "senha.invalida", "A senha deve ter pelo menos 6 caracteres.");
        }

        if (usuarioService.emailExiste(usuario.getEmail())) {
            result.rejectValue("email", "email.duplicado", "Esse e-mail já está cadastrado!");
        }

        if (result.hasErrors()) {
            return new ModelAndView("usuarios/formulario", "usuario", usuario);
        }

        try {
            usuarioService.salvar(usuario);
            ra.addFlashAttribute("mensagemSucesso",
                    "Um e-mail foi enviado para confirmar o cadastro. Verifique na Caixa de Entrada ou Spam.");
            return new ModelAndView("redirect:/login");
        } catch (IllegalArgumentException ex) {
            result.rejectValue("email", "email.duplicado", ex.getMessage());
            return new ModelAndView("usuarios/formulario", "usuario", usuario);
        } catch (IllegalStateException ex) {
            ModelAndView mv = new ModelAndView("usuarios/formulario");
            mv.addObject("usuario", usuario);
            mv.addObject("erroCadastro", ex.getMessage());
            return mv;
        }
    }

}