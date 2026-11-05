package pe.ligacopaperu.estadistica;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatisticsController {
    private final StatisticsService statistics;

    public StatisticsController(StatisticsService statistics) {
        this.statistics = statistics;
    }

    @GetMapping("/estadisticas")
    public String statistics(Model model) {
        model.addAttribute("players", statistics.playerStatistics());
        return "public/statistics";
    }
}
