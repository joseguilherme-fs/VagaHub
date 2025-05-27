//package br.edu.ifpb.vagahub.controller;
//
//import br.edu.ifpb.vagahub.model.Empresa;
//import br.edu.ifpb.vagahub.services.EmpresaService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.ModelAndView;
//
//import java.util.List;
//import java.util.Optional;
//
//@Controller
//@RequestMapping("/empresas")
//public class EmpresaController {
//
//    @Autowired
//    private EmpresaService empresaService;
//
//    @GetMapping
//    public String listarTodas(Model model) {
//        List<Empresa> empresas = empresaService.findAll();
//        model.addAttribute("empresas", empresas);
//        return "empresas/lista";
//    }
//
//    @GetMapping("/{id}")
//    public ModelAndView buscarPorId(@PathVariable Long id) {
//        ModelAndView mv = new ModelAndView("empresas/detalhes");
//       Empresa empresa = empresaService.findById(id);
//        if (empresa !=null) {
//            mv.addObject("empresa", empresa.get());
//        } else {
//            mv.setViewName("redirect:/empresas");
//        }
//        return mv;
//    }
//
////    @GetMapping("/criar")
////    public ModelAndView exibirFormulario() {
////        ModelAndView mv = new ModelAndView("empresas/formulario");
////        mv.addObject("empresa", new Empresa());
////        return mv;
////    }
//
//    @PostMapping
//    public String criar(@ModelAttribute Empresa empresa) {
//        empresaService.save(empresa);
//        return "redirect:/empresas";
//    }
//
//    @GetMapping("/editar/{id}")
//    public ModelAndView editar(@PathVariable Long id) {
//        ModelAndView mv = new ModelAndView("empresas/formulario");
//        Empresa empresa = empresaService.findById(id);
//        if (empresa != null) {
//            mv.addObject("empresa", empresa.get());
//        } else {
//            mv.setViewName("redirect:/empresas");
//        }
//        return mv;
//    }
//
//    @PostMapping("/atualizar")
//    public String atualizar(@ModelAttribute Empresa empresaAtualizada) {
//        empresaService.save(empresaAtualizada);
//        return "redirect:/empresas";
//    }
//
//    @GetMapping("/excluir/{id}")
//    public String deletar(@PathVariable Long id) {
//        empresaService.deleteById(id);
//        return "redirect:/empresas";
//    }
//}
