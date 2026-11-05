package pe.ligacopaperu.equipo;

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
public class TeamController {
    private final TeamService teams;

    public TeamController(TeamService teams) {
        this.teams = teams;
    }

    @GetMapping("/equipos")
    public String publicList(Model model) {
        model.addAttribute("teams", teams.publicList());
        return "public/teams";
    }

    @GetMapping("/admin/equipos")
    public String list(Model model) {
        model.addAttribute("teams", teams.list());
        return "admin/teams/list";
    }

    @GetMapping("/admin/equipos/nuevo")
    public String createForm(Model model) {
        model.addAttribute("teamForm", new TeamForm());
        prepareForm(model, null, "/admin/equipos");
        return "admin/teams/form";
    }

    @GetMapping("/admin/equipos/{id}/editar")
    public String editForm(@PathVariable long id, Model model) {
        model.addAttribute("teamForm", teams.formFor(id));
        prepareForm(model, id, "/admin/equipos/" + id);
        return "admin/teams/form";
    }

    @PostMapping({"/admin/equipos", "/admin/equipos/{id}"})
    public String save(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute TeamForm teamForm,
            BindingResult errors,
            Principal principal,
            Model model,
            RedirectAttributes redirect) {
        String action = id == null ? "/admin/equipos" : "/admin/equipos/" + id;
        if (errors.hasErrors()) {
            prepareForm(model, id, action);
            return "admin/teams/form";
        }
        try {
            teams.save(id, teamForm, principal.getName());
        } catch (IllegalArgumentException exception) {
            errors.reject("team.invalid", exception.getMessage());
            prepareForm(model, id, action);
            return "admin/teams/form";
        }
        redirect.addFlashAttribute("success", "El equipo se guardó correctamente.");
        return "redirect:/admin/equipos";
    }

    @PostMapping("/admin/equipos/{id}/desactivar")
    public String deactivate(@PathVariable long id, Principal principal, RedirectAttributes redirect) {
        teams.deactivate(id, principal.getName());
        redirect.addFlashAttribute("success", "El equipo se desactivó y su historial se conservó.");
        return "redirect:/admin/equipos";
    }

    private void prepareForm(Model model, Long id, String action) {
        model.addAttribute("clubs", teams.availableClubs());
        model.addAttribute("competitions", teams.competitions());
        model.addAttribute("statuses", TeamStatus.values());
        model.addAttribute("action", action);
        model.addAttribute("title", id == null ? "Nuevo equipo" : "Editar equipo");
    }
}
