package pe.ligacopaperu.club;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class ClubController {

    private final ClubService clubs;
    private final SanMarcosBootstrapService sanMarcosBootstrap;

    public ClubController(ClubService clubs, SanMarcosBootstrapService sanMarcosBootstrap) {
        this.clubs = clubs;
        this.sanMarcosBootstrap = sanMarcosBootstrap;
    }

    @GetMapping("/clubes")
    public String publicClubs(@RequestParam(required = false) String distrito, Model model) {
        model.addAttribute("clubs", clubs.listPublic(distrito));
        model.addAttribute("districtQuotas", clubs.sanMarcosDistrictQuotas());
        model.addAttribute("districts", ClubService.SAN_MARCOS_DISTRICTS);
        model.addAttribute("selectedDistrito", distrito);
        return "public/clubs";
    }

    @GetMapping("/admin/clubes")
    public String list(Model model) {
        model.addAttribute("clubs", clubs.listForAdministration());
        model.addAttribute("districtQuotas", clubs.sanMarcosDistrictQuotas());
        model.addAttribute("districts", ClubService.SAN_MARCOS_DISTRICTS);
        return "admin/clubs/list";
    }

    @PostMapping("/admin/clubes/cargar-san-marcos")
    public String bootstrapSanMarcos(Principal principal, RedirectAttributes redirect) {
        sanMarcosBootstrap.ensureSanMarcosLeagueData(principal.getName());
        redirect.addFlashAttribute("success",
                "Se completó el padrón oficial de los 7 distritos de San Marcos (mínimo 6 clubes y equipos por distrito).");
        return "redirect:/admin/clubes";
    }

    @GetMapping("/admin/clubes/nuevo")
    public String newClub(Model model) {
        model.addAttribute("clubForm", new ClubForm());
        prepareForm(model, "Registrar club", "/admin/clubes", null);
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
        prepareForm(model, "Editar club", "/admin/clubes/" + id, id);
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
        model.addAttribute("districts", ClubService.SAN_MARCOS_DISTRICTS);
    }
}
