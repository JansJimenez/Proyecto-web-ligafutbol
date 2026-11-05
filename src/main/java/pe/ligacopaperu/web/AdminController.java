package pe.ligacopaperu.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.ligacopaperu.club.ClubService;

@Controller
public class AdminController {

    private final ClubService clubs;
    private final String leagueName;

    public AdminController(ClubService clubs, @Value("${app.league-name:Liga Copa Perú}") String leagueName) {
        this.clubs = clubs;
        this.leagueName = leagueName;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("leagueName", leagueName);
        model.addAttribute("activeClubCount", clubs.countActive());
        return "admin/dashboard";
    }
}
