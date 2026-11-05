package pe.ligacopaperu.cuerpotecnico;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.ligacopaperu.archivo.ArchivoStorageService;
import pe.ligacopaperu.auditoria.AuditService;
import pe.ligacopaperu.club.ClubStatus;
import pe.ligacopaperu.equipo.Team;
import pe.ligacopaperu.equipo.TeamRepository;
import pe.ligacopaperu.equipo.TeamStatus;

@Service
public class TechnicalStaffService {
    private final TechnicalStaffRepository staff;
    private final TeamRepository teams;
    private final AuditService audit;
    private final ArchivoStorageService storage;

    public TechnicalStaffService(TechnicalStaffRepository staff, TeamRepository teams, AuditService audit,
            ArchivoStorageService storage) {
        this.staff = staff;
        this.teams = teams;
        this.audit = audit;
        this.storage = storage;
    }

    @Transactional(readOnly = true)
    public List<TechnicalStaff> list() {
        return staff.findAllByOrderByLastNameAscFirstNameAsc();
    }

    @Transactional(readOnly = true)
    public List<TechnicalStaff> publicList() {
        return staff.findByStatusOrderByLastNameAscFirstNameAsc(StaffStatus.ACTIVE).stream()
                .filter(person -> person.getTeam().getStatus() == TeamStatus.ACTIVE
                        && person.getTeam().getClub().getStatus() == ClubStatus.ACTIVE)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Team> activeTeams() {
        return teams.findAllByOrderByNameAsc().stream()
                .filter(team -> team.getStatus() == TeamStatus.ACTIVE)
                .toList();
    }

    @Transactional(readOnly = true)
    public TechnicalStaffForm formFor(long id) {
        TechnicalStaff person = find(id);
        TechnicalStaffForm form = new TechnicalStaffForm();
        form.setTeamId(person.getTeam().getId());
        form.setFirstName(person.getFirstName());
        form.setLastName(person.getLastName());
        form.setRole(person.getRole());
        form.setEmail(person.getEmail());
        form.setPhone(person.getPhone());
        form.setPhotoUrl(person.getPhotoUrl());
        form.setIdentityDocumentUrl(person.getIdentityDocumentUrl());
        form.setStatus(person.getStatus());
        return form;
    }

    @Transactional
    public TechnicalStaff save(Long id, TechnicalStaffForm form, String actor) {
        TechnicalStaff person = id == null ? new TechnicalStaff() : find(id);
        Team team = teams.findById(form.getTeamId())
                .filter(value -> value.getStatus() == TeamStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Selecciona un equipo activo."));
        person.setTeam(team);
        person.setFirstName(form.getFirstName().trim());
        person.setLastName(form.getLastName().trim());
        person.setRole(form.getRole());
        person.setEmail(clean(form.getEmail()));
        person.setPhone(clean(form.getPhone()));
        person.setPhotoUrl(storage.replacePublicImage(person.getPhotoUrl(), form.getPhotoFile(), form.isRemovePhoto()));
        person.setIdentityDocumentUrl(storage.replacePrivateDocument(person.getIdentityDocumentUrl(),
                form.getIdentityDocumentFile(), form.isRemoveIdentityDocument()));
        person.setStatus(form.getStatus());
        TechnicalStaff saved = staff.save(person);
        audit.record(actor, id == null ? "STAFF_CREATED" : "STAFF_UPDATED",
                "TECHNICAL_STAFF", saved.getId(), saved.getFullName() + " · " + saved.getRole());
        return saved;
    }

    @Transactional
    public void deactivate(long id, String actor) {
        TechnicalStaff person = find(id);
        person.setStatus(StaffStatus.INACTIVE);
        audit.record(actor, "STAFF_DEACTIVATED", "TECHNICAL_STAFF", id, person.getFullName());
    }

    private TechnicalStaff find(long id) {
        return staff.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Integrante del cuerpo técnico no encontrado."));
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
