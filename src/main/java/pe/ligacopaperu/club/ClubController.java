package pe.ligacopaperu.club;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class ClubController {

    private final ClubService clubs;

    public ClubController(ClubService clubs) {
        this.clubs = clubs;
    }

    @GetMapping("/clubes")
    public String publicClubs(Model model) {
        model.addAttribute("clubs", clubs.listPublic());
        return "public/clubs";
    }

    @GetMapping("/admin/clubes")
    public String list(Model model) {
        model.addAttribute("clubs", clubs.listForAdministration());
        return "admin/clubs/list";
    }

    @GetMapping("/admin/clubes/nuevo")
    public String newClub(Model model) {
        model.addAttribute("clubForm", new ClubForm());
        model.addAttribute("pageTitle", "Registrar club");
        model.addAttribute("formAction", "/admin/clubes");
        model.addAttribute("statuses", ClubStatus.values());
        return "admin/clubs/form";
    }

    @PostMapping("/admin/clubes")
    public String create(
            @Valid @ModelAttribute("clubForm") ClubForm form,
            BindingResult result,
            Principal principal,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepareForm(model, "Registrar club", "/admin/clubes", null);
            return "admin/clubs/form";
        }
        try {
            clubs.create(form, principal.getName());
        } catch (DuplicateClubAcronymException exception) {
            result.rejectValue("acronym", "duplicate", exception.getMessage());
            prepareForm(model, "Registrar club", "/admin/clubes", null);
            return "admin/clubs/form";
        }
        redirect.addFlashAttribute("success", "El club se registró correctamente.");
        return "redirect:/admin/clubes";
    }

    @GetMapping("/admin/clubes/{id}/editar")
    public String edit(@PathVariable long id, Model model) {
        model.addAttribute("clubForm", clubs.formFor(id));
        model.addAttribute("pageTitle", "Editar club");
        model.addAttribute("formAction", "/admin/clubes/" + id);
        model.addAttribute("clubId", id);
        return "admin/clubs/form";
    }

    @PostMapping("/admin/clubes/{id}")
    public String update(
            @PathVariable long id,
            @Valid @ModelAttribute("clubForm") ClubForm form,
            BindingResult result,
            Principal principal,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepareForm(model, "Editar club", "/admin/clubes/" + id, id);
            return "admin/clubs/form";
        }
        try {
            clubs.update(id, form, principal.getName());
        } catch (DuplicateClubAcronymException exception) {
            result.rejectValue("acronym", "duplicate", exception.getMessage());
            prepareForm(model, "Editar club", "/admin/clubes/" + id, id);
            return "admin/clubs/form";
        }
        redirect.addFlashAttribute("success", "Los cambios se guardaron correctamente.");
        return "redirect:/admin/clubes";
    }

    @PostMapping("/admin/clubes/{id}/desactivar")
    public String deactivate(@PathVariable long id, Principal principal, RedirectAttributes redirect) {
        clubs.deactivate(id, principal.getName());
        redirect.addFlashAttribute("success", "El club se desactivó y su historial se conservó.");
        return "redirect:/admin/clubes";
    }

    private void prepareForm(Model model, String title, String action, Long clubId) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("formAction", action);
        model.addAttribute("clubId", clubId);
        model.addAttribute("statuses", ClubStatus.values());
    }
}
