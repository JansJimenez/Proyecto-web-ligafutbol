package pe.ligacopaperu.estadistica;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.ligacopaperu.torneo.CompetitionRepository;

@Controller
public class StandingsController {
    private final StandingsService standings;
    private final CompetitionRepository competitions;

    public StandingsController(StandingsService standings, CompetitionRepository competitions) {
        this.standings = standings;
        this.competitions = competitions;
    }

    @GetMapping("/tabla")
    public String publicTable(@RequestParam(required = false) Long torneo, Model model) {
        var allCompetitions = competitions.findAll(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "startDate"));
        model.addAttribute("competitions", allCompetitions);
        if (torneo != null) {
            var competition = competitions.findById(torneo).orElse(null);
            if (competition != null) {
                model.addAttribute("selectedCompetition", competition);
                model.addAttribute("table", standings.table(torneo));
            }
        } else if (!allCompetitions.isEmpty()) {
            var competition = allCompetitions.get(0);
            model.addAttribute("selectedCompetition", competition);
            model.addAttribute("table", standings.table(competition.getId()));
        }
        return "public/standings";
    }
}
