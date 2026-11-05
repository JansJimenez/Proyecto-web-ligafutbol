package pe.ligacopaperu.torneo;

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
public class SeasonController {
    private final SeasonService seasons;

    public SeasonController(SeasonService seasons) {
        this.seasons = seasons;
    }

    @GetMapping("/admin/temporadas")
    public String list(Model model) {
        model.addAttribute("seasons", seasons.list());
        return "admin/seasons/list";
    }

    @GetMapping("/admin/temporadas/nueva")
    public String createForm(Model model) {
        model.addAttribute("seasonForm", new SeasonForm());
        model.addAttribute("action", "/admin/temporadas");
        model.addAttribute("title", "Nueva temporada");
        return "admin/seasons/form";
    }

    @GetMapping("/admin/temporadas/{id}/editar")
    public String editForm(@PathVariable long id, Model model) {
        model.addAttribute("seasonForm", seasons.formFor(id));
        model.addAttribute("action", "/admin/temporadas/" + id);
        model.addAttribute("title", "Editar temporada");
        return "admin/seasons/form";
    }

    @PostMapping({"/admin/temporadas", "/admin/temporadas/{id}"})
    public String save(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute SeasonForm seasonForm,
            BindingResult errors,
            java.security.Principal principal,
            Model model,
            RedirectAttributes redirect) {
        String action = id == null ? "/admin/temporadas" : "/admin/temporadas/" + id;
        if (errors.hasErrors()) {
            model.addAttribute("action", action);
            model.addAttribute("title", id == null ? "Nueva temporada" : "Editar temporada");
            return "admin/seasons/form";
        }
        try {
            seasons.save(id, seasonForm, principal.getName());
        } catch (IllegalArgumentException exception) {
            errors.reject("season.invalid", exception.getMessage());
            model.addAttribute("action", action);
            model.addAttribute("title", id == null ? "Nueva temporada" : "Editar temporada");
            return "admin/seasons/form";
        }
        redirect.addFlashAttribute("success", "La temporada se guardó correctamente.");
        return "redirect:/admin/temporadas";
    }
}
