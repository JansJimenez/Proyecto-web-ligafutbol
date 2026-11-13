package pe.ligacopaperu.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.ligacopaperu.auditoria.AuditService;
import pe.ligacopaperu.club.ClubService;
import pe.ligacopaperu.equipo.TeamService;
import pe.ligacopaperu.jugador.PlayerService;
import pe.ligacopaperu.noticia.NewsService;
import pe.ligacopaperu.partido.MatchService;

@Controller
public class AdminController {

    private final ClubService clubs;
    private final TeamService teams;
    private final PlayerService players;
    private final MatchService matches;
    private final NewsService news;
    private final AuditService audit;
    private final String leagueName;

    public AdminController(ClubService clubs, TeamService teams, PlayerService players,
            MatchService matches, NewsService news, AuditService audit,
            @Value("${app.league-name:Liga Copa Perú}") String leagueName) {
        this.clubs = clubs;
        this.teams = teams;
        this.players = players;
        this.matches = matches;
        this.news = news;
        this.audit = audit;
        this.leagueName = leagueName;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("leagueName", leagueName);
        model.addAttribute("districtQuotas", clubs.sanMarcosDistrictQuotas());
        model.addAttribute("activeClubCount", clubs.countActive());
        model.addAttribute("activeTeamCount", teams.countActive());
        model.addAttribute("activePlayerCount", players.countActive());
        model.addAttribute("finalizedMatchCount", matches.countFinalized());
        model.addAttribute("publishedNewsCount", news.countPublished());
        return "admin/dashboard";
    }

    @GetMapping("/admin/auditoria")
    public String auditLog(Model model) {
        model.addAttribute("entries", audit.listRecent());
        return "admin/audit";
    }
}
