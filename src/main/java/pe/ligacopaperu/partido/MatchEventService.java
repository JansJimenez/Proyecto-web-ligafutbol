package pe.ligacopaperu.partido;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.firestore.Firestore;

import pe.ligacopaperu.auditoria.AuditService;
import pe.ligacopaperu.equipo.Team;
import pe.ligacopaperu.equipo.TeamStatus;
import pe.ligacopaperu.jugador.Player;
import pe.ligacopaperu.jugador.PlayerRepository;
import pe.ligacopaperu.jugador.PlayerStatus;

@Service
public class MatchEventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchEventService.class);

    private final MatchRepository matches;
    private final MatchEventRepository events;
    private final PlayerRepository players;
    private final AuditService audit;
    private final Firestore firestore;

    @Autowired
    public MatchEventService(MatchRepository matches, MatchEventRepository events,
            PlayerRepository players, AuditService audit, Firestore firestore) {
        this.matches = matches;
        this.events = events;
        this.players = players;
        this.audit = audit;
        this.firestore = firestore;
    }

    public MatchEventService(MatchRepository matches, MatchEventRepository events,
            PlayerRepository players, AuditService audit) {
        this(matches, events, players, audit, null);
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
        if (form.getType() == MatchEventType.RED_CARD
                && events.existsByMatchIdAndPlayerIdAndType(matchId, player.getId(), MatchEventType.RED_CARD)) {
            throw new IllegalArgumentException("El jugador ya registra una tarjeta roja en el acta de este partido.");
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
        syncToFirestore(saved);
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
        Long removedId = event.getId();
        audit.record(actor, "MATCH_EVENT_REMOVED", "MATCH_EVENT", removedId,
                matchId + ":" + event.getType() + ":" + event.getPlayer().getFullName());
        events.delete(event);
        deleteFromFirestore(removedId);
    }

    @SuppressWarnings("null")
    private void syncToFirestore(MatchEvent event) {
        if (firestore == null || event == null || event.getId() == null) {
            return;
        }
        try {
            Map<String, Object> doc = new LinkedHashMap<>();
            doc.put("id", event.getId());
            doc.put("matchId", event.getMatch() != null ? event.getMatch().getId() : null);
            doc.put("teamId", event.getTeam() != null ? event.getTeam().getId() : null);
            doc.put("playerId", event.getPlayer() != null ? event.getPlayer().getId() : null);
            doc.put("relatedPlayerId", event.getRelatedPlayer() != null ? event.getRelatedPlayer().getId() : null);
            doc.put("minute", event.getMinute());
            doc.put("type", event.getType() != null ? event.getType().name() : null);
            doc.put("notes", event.getNotes());
            firestore.collection("match_events").document(java.util.Objects.requireNonNull(String.valueOf(event.getId()))).set(doc);
        } catch (Exception exception) {
            LOGGER.warn("No se pudo sincronizar el evento de partido en Firestore.", exception);
        }
    }

    @SuppressWarnings("null")
    private void deleteFromFirestore(Long eventId) {
        if (firestore == null || eventId == null) {
            return;
        }
        try {
            firestore.collection("match_events").document(java.util.Objects.requireNonNull(String.valueOf(eventId))).delete();
        } catch (Exception exception) {
            LOGGER.warn("No se pudo eliminar el evento de partido en Firestore.", exception);
        }
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
