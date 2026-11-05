package pe.ligacopaperu.partido;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.ligacopaperu.auditoria.AuditService;
import pe.ligacopaperu.equipo.Team;
import pe.ligacopaperu.equipo.TeamStatus;
import pe.ligacopaperu.jugador.Player;
import pe.ligacopaperu.jugador.PlayerRepository;
import pe.ligacopaperu.jugador.PlayerStatus;

@Service
public class MatchEventService {
    private final MatchRepository matches;
    private final MatchEventRepository events;
    private final PlayerRepository players;
    private final AuditService audit;

    public MatchEventService(MatchRepository matches, MatchEventRepository events,
            PlayerRepository players, AuditService audit) {
        this.matches = matches;
        this.events = events;
        this.players = players;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public Match findMatch(long id) {
        return matches.findById(id).orElseThrow(() -> new IllegalArgumentException("Partido no encontrado."));
    }

    @Transactional(readOnly = true)
    public List<MatchEvent> list(long matchId) {
        findMatch(matchId);
        return events.findByMatchIdOrderByMinuteAscIdAsc(matchId);
    }

    @Transactional(readOnly = true)
    public List<Player> eligiblePlayers(long matchId) {
        Match match = findMatch(matchId);
        List<Long> teamIds = List.of(match.getHomeTeam().getId(), match.getAwayTeam().getId());
        return players.findByTeamIdInAndStatusOrderByLastNameAscFirstNameAsc(teamIds, PlayerStatus.ACTIVE);
    }

    @Transactional
    public MatchEvent add(long matchId, MatchEventForm form, String actor) {
        Match match = matches.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado."));
        if (match.getStatus() == MatchStatus.CANCELLED || match.getStatus() == MatchStatus.POSTPONED) {
            throw new IllegalArgumentException("No se pueden registrar eventos en un partido aplazado o cancelado.");
        }
        if (match.getStatus() == MatchStatus.FINALIZED) {
            throw new IllegalArgumentException(
                    "Reabre el partido antes de corregir el acta y vuelve a finalizarlo al terminar.");
        }
        Team team = match.getHomeTeam().getId().equals(form.getTeamId())
                ? match.getHomeTeam()
                : match.getAwayTeam().getId().equals(form.getTeamId()) ? match.getAwayTeam() : null;
        if (team == null || team.getStatus() != TeamStatus.ACTIVE) {
            throw new IllegalArgumentException("El equipo no participa activamente en este partido.");
        }
        Player player = players.findById(form.getPlayerId())
                .filter(value -> value.getStatus() == PlayerStatus.ACTIVE
                        && value.getTeam().getId().equals(team.getId()))
                .orElseThrow(() -> new IllegalArgumentException("El jugador no pertenece al equipo seleccionado."));
        Player relatedPlayer = null;
        if (form.getType() == MatchEventType.SUBSTITUTION) {
            if (form.getRelatedPlayerId() == null || form.getRelatedPlayerId().equals(form.getPlayerId())) {
                throw new IllegalArgumentException("Indica dos jugadores distintos para la sustitución.");
            }
            relatedPlayer = players.findById(form.getRelatedPlayerId())
                    .filter(value -> value.getStatus() == PlayerStatus.ACTIVE
                            && value.getTeam().getId().equals(team.getId()))
                    .orElseThrow(() -> new IllegalArgumentException(
                            "El jugador sustituido debe pertenecer al mismo equipo."));
        } else if (form.getRelatedPlayerId() != null) {
            throw new IllegalArgumentException("El jugador relacionado solo aplica a sustituciones.");
        }
        MatchEvent event = new MatchEvent();
        event.setMatch(match);
        event.setTeam(team);
        event.setPlayer(player);
        event.setRelatedPlayer(relatedPlayer);
        event.setMinute(form.getMinute());
        event.setType(form.getType());
        event.setNotes(clean(form.getNotes()));
        MatchEvent saved = events.save(event);
        audit.record(actor, "MATCH_EVENT_CREATED", "MATCH_EVENT", saved.getId(),
                matchId + ":" + form.getType() + ":" + player.getFullName());
        return saved;
    }

    @Transactional
    public void remove(long matchId, long eventId, String actor) {
        Match match = matches.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado."));
        if (match.getStatus() == MatchStatus.FINALIZED) {
            throw new IllegalArgumentException(
                    "Reabre el partido antes de corregir el acta y vuelve a finalizarlo al terminar.");
        }
        MatchEvent event = events.findById(eventId)
                .filter(value -> value.getMatch().getId().equals(matchId))
                .orElseThrow(() -> new IllegalArgumentException("El evento no pertenece al partido."));
        audit.record(actor, "MATCH_EVENT_REMOVED", "MATCH_EVENT", event.getId(),
                matchId + ":" + event.getType() + ":" + event.getPlayer().getFullName());
        events.delete(event);
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
