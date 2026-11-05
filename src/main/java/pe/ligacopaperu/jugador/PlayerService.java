package pe.ligacopaperu.jugador;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.ligacopaperu.archivo.ArchivoStorageService;
import pe.ligacopaperu.auditoria.AuditService;
import pe.ligacopaperu.equipo.Team;
import pe.ligacopaperu.equipo.TeamRepository;
import pe.ligacopaperu.equipo.TeamStatus;
import pe.ligacopaperu.club.ClubStatus;

@Service
public class PlayerService {
    private final PlayerRepository players;
    private final TeamRepository teams;
    private final AuditService audit;
    private final ArchivoStorageService storage;

    public PlayerService(PlayerRepository players, TeamRepository teams, AuditService audit,
            ArchivoStorageService storage) {
        this.players = players;
        this.teams = teams;
        this.audit = audit;
        this.storage = storage;
    }

    @Transactional(readOnly = true)
    public List<Player> list() {
        return players.findAllByOrderByLastNameAscFirstNameAsc();
    }

    @Transactional(readOnly = true)
    public List<Player> publicList() {
        return players.findByStatusOrderByLastNameAscFirstNameAsc(PlayerStatus.ACTIVE).stream()
                .filter(player -> player.getTeam().getStatus() == TeamStatus.ACTIVE
                        && player.getTeam().getClub().getStatus() == ClubStatus.ACTIVE)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Team> activeTeams() {
        return teams.findAllByOrderByNameAsc().stream()
                .filter(team -> team.getStatus() == TeamStatus.ACTIVE)
                .toList();
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
                .filter(value -> value.getStatus() == TeamStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Selecciona un equipo activo."));
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
        audit.record(actor, id == null ? "PLAYER_CREATED" : "PLAYER_UPDATED",
                "PLAYER", saved.getId(), saved.getFullName());
        return saved;
    }

    @Transactional
    public void deactivate(long id, String actor) {
        Player player = find(id);
        player.setStatus(PlayerStatus.INACTIVE);
        audit.record(actor, "PLAYER_DEACTIVATED", "PLAYER", id, player.getFullName());
    }

    private Player find(long id) {
        return players.findById(id).orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado."));
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
