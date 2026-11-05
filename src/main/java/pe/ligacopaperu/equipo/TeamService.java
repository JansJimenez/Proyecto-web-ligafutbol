package pe.ligacopaperu.equipo;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.ligacopaperu.archivo.ArchivoStorageService;

import pe.ligacopaperu.auditoria.AuditService;
import pe.ligacopaperu.club.Club;
import pe.ligacopaperu.club.ClubRepository;
import pe.ligacopaperu.club.ClubStatus;
import pe.ligacopaperu.torneo.Competition;
import pe.ligacopaperu.torneo.CompetitionRepository;

@Service
public class TeamService {
    private final TeamRepository teams;
    private final ClubRepository clubs;
    private final CompetitionRepository competitions;
    private final AuditService audit;
    private final ArchivoStorageService storage;

    public TeamService(TeamRepository teams, ClubRepository clubs, CompetitionRepository competitions,
            AuditService audit, ArchivoStorageService storage) {
        this.teams = teams;
        this.clubs = clubs;
        this.competitions = competitions;
        this.audit = audit;
        this.storage = storage;
    }

    @Transactional(readOnly = true)
    public List<Team> list() {
        return teams.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<Team> publicList() {
        return teams.findAllByOrderByNameAsc().stream()
                .filter(team -> team.getStatus() == TeamStatus.ACTIVE
                        && team.getClub().getStatus() == ClubStatus.ACTIVE)
                .toList();
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
                .filter(value -> value.getStatus() == ClubStatus.ACTIVE)
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
        audit.record(actor, id == null ? "TEAM_CREATED" : "TEAM_UPDATED",
                "TEAM", saved.getId(), saved.getName());
        return saved;
    }

    @Transactional
    public void deactivate(long id, String actor) {
        Team team = find(id);
        team.setStatus(TeamStatus.INACTIVE);
        audit.record(actor, "TEAM_DEACTIVATED", "TEAM", id, team.getName());
    }

    @Transactional(readOnly = true)
    public Team find(long id) {
        return teams.findById(id).orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado."));
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
