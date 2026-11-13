package pe.ligacopaperu.club;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.firestore.Firestore;

import pe.ligacopaperu.auditoria.AuditService;

@Service
public class ClubService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClubService.class);
    public static final int MIN_CLUBS_PER_DISTRICT = 6;
    public static final List<String> SAN_MARCOS_DISTRICTS = List.of(
            "Pedro Gálvez",
            "Chancay",
            "Eduardo Villanueva",
            "Gregorio Pita",
            "Ichocán",
            "José Manuel Quiroz",
            "José Sabogal");

    private final ClubRepository clubs;
    private final AuditService audit;
    private final Firestore firestore;

    @Autowired
    public ClubService(ClubRepository clubs, AuditService audit, Firestore firestore) {
        this.clubs = clubs;
        this.audit = audit;
        this.firestore = firestore;
    }

    public ClubService(ClubRepository clubs, AuditService audit) {
        this(clubs, audit, null);
    }

    @Transactional(readOnly = true)
    public List<Club> listForAdministration() {
        return clubs.findAll(Sort.by(Sort.Direction.ASC, "officialName"));
    }

    @Transactional(readOnly = true)
    public List<Club> listForAdministration(String district) {
        if (district == null || district.isBlank()) {
            return listForAdministration();
        }
        String normalizedDistrict = district.trim();
        return listForAdministration().stream()
                .filter(club -> club.getDistrict() != null
                        && club.getDistrict().equalsIgnoreCase(normalizedDistrict))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Club> listPublic() {
        return clubs.findByStatusOrderByOfficialNameAsc(ClubStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Club> listPublic(String district) {
        if (district == null || district.isBlank()) {
            return listPublic();
        }
        return clubs.findByStatusAndDistrictIgnoreCaseOrderByOfficialNameAsc(ClubStatus.ACTIVE, district.trim());
    }

    @Transactional(readOnly = true)
    public List<DistrictQuotaSummary> sanMarcosDistrictQuotas() {
        return List.of(
                new DistrictQuotaSummary("Pedro Gálvez", "San Marcos (Sede Provincial)",
                        "Estadio Municipal de San Marcos",
                        clubs.countByStatusAndDistrictIgnoreCase(ClubStatus.ACTIVE, "Pedro Gálvez"),
                        MIN_CLUBS_PER_DISTRICT),
                new DistrictQuotaSummary("Chancay", "Chancay",
                        "Estadio Municipal de Chancay",
                        clubs.countByStatusAndDistrictIgnoreCase(ClubStatus.ACTIVE, "Chancay"),
                        MIN_CLUBS_PER_DISTRICT),
                new DistrictQuotaSummary("Eduardo Villanueva", "La Grama",
                        "Estadio Municipal de La Grama",
                        clubs.countByStatusAndDistrictIgnoreCase(ClubStatus.ACTIVE, "Eduardo Villanueva"),
                        MIN_CLUBS_PER_DISTRICT),
                new DistrictQuotaSummary("Gregorio Pita", "Paucamarca",
                        "Estadio Municipal de Paucamarca",
                        clubs.countByStatusAndDistrictIgnoreCase(ClubStatus.ACTIVE, "Gregorio Pita"),
                        MIN_CLUBS_PER_DISTRICT),
                new DistrictQuotaSummary("Ichocán", "Ichocán",
                        "Estadio Municipal de Ichocán",
                        clubs.countByStatusAndDistrictIgnoreCase(ClubStatus.ACTIVE, "Ichocán"),
                        MIN_CLUBS_PER_DISTRICT),
                new DistrictQuotaSummary("José Manuel Quiroz", "Shirac",
                        "Estadio Municipal de Shirac",
                        clubs.countByStatusAndDistrictIgnoreCase(ClubStatus.ACTIVE, "José Manuel Quiroz"),
                        MIN_CLUBS_PER_DISTRICT),
                new DistrictQuotaSummary("José Sabogal", "Venecia",
                        "Estadio Municipal de Venecia",
                        clubs.countByStatusAndDistrictIgnoreCase(ClubStatus.ACTIVE, "José Sabogal"),
                        MIN_CLUBS_PER_DISTRICT));
    }

    @Transactional(readOnly = true)
    public long countActive() {
        return clubs.countByStatus(ClubStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public ClubForm formFor(long id) {
        return ClubForm.from(findClub(id));
    }

    @Transactional
    public Club create(ClubForm form, String actor) {
        checkAcronym(form.getAcronym(), null);
        Club club = new Club();
        apply(form, club);
        Club saved = clubs.save(club);
        syncToFirestore(saved);
        audit.record(actor, "CLUB_CREATED", saved.getId(), saved.getOfficialName());
        return saved;
    }

    @Transactional
    public Club update(long id, ClubForm form, String actor) {
        Club club = findClub(id);
        checkAcronym(form.getAcronym(), id);
        String oldName = club.getOfficialName();
        apply(form, club);
        Club saved = clubs.save(club);
        syncToFirestore(saved);
        audit.record(actor, "CLUB_UPDATED", id, "Nombre anterior: " + oldName + "; nombre nuevo: "
                + saved.getOfficialName());
        return saved;
    }

    @Transactional
    public void deactivate(long id, String actor) {
        Club club = findClub(id);
        club.setStatus(ClubStatus.INACTIVE);
        Club saved = clubs.save(club);
        syncToFirestore(saved);
        audit.record(actor, "CLUB_DEACTIVATED", id, saved.getOfficialName());
    }

    @Transactional
    public void activate(long id, String actor) {
        Club club = findClub(id);
        club.setStatus(ClubStatus.ACTIVE);
        Club saved = clubs.save(club);
        syncToFirestore(saved);
        audit.record(actor, "CLUB_ACTIVATED", id, saved.getOfficialName());
    }

    private Club findClub(long id) {
        return clubs.findById(id).orElseThrow(() -> new ClubNotFoundException(id));
    }

    private void checkAcronym(String acronym, Long currentId) {
        if (acronym == null || acronym.isBlank()) {
            return;
        }
        boolean duplicate = clubs.existsByAcronymIgnoreCase(acronym.trim());
        if (duplicate) {
            boolean sameClub = currentId != null
                    && clubs.findById(currentId)
                            .map(club -> club.getAcronym() != null
                                    && club.getAcronym().equalsIgnoreCase(acronym.trim()))
                            .orElse(false);
            if (!sameClub) {
                throw new DuplicateClubAcronymException();
            }
        }
    }

    private void apply(ClubForm form, Club club) {
        club.setOfficialName(form.getOfficialName().trim());
        club.setShortName(clean(form.getShortName()));
        String acronym = clean(form.getAcronym());
        club.setAcronym(acronym == null ? null : acronym.toUpperCase(Locale.ROOT));
        club.setDescription(clean(form.getDescription()));
        club.setFoundedYear(form.getFoundedYear());
        club.setCountry(clean(form.getCountry()));
        club.setDepartment(clean(form.getDepartment()));
        club.setProvince(clean(form.getProvince()));
        club.setDistrict(clean(form.getDistrict()));
        club.setStadium(clean(form.getStadium()));
        club.setPresident(clean(form.getPresident()));
        club.setDelegateName(clean(form.getDelegateName()));
        club.setPhone(clean(form.getPhone()));
        club.setEmail(clean(form.getEmail()));
        club.setWebsite(clean(form.getWebsite()));
        club.setPrimaryColor(clean(form.getPrimaryColor()));
        club.setSecondaryColor(clean(form.getSecondaryColor()));
        club.setStatus(form.getStatus());
    }

    @SuppressWarnings("null")
    private void syncToFirestore(Club club) {
        if (firestore == null || club == null || club.getId() == null) {
            return;
        }
        try {
            Map<String, Object> doc = new LinkedHashMap<>();
            doc.put("id", club.getId());
            doc.put("officialName", club.getOfficialName());
            doc.put("shortName", club.getShortName());
            doc.put("acronym", club.getAcronym());
            doc.put("description", club.getDescription());
            doc.put("foundedYear", club.getFoundedYear());
            doc.put("country", club.getCountry());
            doc.put("department", club.getDepartment());
            doc.put("province", club.getProvince());
            doc.put("district", club.getDistrict());
            doc.put("stadium", club.getStadium());
            doc.put("president", club.getPresident());
            doc.put("delegateName", club.getDelegateName());
            doc.put("phone", club.getPhone());
            doc.put("email", club.getEmail());
            doc.put("website", club.getWebsite());
            doc.put("primaryColor", club.getPrimaryColor());
            doc.put("secondaryColor", club.getSecondaryColor());
            doc.put("status", club.getStatus() != null ? club.getStatus().name() : null);
            firestore.collection("clubs").document(java.util.Objects.requireNonNull(String.valueOf(club.getId()))).set(doc);
        } catch (Exception exception) {
            LOGGER.warn("No se pudo sincronizar el club en Firestore.", exception);
        }
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
