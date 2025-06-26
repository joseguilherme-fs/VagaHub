package br.edu.ifpb.vagahub.controller;

import br.edu.ifpb.vagahub.model.Usuario;
import br.edu.ifpb.vagahub.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
    public ModelAndView registrar(@Valid @ModelAttribute Usuario usuario, BindingResult result) {
        if (result.hasErrors()) {
            ModelAndView mv = new ModelAndView("usuarios/formulario");
            mv.addObject("usuario", usuario);
            return mv;
        }
        usuarioService.salvar(usuario);
        return new ModelAndView("redirect:/login");
    }

    // Método para exibir o formulário de edição
    @GetMapping("/usuarios/{idUsuario}/editar")
    public ModelAndView exibirFormularioEdicao(@PathVariable Long idUsuario) {
        Usuario usuario = usuarioService.buscarPorId(idUsuario);
        if (usuario == null) {
            return new ModelAndView("redirect:/erro"); // Redireciona para uma página de erro, se necessário
        }
        ModelAndView mv = new ModelAndView("usuarios/formulario");
        mv.addObject("usuario", usuario);
        return mv;
    }

    // Método para salvar as alterações do usuário
    @PostMapping("/usuarios/{idUsuario}/editar")
    public ModelAndView editar(@PathVariable Long idUsuario, @Valid @ModelAttribute Usuario usuario, BindingResult result) {
        if (result.hasErrors()) {
            ModelAndView mv = new ModelAndView("usuarios/formulario");
            mv.addObject("usuario", usuario);
            return mv;
        }
        usuario.setIdUsuario(idUsuario); // Define o ID do usuário antes de salvar
        usuarioService.salvar(usuario);
        return new ModelAndView("redirect:/usuarios");
    }

}

