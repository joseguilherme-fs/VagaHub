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
}

