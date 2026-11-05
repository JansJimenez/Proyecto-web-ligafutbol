package pe.ligacopaperu.torneo;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class CompetitionController {
    private final CompetitionService competitions;

    public CompetitionController(CompetitionService competitions) {
        this.competitions = competitions;
    }

    @GetMapping("/admin/torneos")
    public String list(Model model) {
        model.addAttribute("competitions", competitions.list());
        return "admin/competitions/list";
    }

    @GetMapping("/admin/torneos/nuevo")
    public String createForm(Model model) {
        model.addAttribute("competitionForm", new CompetitionForm());
        model.addAttribute("seasons", competitions.seasons());
        model.addAttribute("action", "/admin/torneos");
        model.addAttribute("title", "Nuevo torneo");
        return "admin/competitions/form";
    }

    @GetMapping("/admin/torneos/{id}/editar")
    public String editForm(@PathVariable long id, Model model) {
        model.addAttribute("competitionForm", competitions.formFor(id));
        model.addAttribute("seasons", competitions.seasons());
        model.addAttribute("action", "/admin/torneos/" + id);
        model.addAttribute("title", "Editar torneo");
        return "admin/competitions/form";
    }

    @PostMapping({"/admin/torneos", "/admin/torneos/{id}"})
    public String save(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute CompetitionForm competitionForm,
            BindingResult errors,
            Principal principal,
            Model model,
            RedirectAttributes redirect) {
        String action = id == null ? "/admin/torneos" : "/admin/torneos/" + id;
        if (errors.hasErrors()) {
            prepareForm(model, id, action);
            return "admin/competitions/form";
        }
        try {
            competitions.save(id, competitionForm, principal.getName());
        } catch (IllegalArgumentException exception) {
            errors.reject("competition.invalid", exception.getMessage());
            prepareForm(model, id, action);
            return "admin/competitions/form";
        }
        redirect.addFlashAttribute("success", "El torneo se guardó correctamente.");
        return "redirect:/admin/torneos";
    }

    private void prepareForm(Model model, Long id, String action) {
        model.addAttribute("seasons", competitions.seasons());
        model.addAttribute("action", action);
        model.addAttribute("title", id == null ? "Nuevo torneo" : "Editar torneo");
    }
}
