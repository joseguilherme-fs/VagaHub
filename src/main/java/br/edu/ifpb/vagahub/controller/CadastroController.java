package br.edu.ifpb.vagahub.controller;

import br.edu.ifpb.vagahub.model.Usuario;
import br.edu.ifpb.vagahub.services.UsuarioService;
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

    @PostMapping("/usuarios")
    public ModelAndView registrar(@Valid @ModelAttribute Usuario usuario, BindingResult result, RedirectAttributes ra) {
        String senha = usuario.getSenha();
        if (senha == null || senha.isBlank() || senha.length() < 6) {
            result.rejectValue("senha", "senha.invalida", "A senha deve ter pelo menos 6 caracteres.");
        }

        String email = usuario.getEmail();
        if (email != null && !email.isBlank() && usuarioService.emailExiste(email)) {
            result.rejectValue("email", "email.duplicado", "Esse e-mail já está cadastrado!");
        }

        if (result.hasErrors()) {
            ModelAndView mv = new ModelAndView("usuarios/formulario");
            mv.addObject("usuario", usuario);
            return mv;
        }

        try {
            usuarioService.salvar(usuario);
            ra.addFlashAttribute("mensagemSucesso",
                    "Um e-mail foi enviado para confirmar o cadastro. Verifique na Caixa de Entrada ou Spam.");
            return new ModelAndView("redirect:/login");
        } catch (IllegalArgumentException ex) {
            if ("Esse e-mail já está cadastrado!".equals(ex.getMessage())) {
                result.rejectValue("email", "email.duplicado", ex.getMessage());
                ModelAndView mv = new ModelAndView("usuarios/formulario");
                mv.addObject("usuario", usuario);
                return mv;
            }
            ModelAndView mv = new ModelAndView("usuarios/formulario");
            mv.addObject("usuario", usuario);
            mv.addObject("erroCadastro", ex.getMessage());
            return mv;
        } catch (IllegalStateException ex) {
            ModelAndView mv = new ModelAndView("usuarios/formulario");
            mv.addObject("usuario", usuario);
            mv.addObject("erroCadastro", ex.getMessage());
            return mv;
        }
    }

    @GetMapping("/usuarios/{idUsuario}/editar")
    public ModelAndView exibirFormularioEdicao(@PathVariable Long idUsuario) {
        Usuario usuario = usuarioService.buscarPorId(idUsuario);
        if (usuario == null) {
            return new ModelAndView("redirect:/erro");
        }
        ModelAndView mv = new ModelAndView("usuarios/formulario");
        mv.addObject("usuario", usuario);
        return mv;
    }

    @PostMapping("/usuarios/{idUsuario}/editar")
    public ModelAndView editar(@PathVariable Long idUsuario, @Valid @ModelAttribute Usuario usuario, BindingResult result) {
        if (result.hasErrors()) {
            ModelAndView mv = new ModelAndView("usuarios/formulario");
            mv.addObject("usuario", usuario);
            return mv;
        }
        usuario.setIdUsuario(idUsuario);
        try {
            usuarioService.salvar(usuario);
            return new ModelAndView("redirect:/usuarios");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            ModelAndView mv = new ModelAndView("usuarios/formulario");
            mv.addObject("usuario", usuario);
            mv.addObject("erroCadastro", ex.getMessage());
            return mv;
        }
    }
}