package pe.ligacopaperu.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.ligacopaperu.club.Club;
import pe.ligacopaperu.club.ClubService;

@Controller
public class PublicController {

    private final ClubService clubs;
    private final String leagueName;

    public PublicController(ClubService clubs, @Value("${app.league-name:Liga Copa Perú}") String leagueName) {
        this.clubs = clubs;
        this.leagueName = leagueName;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Club> activeClubs = clubs.listPublic();
        model.addAttribute("leagueName", leagueName);
        model.addAttribute("clubs", activeClubs.stream().limit(4).toList());
        model.addAttribute("activeClubCount", activeClubs.size());
        return "public/home";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}
