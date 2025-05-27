package br.edu.ifpb.vagahub.controller;

import br.edu.ifpb.vagahub.model.Processo;
import br.edu.ifpb.vagahub.repository.EmpresaRepository;
import br.edu.ifpb.vagahub.repository.HabilidadeRepository;
import br.edu.ifpb.vagahub.services.ProcessoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/processos")
public class ProcessoController {

    @Autowired
    private ProcessoService processoService;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private HabilidadeRepository habilidadeRepository;

    @GetMapping
    public String listarTodos(Model model) {
        List<Processo> processos = processoService.findAll();
        model.addAttribute("processos", processos);
        return "processos/lista";
    }

    @GetMapping("/criar")
    public ModelAndView exibirFormulario() {
        ModelAndView mv = new ModelAndView("processos/formulario");
        mv.addObject("processo", new Processo());
        mv.addObject("empresas", empresaRepository.findAll());
        mv.addObject("habilidades", habilidadeRepository.findAll());
        return mv;
    }

    @GetMapping("/{id}")
    public ModelAndView buscarPorId(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView("processos/detalhes");
        Processo processo = processoService.findById(id);
        if (processo != null) {
            mv.addObject("processo", processo);
        } else {
            mv.setViewName("redirect:/processos");
        }
        return mv;
    }


    @PostMapping
    public String criar(
            @ModelAttribute Processo processo,
            @RequestParam("nomeEmpresa") String nomeEmpresa,
            @RequestParam("nomesHabilidades") String nomesHabilidades
    ) {

        processoService.criar(processo, nomeEmpresa, nomesHabilidades);

        return "redirect:/processos";
    }


    @GetMapping("/editar/{id}")
    public ModelAndView editar(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView("processos/formulario");
       Processo processo = processoService.findById(id);
        if (processo != null) {
            mv.addObject("processo", processo);
            mv.addObject("empresas", empresaRepository.findAll());
            mv.addObject("habilidades", habilidadeRepository.findAll());
        } else {
            mv.setViewName("redirect:/processos");
        }
        return mv;
    }

    @PostMapping("/atualizar")
    public String atualizar(@ModelAttribute Processo processoAtualizado) {
        processoService.save(processoAtualizado);
        return "redirect:/processos";
    }

    @GetMapping("/excluir/{id}")
    public String deletar(@PathVariable Long id) {
        processoService.deleteById(id);
        return "redirect:/processos";
    }
}
