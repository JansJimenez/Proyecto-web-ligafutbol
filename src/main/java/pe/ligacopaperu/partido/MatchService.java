package pe.ligacopaperu.partido;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.ligacopaperu.auditoria.AuditService;
import pe.ligacopaperu.equipo.Team;
import pe.ligacopaperu.equipo.TeamRepository;
import pe.ligacopaperu.equipo.TeamStatus;
import pe.ligacopaperu.torneo.Competition;
import pe.ligacopaperu.torneo.CompetitionRepository;

@Service
public class MatchService {
    private final MatchRepository matches;
    private final CompetitionRepository competitions;
    private final TeamRepository teams;
    private final MatchEventRepository events;
    private final AuditService audit;

    public MatchService(MatchRepository matches, CompetitionRepository competitions,
            TeamRepository teams, MatchEventRepository events, AuditService audit) {
        this.matches = matches;
        this.competitions = competitions;
        this.teams = teams;
        this.events = events;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<Match> list() {
        return matches.findAllByOrderByScheduledAtAsc();
    }

    @Transactional(readOnly = true)
    public List<Competition> competitions() {
        return competitions.findAll(Sort.by(Sort.Direction.DESC, "startDate"));
    }

    @Transactional(readOnly = true)
    public List<Team> activeTeams() {
        return teams.findAllByOrderByNameAsc().stream()
                .filter(team -> team.getStatus() == TeamStatus.ACTIVE)
                .toList();
    }

    @Transactional(readOnly = true)
    public MatchForm formFor(long id) {
        Match match = find(id);
        MatchForm form = new MatchForm();
        form.setCompetitionId(match.getCompetition().getId());
        form.setHomeTeamId(match.getHomeTeam().getId());
        form.setAwayTeamId(match.getAwayTeam().getId());
        form.setRoundNumber(match.getRoundNumber());
        form.setScheduledAt(match.getScheduledAt());
        form.setStadium(match.getStadium());
        form.setHomeGoals(match.getHomeGoals());
        form.setAwayGoals(match.getAwayGoals());
        form.setStatus(match.getStatus());
        return form;
    }

    @Transactional
    public Match save(Long id, MatchForm form, String actor) {
        if (form.getHomeTeamId().equals(form.getAwayTeamId())) {
            throw new IllegalArgumentException("Un equipo no puede enfrentarse a sí mismo.");
        }
        Competition competition = competitions.findById(form.getCompetitionId())
                .orElseThrow(() -> new IllegalArgumentException("Selecciona un torneo válido."));
        Team home = teams.findById(form.getHomeTeamId())
                .filter(team -> team.getStatus() == TeamStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("El equipo local no está activo."));
        Team away = teams.findById(form.getAwayTeamId())
                .filter(team -> team.getStatus() == TeamStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("El equipo visitante no está activo."));
        if (!home.getCompetition().getId().equals(competition.getId())
                || !away.getCompetition().getId().equals(competition.getId())) {
            throw new IllegalArgumentException("Ambos equipos deben estar inscritos en el torneo seleccionado.");
        }
        if (form.getStatus() == MatchStatus.FINALIZED
                && (form.getHomeGoals() == null || form.getAwayGoals() == null)) {
            throw new IllegalArgumentException("Registra ambos marcadores antes de finalizar el partido.");
        }
        if (form.getStatus() == MatchStatus.FINALIZED) {
            validateGoalEvents(form, home, away, id);
        }
        Match match = id == null ? new Match() : find(id);
        match.setCompetition(competition);
        match.setHomeTeam(home);
        match.setAwayTeam(away);
        match.setRoundNumber(form.getRoundNumber());
        match.setScheduledAt(form.getScheduledAt());
        match.setStadium(clean(form.getStadium()));
        match.setHomeGoals(form.getHomeGoals());
        match.setAwayGoals(form.getAwayGoals());
        match.setStatus(form.getStatus());
        Match saved = matches.save(match);
        String details = home.getName() + " vs " + away.getName()
                + (form.getStatus() == MatchStatus.FINALIZED
                        ? " " + form.getHomeGoals() + "-" + form.getAwayGoals()
                        : " (" + form.getStatus() + ")");
        audit.record(actor, id == null ? "MATCH_CREATED" : "MATCH_UPDATED",
                "MATCH", saved.getId(), details);
        return saved;
    }

    private Match find(long id) {
        return matches.findById(id).orElseThrow(() -> new IllegalArgumentException("Partido no encontrado."));
    }

    private void validateGoalEvents(MatchForm form, Team home, Team away, Long matchId) {
        if (matchId == null) {
            return;
        }
        int homeGoals = 0;
        int awayGoals = 0;
        boolean hasGoalEvents = false;
        for (MatchEvent event : events.findByMatchIdOrderByMinuteAscIdAsc(matchId)) {
            if (event.getType() != MatchEventType.GOAL && event.getType() != MatchEventType.OWN_GOAL) {
                continue;
            }
            hasGoalEvents = true;
            boolean creditedHome = event.getType() == MatchEventType.GOAL
                    ? event.getTeam().getId().equals(home.getId())
                    : event.getTeam().getId().equals(away.getId());
            if (creditedHome) {
                homeGoals++;
            } else {
                awayGoals++;
            }
        }
        if (hasGoalEvents && (homeGoals != form.getHomeGoals() || awayGoals != form.getAwayGoals())) {
            throw new IllegalArgumentException(
                    "El marcador no coincide con los goles y autogoles registrados en el acta.");
        }
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
