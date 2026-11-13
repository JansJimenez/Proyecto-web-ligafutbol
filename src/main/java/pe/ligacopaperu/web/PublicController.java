package pe.ligacopaperu.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.ligacopaperu.club.Club;
import pe.ligacopaperu.club.ClubService;
import pe.ligacopaperu.equipo.TeamService;
import pe.ligacopaperu.jugador.PlayerService;
import pe.ligacopaperu.partido.MatchService;

@Controller
public class PublicController {

    private final ClubService clubs;
    private final TeamService teams;
    private final PlayerService players;
    private final MatchService matches;
    private final String leagueName;

    public PublicController(ClubService clubs, TeamService teams, PlayerService players, MatchService matches,
            @Value("${app.league-name:Liga Copa Perú}") String leagueName) {
        this.clubs = clubs;
        this.teams = teams;
        this.players = players;
        this.matches = matches;
        this.leagueName = leagueName;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Club> activeClubs = clubs.listPublic();
        model.addAttribute("leagueName", leagueName);
        model.addAttribute("clubs", activeClubs.stream().limit(6).toList());
        model.addAttribute("districtQuotas", clubs.sanMarcosDistrictQuotas());
        model.addAttribute("activeClubCount", activeClubs.size());
        model.addAttribute("activeTeamCount", teams.countActive());
        model.addAttribute("activePlayerCount", players.countActive());
        model.addAttribute("finalizedMatchCount", matches.countFinalized());
        return "public/home";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/politica-privacidad")
    public String privacyPolicy() {
        return "public/privacy";
    }

    @GetMapping("/terminos-uso")
    public String termsOfUse() {
        return "public/terms";
    }
}
