package pe.ligacopaperu.estadistica;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.ligacopaperu.torneo.CompetitionRepository;

@Controller
public class StatisticsController {
    private final StatisticsService statistics;
    private final CompetitionRepository competitions;

    public StatisticsController(StatisticsService statistics, CompetitionRepository competitions) {
        this.statistics = statistics;
        this.competitions = competitions;
    }

    @GetMapping("/estadisticas")
    public String statistics(@RequestParam(required = false) Long torneo, Model model) {
        model.addAttribute("players", statistics.playerStatistics(torneo));
        model.addAttribute("competitions", competitions.findAll(Sort.by(Sort.Direction.DESC, "startDate")));
        model.addAttribute("selectedTorneo", torneo);
        return "public/statistics";
    }
}
