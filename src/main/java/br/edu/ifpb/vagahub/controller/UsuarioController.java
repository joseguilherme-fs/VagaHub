package br.edu.ifpb.vagahub.controller;

import br.edu.ifpb.vagahub.model.Processo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UsuarioController {

    @GetMapping("/perfil")
    public ModelAndView exibirPerfil() {
        return new ModelAndView("/usuarios/perfil");
    }
}
