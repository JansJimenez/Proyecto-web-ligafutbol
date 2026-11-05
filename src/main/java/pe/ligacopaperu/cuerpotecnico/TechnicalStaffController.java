package pe.ligacopaperu.cuerpotecnico;

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
public class TechnicalStaffController {
    private final TechnicalStaffService staff;

    public TechnicalStaffController(TechnicalStaffService staff) {
        this.staff = staff;
    }

    @GetMapping("/cuerpo-tecnico")
    public String publicList(Model model) {
        model.addAttribute("staff", staff.publicList());
        return "public/technical-staff";
    }

    @GetMapping("/admin/cuerpo-tecnico")
    public String list(Model model) {
        model.addAttribute("staff", staff.list());
        return "admin/technical-staff/list";
    }

    @GetMapping("/admin/cuerpo-tecnico/nuevo")
    public String createForm(Model model) {
        model.addAttribute("staffForm", new TechnicalStaffForm());
        prepareForm(model, null, "/admin/cuerpo-tecnico");
        return "admin/technical-staff/form";
    }

    @GetMapping("/admin/cuerpo-tecnico/{id}/editar")
    public String editForm(@PathVariable long id, Model model) {
        model.addAttribute("staffForm", staff.formFor(id));
        prepareForm(model, id, "/admin/cuerpo-tecnico/" + id);
        return "admin/technical-staff/form";
    }

    @PostMapping({"/admin/cuerpo-tecnico", "/admin/cuerpo-tecnico/{id}"})
    public String save(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute("staffForm") TechnicalStaffForm form,
            BindingResult errors,
            Principal principal,
            Model model,
            RedirectAttributes redirect) {
        String action = id == null ? "/admin/cuerpo-tecnico" : "/admin/cuerpo-tecnico/" + id;
        if (errors.hasErrors()) {
            prepareForm(model, id, action);
            return "admin/technical-staff/form";
        }
        try {
            staff.save(id, form, principal.getName());
        } catch (IllegalArgumentException exception) {
            errors.reject("staff.invalid", exception.getMessage());
            prepareForm(model, id, action);
            return "admin/technical-staff/form";
        }
        redirect.addFlashAttribute("success", "El integrante del cuerpo técnico se guardó correctamente.");
        return "redirect:/admin/cuerpo-tecnico";
    }

    @PostMapping("/admin/cuerpo-tecnico/{id}/desactivar")
    public String deactivate(@PathVariable long id, Principal principal, RedirectAttributes redirect) {
        staff.deactivate(id, principal.getName());
        redirect.addFlashAttribute("success", "El registro se desactivó y su historial se conservó.");
        return "redirect:/admin/cuerpo-tecnico";
    }

    private void prepareForm(Model model, Long id, String action) {
        model.addAttribute("teams", staff.activeTeams());
        model.addAttribute("roles", StaffRole.values());
        model.addAttribute("statuses", StaffStatus.values());
        model.addAttribute("action", action);
        model.addAttribute("title", id == null ? "Nuevo integrante" : "Editar integrante");
    }
}
