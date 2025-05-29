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

    @GetMapping("/listar")
    public ModelAndView listarTodos(ModelAndView mv) {
        List<Processo> processos = processoService.findAll();
        mv.addObject("processos", processos);
        mv.addObject("modoEdicao", false);
        mv.setViewName("/processos/listar");
        return mv;
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
        ModelAndView mv = new ModelAndView("fragments/card");
        Processo processo = processoService.findById(id);
        if (processo != null) {
            mv.addObject("modoEdicao", true);
            mv.addObject("processo", processo);
        } else {
            mv.setViewName("redirect:/formulario");
        }
        return mv;
    }

    @PostMapping
    public String criar(
            @ModelAttribute Processo processo,
            @RequestParam("campoEmpresa") String campoEmpresa,
            @RequestParam("campoHabilidades") String campoHabilidades
    ) {

        processoService.criar(processo, campoEmpresa, campoHabilidades);

        return "redirect:/processos";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Long id, @RequestParam String status, @RequestParam String descricao, @RequestParam String tipoContratacao, @RequestParam String formaCandidatura, @RequestParam String atualizacao, Model model) {
        Processo processo = processoService.findById(id);
        if (processo != null) {
            if (atualizacao.equals("abrirCard")){
                model.addAttribute("modoEdicao", true);
                return "redirect:/processos/{id}";
            } else if (atualizacao.equals("excluirProcesso")){
                processoService.deleteById(id);
                model.addAttribute("modoEdicao", false);
                return "redirect:/processos/listar";
            }
            else {
                processo.setStatus(status);
                processo.setDescricao(descricao);
                processo.setTipoContratacao(tipoContratacao);
                processo.setFormaCandidatura(formaCandidatura);
            }
            processoService.save(processo);
        } else {
            return "redirect:/processos";
        }
        return "redirect:/processos/listar";
    }
}
