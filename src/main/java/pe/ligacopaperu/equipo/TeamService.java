package pe.ligacopaperu.equipo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.firestore.Firestore;

import pe.ligacopaperu.archivo.ArchivoStorageService;
import pe.ligacopaperu.auditoria.AuditService;
import pe.ligacopaperu.club.Club;
import pe.ligacopaperu.club.ClubRepository;
import pe.ligacopaperu.club.ClubStatus;
import pe.ligacopaperu.torneo.Competition;
import pe.ligacopaperu.torneo.CompetitionRepository;

@Service
public class TeamService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository teams;
    private final ClubRepository clubs;
    private final CompetitionRepository competitions;
    private final AuditService audit;
    private final ArchivoStorageService storage;
    private final Firestore firestore;

    @Autowired
    public TeamService(TeamRepository teams, ClubRepository clubs, CompetitionRepository competitions,
            AuditService audit, ArchivoStorageService storage, Firestore firestore) {
        this.teams = teams;
        this.clubs = clubs;
        this.competitions = competitions;
        this.audit = audit;
        this.storage = storage;
        this.firestore = firestore;
    }

    public TeamService(TeamRepository teams, ClubRepository clubs, CompetitionRepository competitions,
            AuditService audit, ArchivoStorageService storage) {
        this(teams, clubs, competitions, audit, storage, null);
    }

    @Transactional(readOnly = true)
    public List<Team> list() {
        return teams.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<Team> publicList() {
        return teams.findByStatusAndClubStatusOrderByNameAsc(TeamStatus.ACTIVE, ClubStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Team> publicList(Long competitionId) {
        if (competitionId == null) {
            return publicList();
        }
        return teams.findByCompetitionIdAndStatusAndClubStatusOrderByNameAsc(
                competitionId, TeamStatus.ACTIVE, ClubStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public long countActive() {
        return teams.countByStatus(TeamStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Team> activeByCompetition(long competitionId) {
        return teams.findByCompetitionIdAndStatusOrderByNameAsc(competitionId, TeamStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Club> availableClubs() {
        return clubs.findByStatusOrderByOfficialNameAsc(ClubStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Club> availableClubs(Long teamId) {
        List<Club> activeClubs = clubs.findByStatusOrderByOfficialNameAsc(ClubStatus.ACTIVE);
        if (teamId == null) {
            return activeClubs;
        }
        return teams.findById(teamId)
                .map(team -> team.getClub())
                .filter(club -> activeClubs.stream().noneMatch(active -> active.getId().equals(club.getId())))
                .map(club -> {
                    java.util.ArrayList<Club> combined = new java.util.ArrayList<>(activeClubs);
                    combined.add(club);
                    return (List<Club>) combined;
                })
                .orElse(activeClubs);
    }

    @Transactional(readOnly = true)
    public List<Competition> competitions() {
        return competitions.findAll(Sort.by(Sort.Direction.DESC, "startDate"));
    }

    @Transactional(readOnly = true)
    public TeamForm formFor(long id) {
        Team team = find(id);
        TeamForm form = new TeamForm();
        form.setClubId(team.getClub().getId());
        form.setCompetitionId(team.getCompetition().getId());
        form.setName(team.getName());
        form.setShortName(team.getShortName());
        form.setGroupName(team.getGroupName());
        form.setStadium(team.getStadium());
        form.setCrestUrl(team.getCrestUrl());
        form.setStatus(team.getStatus());
        return form;
    }

    @Transactional
    public Team save(Long id, TeamForm form, String actor) {
        Team team = id == null ? new Team() : find(id);
        Club club = clubs.findById(form.getClubId())
                .filter(value -> value.getStatus() == ClubStatus.ACTIVE
                        || (id != null && team.getClub() != null && team.getClub().getId().equals(value.getId())))
                .orElseThrow(() -> new IllegalArgumentException("Selecciona un club activo."));
        Competition competition = competitions.findById(form.getCompetitionId())
                .orElseThrow(() -> new IllegalArgumentException("Selecciona un torneo válido."));
        boolean duplicate = teams.existsByCompetitionIdAndNameIgnoreCase(competition.getId(), form.getName().trim());
        if (duplicate && (id == null || !team.getName().equalsIgnoreCase(form.getName().trim())
                || !team.getCompetition().getId().equals(competition.getId()))) {
            throw new IllegalArgumentException("Ya existe un equipo con ese nombre en el torneo.");
        }
        team.setClub(club);
        team.setCompetition(competition);
        team.setName(form.getName().trim());
        team.setShortName(clean(form.getShortName()));
        team.setGroupName(clean(form.getGroupName()));
        team.setStadium(clean(form.getStadium()));
        team.setCrestUrl(storage.replacePublicImage(team.getCrestUrl(), form.getCrestFile(), form.isRemoveCrest()));
        team.setStatus(form.getStatus());
        Team saved = teams.save(team);
        syncToFirestore(saved);
        audit.record(actor, id == null ? "TEAM_CREATED" : "TEAM_UPDATED",
                "TEAM", saved.getId(), saved.getName());
        return saved;
    }

    @Transactional
    public void deactivate(long id, String actor) {
        Team team = find(id);
        team.setStatus(TeamStatus.INACTIVE);
        Team saved = teams.save(team);
        syncToFirestore(saved);
        audit.record(actor, "TEAM_DEACTIVATED", "TEAM", id, saved.getName());
    }

    @Transactional(readOnly = true)
    public Team find(long id) {
        return teams.findById(id).orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado."));
    }

    @SuppressWarnings("null")
    private void syncToFirestore(Team team) {
        if (firestore == null || team == null || team.getId() == null) {
            return;
        }
        try {
            Map<String, Object> doc = new LinkedHashMap<>();
            doc.put("id", team.getId());
            doc.put("clubId", team.getClub() != null ? team.getClub().getId() : null);
            doc.put("competitionId", team.getCompetition() != null ? team.getCompetition().getId() : null);
            doc.put("name", team.getName());
            doc.put("shortName", team.getShortName());
            doc.put("groupName", team.getGroupName());
            doc.put("stadium", team.getStadium());
            doc.put("crestUrl", team.getCrestUrl());
            doc.put("status", team.getStatus() != null ? team.getStatus().name() : null);
            firestore.collection("teams").document(java.util.Objects.requireNonNull(String.valueOf(team.getId()))).set(doc);
        } catch (Exception exception) {
            LOGGER.warn("No se pudo sincronizar el equipo en Firestore.", exception);
        }
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
