package pe.ligacopaperu.partido;

import java.security.Principal;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import pe.ligacopaperu.estadistica.StandingsService;
import pe.ligacopaperu.torneo.CompetitionRepository;

@Controller
public class MatchController {

    private final MatchService matches;
    private final StandingsService standings;
    private final CompetitionRepository competitions;
    private final MatchEventService matchEvents;

    public MatchController(MatchService matches, StandingsService standings,
            CompetitionRepository competitions, MatchEventService matchEvents) {
        this.matches = matches;
        this.standings = standings;
        this.competitions = competitions;
        this.matchEvents = matchEvents;
    }

    @GetMapping("/partidos")
    public String publicList(Model model) {
        model.addAttribute("matches", matches.list().stream()
                .filter(match -> match.getStatus() == MatchStatus.SCHEDULED
                        || match.getStatus() == MatchStatus.FINALIZED)
                .toList());
        return "public/matches";
    }

    @GetMapping("/admin/tabla")
    public String adminStandings(@RequestParam(required = false) Long torneo, Model model) {
        var allCompetitions = competitions.findAll(Sort.by(Sort.Direction.DESC, "startDate"));
        model.addAttribute("competitions", allCompetitions);
        if (torneo != null) {
            competitions.findById(torneo).ifPresent(competition -> {
                model.addAttribute("selectedCompetition", competition);
                model.addAttribute("table", standings.table(torneo));
            });
        } else if (!allCompetitions.isEmpty()) {
            var competition = allCompetitions.get(0);
            model.addAttribute("selectedCompetition", competition);
            model.addAttribute("table", standings.table(competition.getId()));
        }
        return "admin/standings";
    }

    @GetMapping("/admin/partidos/{matchId}/acta")
    public String matchSheet(@PathVariable long matchId, Model model) {
        prepareSheet(model, matchId, new MatchEventForm());
        return "admin/matches/sheet";
    }

    @PostMapping("/admin/partidos/{matchId}/acta")
    public String addEvent(@PathVariable long matchId,
            @Valid @ModelAttribute MatchEventForm eventForm,
            BindingResult errors,
            Principal principal,
            Model model,
            RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            prepareSheet(model, matchId, eventForm);
            return "admin/matches/sheet";
        }
        try {
            matchEvents.add(matchId, eventForm, principal.getName());
        } catch (IllegalArgumentException exception) {
            errors.reject("event.invalid", exception.getMessage());
            prepareSheet(model, matchId, eventForm);
            return "admin/matches/sheet";
        }
        redirect.addFlashAttribute("success", "El evento se registró en el acta.");
        return "redirect:/admin/partidos/" + matchId + "/acta";
    }

    @PostMapping("/admin/partidos/{matchId}/acta/{eventId}/eliminar")
    public String removeEvent(@PathVariable long matchId, @PathVariable long eventId,
            Principal principal, RedirectAttributes redirect) {
        matchEvents.remove(matchId, eventId, principal.getName());
        redirect.addFlashAttribute("success", "El evento se eliminó del acta.");
        return "redirect:/admin/partidos/" + matchId + "/acta";
    }

    @GetMapping("/admin/partidos")
    public String list(Model model) {
        model.addAttribute("matches", matches.list());
        return "admin/matches/list";
    }

    @GetMapping("/admin/partidos/nuevo")
    public String createForm(Model model) {
        model.addAttribute("matchForm", new MatchForm());
        prepareForm(model, null, "/admin/partidos");
        return "admin/matches/form";
    }

    @GetMapping("/admin/partidos/{id}/editar")
    public String editForm(@PathVariable long id, Model model) {
        model.addAttribute("matchForm", matches.formFor(id));
        prepareForm(model, id, "/admin/partidos/" + id);
        return "admin/matches/form";
    }

    @PostMapping({"/admin/partidos", "/admin/partidos/{id}"})
    public String save(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute MatchForm matchForm,
            BindingResult errors,
            Principal principal,
            Model model,
            RedirectAttributes redirect) {
        String action = id == null ? "/admin/partidos" : "/admin/partidos/" + id;
        if (errors.hasErrors()) {
            prepareForm(model, id, action);
            return "admin/matches/form";
        }
        try {
            matches.save(id, matchForm, principal.getName());
        } catch (IllegalArgumentException exception) {
            errors.reject("match.invalid", exception.getMessage());
            prepareForm(model, id, action);
            return "admin/matches/form";
        }
        redirect.addFlashAttribute("success", "El partido se guardó correctamente.");
        return "redirect:/admin/partidos";
    }

    private void prepareForm(Model model, Long id, String action) {
        model.addAttribute("competitions", matches.competitions());
        model.addAttribute("teams", matches.activeTeams());
        model.addAttribute("statuses", MatchStatus.values());
        model.addAttribute("action", action);
        model.addAttribute("title", id == null ? "Programar partido" : "Editar partido / resultado");
    }

    private void prepareSheet(Model model, long matchId, MatchEventForm form) {
        model.addAttribute("match", matchEvents.findMatch(matchId));
        model.addAttribute("events", matchEvents.list(matchId));
        model.addAttribute("eligiblePlayers", matchEvents.eligiblePlayers(matchId));
        model.addAttribute("eventForm", form);
        model.addAttribute("eventTypes", MatchEventType.values());
    }
}
