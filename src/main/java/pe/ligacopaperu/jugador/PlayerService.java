package pe.ligacopaperu.jugador;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.firestore.Firestore;

import pe.ligacopaperu.archivo.ArchivoStorageService;
import pe.ligacopaperu.auditoria.AuditService;
import pe.ligacopaperu.club.ClubStatus;
import pe.ligacopaperu.equipo.Team;
import pe.ligacopaperu.equipo.TeamRepository;
import pe.ligacopaperu.equipo.TeamStatus;

@Service
public class PlayerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository players;
    private final TeamRepository teams;
    private final AuditService audit;
    private final ArchivoStorageService storage;
    private final Firestore firestore;

    @Autowired
    public PlayerService(PlayerRepository players, TeamRepository teams, AuditService audit,
            ArchivoStorageService storage, Firestore firestore) {
        this.players = players;
        this.teams = teams;
        this.audit = audit;
        this.storage = storage;
        this.firestore = firestore;
    }

    public PlayerService(PlayerRepository players, TeamRepository teams, AuditService audit,
            ArchivoStorageService storage) {
        this(players, teams, audit, storage, null);
    }

    @Transactional(readOnly = true)
    public List<Player> list() {
        return players.findAllByOrderByLastNameAscFirstNameAsc();
    }

    @Transactional(readOnly = true)
    public List<Player> publicList() {
        return players.findByStatusAndTeamStatusAndTeamClubStatusOrderByLastNameAscFirstNameAsc(
                PlayerStatus.ACTIVE, TeamStatus.ACTIVE, ClubStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Player> publicList(Long competitionId) {
        if (competitionId == null) {
            return publicList();
        }
        return players.findByTeamCompetitionIdAndStatusAndTeamStatusAndTeamClubStatusOrderByLastNameAscFirstNameAsc(
                competitionId, PlayerStatus.ACTIVE, TeamStatus.ACTIVE, ClubStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public long countActive() {
        return players.countByStatus(PlayerStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Team> activeTeams() {
        return teams.findByStatusOrderByNameAsc(TeamStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Team> availableTeams(Long playerId) {
        List<Team> active = activeTeams();
        if (playerId == null) {
            return active;
        }
        return players.findById(playerId)
                .map(player -> player.getTeam())
                .filter(team -> active.stream().noneMatch(item -> item.getId().equals(team.getId())))
                .map(team -> {
                    java.util.ArrayList<Team> combined = new java.util.ArrayList<>(active);
                    combined.add(team);
                    return (List<Team>) combined;
                })
                .orElse(active);
    }

    @Transactional(readOnly = true)
    public PlayerForm formFor(long id) {
        Player player = find(id);
        PlayerForm form = new PlayerForm();
        form.setTeamId(player.getTeam().getId());
        form.setFirstName(player.getFirstName());
        form.setLastName(player.getLastName());
        form.setBirthDate(player.getBirthDate());
        form.setJerseyNumber(player.getJerseyNumber());
        form.setPosition(player.getPosition());
        form.setNationality(player.getNationality());
        form.setPhotoUrl(player.getPhotoUrl());
        form.setIdentityDocumentUrl(player.getIdentityDocumentUrl());
        form.setStatus(player.getStatus());
        return form;
    }

    @Transactional
    public Player save(Long id, PlayerForm form, String actor) {
        Player player = id == null ? new Player() : find(id);
        Team team = teams.findById(form.getTeamId())
                .filter(value -> value.getStatus() == TeamStatus.ACTIVE
                        || (id != null && player.getTeam() != null && player.getTeam().getId().equals(value.getId())))
                .orElseThrow(() -> new IllegalArgumentException("Selecciona un equipo activo."));
        if (form.getBirthDate() != null) {
            java.time.LocalDate today = java.time.LocalDate.now();
            if (form.getBirthDate().isAfter(today.minusYears(12))
                    || form.getBirthDate().isBefore(today.minusYears(65))) {
                throw new IllegalArgumentException(
                        "La edad del jugador para Copa Perú debe estar entre 12 y 65 años.");
            }
        }
        if (form.getJerseyNumber() != null) {
            boolean duplicate = id == null
                    ? players.existsByTeamIdAndJerseyNumber(team.getId(), form.getJerseyNumber())
                    : players.existsByTeamIdAndJerseyNumberAndIdNot(
                            team.getId(), form.getJerseyNumber(), id);
            if (duplicate) {
                throw new IllegalArgumentException("Ese número de camiseta ya está ocupado en el equipo.");
            }
        }
        player.setTeam(team);
        player.setFirstName(form.getFirstName().trim());
        player.setLastName(form.getLastName().trim());
        player.setBirthDate(form.getBirthDate());
        player.setJerseyNumber(form.getJerseyNumber());
        player.setPosition(form.getPosition());
        player.setNationality(clean(form.getNationality()));
        player.setPhotoUrl(storage.replacePublicImage(player.getPhotoUrl(), form.getPhotoFile(), form.isRemovePhoto()));
        player.setIdentityDocumentUrl(storage.replacePrivateDocument(player.getIdentityDocumentUrl(),
                form.getIdentityDocumentFile(), form.isRemoveIdentityDocument()));
        player.setStatus(form.getStatus());
        Player saved = players.save(player);
        syncToFirestore(saved);
        audit.record(actor, id == null ? "PLAYER_CREATED" : "PLAYER_UPDATED",
                "PLAYER", saved.getId(), saved.getFullName());
        return saved;
    }

    @Transactional
    public void deactivate(long id, String actor) {
        Player player = find(id);
        player.setStatus(PlayerStatus.INACTIVE);
        Player saved = players.save(player);
        syncToFirestore(saved);
        audit.record(actor, "PLAYER_DEACTIVATED", "PLAYER", id, saved.getFullName());
    }

    private Player find(long id) {
        return players.findById(id).orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado."));
    }

    @SuppressWarnings("null")
    private void syncToFirestore(Player player) {
        if (firestore == null || player == null || player.getId() == null) {
            return;
        }
        try {
            Map<String, Object> doc = new LinkedHashMap<>();
            doc.put("id", player.getId());
            doc.put("teamId", player.getTeam() != null ? player.getTeam().getId() : null);
            doc.put("firstName", player.getFirstName());
            doc.put("lastName", player.getLastName());
            doc.put("birthDate", player.getBirthDate() != null ? player.getBirthDate().toString() : null);
            doc.put("jerseyNumber", player.getJerseyNumber());
            doc.put("position", player.getPosition() != null ? player.getPosition().name() : null);
            doc.put("nationality", player.getNationality());
            doc.put("photoUrl", player.getPhotoUrl());
            doc.put("identityDocumentUrl", player.getIdentityDocumentUrl());
            doc.put("status", player.getStatus() != null ? player.getStatus().name() : null);
            firestore.collection("players").document(java.util.Objects.requireNonNull(String.valueOf(player.getId()))).set(doc);
        } catch (Exception exception) {
            LOGGER.warn("No se pudo sincronizar el jugador en Firestore.", exception);
        }
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
