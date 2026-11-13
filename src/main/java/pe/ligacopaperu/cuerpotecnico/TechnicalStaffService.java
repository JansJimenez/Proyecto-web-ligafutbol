package pe.ligacopaperu.cuerpotecnico;

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
public class TechnicalStaffService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TechnicalStaffService.class);

    private final TechnicalStaffRepository staff;
    private final TeamRepository teams;
    private final AuditService audit;
    private final ArchivoStorageService storage;
    private final Firestore firestore;

    @Autowired
    public TechnicalStaffService(TechnicalStaffRepository staff, TeamRepository teams, AuditService audit,
            ArchivoStorageService storage, Firestore firestore) {
        this.staff = staff;
        this.teams = teams;
        this.audit = audit;
        this.storage = storage;
        this.firestore = firestore;
    }

    public TechnicalStaffService(TechnicalStaffRepository staff, TeamRepository teams, AuditService audit,
            ArchivoStorageService storage) {
        this(staff, teams, audit, storage, null);
    }

    @Transactional(readOnly = true)
    public List<TechnicalStaff> list() {
        return staff.findAllByOrderByLastNameAscFirstNameAsc();
    }

    @Transactional(readOnly = true)
    public List<TechnicalStaff> publicList() {
        return staff.findByStatusAndTeamStatusAndTeamClubStatusOrderByLastNameAscFirstNameAsc(
                StaffStatus.ACTIVE, TeamStatus.ACTIVE, ClubStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Team> activeTeams() {
        return teams.findByStatusOrderByNameAsc(TeamStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Team> availableTeams(Long staffId) {
        List<Team> active = activeTeams();
        if (staffId == null) {
            return active;
        }
        return staff.findById(staffId)
                .map(person -> person.getTeam())
                .filter(team -> active.stream().noneMatch(item -> item.getId().equals(team.getId())))
                .map(team -> {
                    java.util.ArrayList<Team> combined = new java.util.ArrayList<>(active);
                    combined.add(team);
                    return (List<Team>) combined;
                })
                .orElse(active);
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
                .filter(value -> value.getStatus() == TeamStatus.ACTIVE
                        || (id != null && person.getTeam() != null && person.getTeam().getId().equals(value.getId())))
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
        syncToFirestore(saved);
        audit.record(actor, id == null ? "STAFF_CREATED" : "STAFF_UPDATED",
                "TECHNICAL_STAFF", saved.getId(), saved.getFullName() + " · " + saved.getRole());
        return saved;
    }

    @Transactional
    public void deactivate(long id, String actor) {
        TechnicalStaff person = find(id);
        person.setStatus(StaffStatus.INACTIVE);
        TechnicalStaff saved = staff.save(person);
        syncToFirestore(saved);
        audit.record(actor, "STAFF_DEACTIVATED", "TECHNICAL_STAFF", id, saved.getFullName());
    }

    private TechnicalStaff find(long id) {
        return staff.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Integrante del cuerpo técnico no encontrado."));
    }

    @SuppressWarnings("null")
    private void syncToFirestore(TechnicalStaff person) {
        if (firestore == null || person == null || person.getId() == null) {
            return;
        }
        try {
            Map<String, Object> doc = new LinkedHashMap<>();
            doc.put("id", person.getId());
            doc.put("teamId", person.getTeam() != null ? person.getTeam().getId() : null);
            doc.put("firstName", person.getFirstName());
            doc.put("lastName", person.getLastName());
            doc.put("role", person.getRole() != null ? person.getRole().name() : null);
            doc.put("email", person.getEmail());
            doc.put("phone", person.getPhone());
            doc.put("photoUrl", person.getPhotoUrl());
            doc.put("identityDocumentUrl", person.getIdentityDocumentUrl());
            doc.put("status", person.getStatus() != null ? person.getStatus().name() : null);
            firestore.collection("technical_staff").document(java.util.Objects.requireNonNull(String.valueOf(person.getId()))).set(doc);
        } catch (Exception exception) {
            LOGGER.warn("No se pudo sincronizar el cuerpo técnico en Firestore.", exception);
        }
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
