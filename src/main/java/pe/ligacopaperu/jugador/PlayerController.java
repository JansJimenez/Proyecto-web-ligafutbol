package pe.ligacopaperu.jugador;

import java.security.Principal;
import java.time.LocalDate;

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
public class PlayerController {
    private final PlayerService players;

    public PlayerController(PlayerService players) {
        this.players = players;
    }

    @GetMapping("/jugadores")
    public String publicList(Model model) {
        model.addAttribute("players", players.publicList());
        return "public/players";
    }

    @GetMapping("/admin/jugadores")
    public String list(Model model) {
        model.addAttribute("players", players.list());
        return "admin/players/list";
    }

    @GetMapping("/admin/jugadores/nuevo")
    public String createForm(Model model) {
        model.addAttribute("playerForm", new PlayerForm());
        prepareForm(model, null, "/admin/jugadores");
        return "admin/players/form";
    }

    @GetMapping("/admin/jugadores/{id}/editar")
    public String editForm(@PathVariable long id, Model model) {
        model.addAttribute("playerForm", players.formFor(id));
        prepareForm(model, id, "/admin/jugadores/" + id);
        return "admin/players/form";
    }

    @PostMapping({"/admin/jugadores", "/admin/jugadores/{id}"})
    public String save(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute PlayerForm playerForm,
            BindingResult errors,
            Principal principal,
            Model model,
            RedirectAttributes redirect) {
        String action = id == null ? "/admin/jugadores" : "/admin/jugadores/" + id;
        if (errors.hasErrors()) {
            prepareForm(model, id, action);
            return "admin/players/form";
        }
        try {
            players.save(id, playerForm, principal.getName());
        } catch (IllegalArgumentException exception) {
            errors.reject("player.invalid", exception.getMessage());
            prepareForm(model, id, action);
            return "admin/players/form";
        }
        redirect.addFlashAttribute("success", "El jugador se guardó correctamente.");
        return "redirect:/admin/jugadores";
    }

    @PostMapping("/admin/jugadores/{id}/desactivar")
    public String deactivate(@PathVariable long id, Principal principal, RedirectAttributes redirect) {
        players.deactivate(id, principal.getName());
        redirect.addFlashAttribute("success", "El jugador se desactivó y su registro histórico se conservó.");
        return "redirect:/admin/jugadores";
    }

    private void prepareForm(Model model, Long id, String action) {
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("teams", players.activeTeams());
        model.addAttribute("positions", PlayerPosition.values());
        model.addAttribute("statuses", PlayerStatus.values());
        model.addAttribute("action", action);
        model.addAttribute("title", id == null ? "Nuevo jugador" : "Editar jugador");
    }
}
