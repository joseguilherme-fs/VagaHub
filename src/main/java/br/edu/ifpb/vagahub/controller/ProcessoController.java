package br.edu.ifpb.vagahub.controller;

import br.edu.ifpb.vagahub.model.Lembrete;
import br.edu.ifpb.vagahub.model.Processo;
import br.edu.ifpb.vagahub.model.Usuario;
import br.edu.ifpb.vagahub.repository.EmpresaRepository;
import br.edu.ifpb.vagahub.repository.HabilidadeRepository;
import br.edu.ifpb.vagahub.services.AvaliacaoService;
import br.edu.ifpb.vagahub.services.LembreteService;
import br.edu.ifpb.vagahub.services.ProcessoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private AvaliacaoService avaliacaoService;

    @Autowired
    private LembreteService lembreteService;

    @GetMapping("/listar")
    public ModelAndView listarTodos(ModelAndView mv) {
        List<Processo> processos = processoService.findAll();
        mv.addObject("processos", processos);
        mv.addObject("modoEdicao", false);
        mv.setViewName("processos/listar");
        return mv;
    }

    @GetMapping("/criar")
    public ModelAndView exibirFormulario() {
        ModelAndView mv = new ModelAndView("processos/formulario");

        Processo processo = new Processo();
        for (int i = 0; i < 5; i++) {
            processo.getLembretes().add(new Lembrete());
        }

        mv.addObject("processo", processo);
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
            mv.setViewName("redirect:formulario");
        }
        return mv;
    }

    @PostMapping
    public String criar(
            @ModelAttribute Processo processo,
            @RequestParam("horarioLembrete") LocalTime horario,
            @RequestParam("diaDaSemana") String diaDaSemana,
            @RequestParam("frequenciaLembretes") String frequenciaLembretes,
            @RequestParam("campoEmpresa") String campoEmpresa,
            @RequestParam("campoHabilidades") String campoHabilidades,
            HttpSession session
    ) throws ParseException {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            throw new RuntimeException("Nenhum usuário logado na sessão.");
        }
        processo.setUsuario(usuarioLogado);

        processoService.criar(processo, campoEmpresa, campoHabilidades);
        lembreteService.formatarLembretes(processo, horario, diaDaSemana, frequenciaLembretes);
        lembreteService.marcarLembretes(processo.getLembretes(), usuarioLogado.getEmail(), usuarioLogado.getNomeCompleto(), processo.getTitulo());

        return "redirect:/processos/listar";
    }

    @GetMapping("/editar/{id}")
    public ModelAndView editar(@PathVariable Long id, ModelAndView mv) {
        Processo processo = processoService.findById(id);
        if (processo != null) {
            mv.addObject("processo", processo);
        } else {
            mv.setViewName("redirect:formulario");
        }
        mv.setViewName("processos/card-edicao");
        return mv;
    }

    // Método atualizado para incluir data de finalização
    @PostMapping("/atualizar/{id}")
    public String atualizar(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam String titulo,
            @RequestParam String descricao,
            @RequestParam String tipoContratacao,
            @RequestParam String formaCandidatura,
            @RequestParam String modeloAtuacao,
            @RequestParam String areaAtuacao,
            RedirectAttributes redirectAttributes) {

        Processo processo = processoService.findById(id);
        if (processo != null) {
            // Verifica se o status mudou para "Finalizado"
            if ("Finalizado".equals(status) && !"Finalizado".equals(processo.getStatus())) {
                processo.setDataFinalizacao(LocalDateTime.now());
            }

            processo.setStatus(status);
            processo.setTitulo(titulo);
            processo.setDescricao(descricao);
            processo.setTipoContratacao(tipoContratacao);
            processo.setFormaCandidatura(formaCandidatura);
            processo.setModeloDeAtuacao(modeloAtuacao);
            processo.setAreaAtuacao(areaAtuacao);
            processoService.save(processo);

            redirectAttributes.addFlashAttribute("mensagemSucesso", "Alterações salvas com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao atualizar o processo.");
        }
        return "redirect:/processos/listar";
    }

    // Novo método para atualizar apenas o status via AJAX
    @PostMapping("/atualizar-status/{id}")
    @ResponseBody
    public String atualizarStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Processo processo = processoService.findById(id);
            if (processo != null) {
                // Se está mudando para "Finalizado", define a data
                if ("Finalizado".equals(status) && !"Finalizado".equals(processo.getStatus())) {
                    processo.setDataFinalizacao(LocalDateTime.now());
                }
                processo.setStatus(status);
                processoService.save(processo);
                return "success";
            }
            return "error";
        } catch (Exception e) {
            return "error";
        }
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        Processo p = processoService.deleteById(id);
        if (p != null) {
            ra.addFlashAttribute("mensagemSucesso", "Processo excluído com sucesso!");
        } else {
            ra.addFlashAttribute("mensagemErro", "Erro ao excluir o processo.");
        }
        return "redirect:/processos/listar";
    }

    // Exibir formulário de avaliação
    @GetMapping("/avaliar/{id}")
    public ModelAndView avaliar(@PathVariable Long id, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        Processo processo = processoService.findById(id);
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (processo != null && usuario != null) {
            mv.addObject("processo", processo);
            mv.setViewName("processos/avaliar");
        } else {
            mv.setViewName("redirect:/processos-finalizados");
        }
        return mv;
    }



    // Processar avaliação
    @PostMapping("/avaliar/{id}")
    public String processarAvaliacao(@PathVariable Long id,
                                     @RequestParam("qualidadeCandidatos") Integer avaliacaoGeral,
                                     @RequestParam(required = false) String observacoesAvaliacao,
                                     @RequestParam(required = false) String recomendacoes,

                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {

        Processo processo = processoService.findById(id);
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (processo == null || usuario == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao processar avaliação.");
            return "redirect:/processos-finalizados";
        }

        try {
            processoService.save(processo);
            avaliacaoService.criarAvaliacao(processo, usuario, avaliacaoGeral, observacoesAvaliacao, recomendacoes);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Avaliação enviada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar avaliação.");
        }

        return "redirect:/processos-finalizados";
    }

}



