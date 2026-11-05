package pe.ligacopaperu.club;

import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.ligacopaperu.auditoria.AuditService;

@Service
public class ClubService {

    private final ClubRepository clubs;
    private final AuditService audit;

    public ClubService(ClubRepository clubs, AuditService audit) {
        this.clubs = clubs;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<Club> listForAdministration() {
        return clubs.findAll(Sort.by(Sort.Direction.ASC, "officialName"));
    }

    @Transactional(readOnly = true)
    public List<Club> listPublic() {
        return clubs.findByStatusOrderByOfficialNameAsc(ClubStatus.ACTIVE);
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
        audit.record(actor, "CLUB_CREATED", saved.getId(), saved.getOfficialName());
        return saved;
    }

    @Transactional
    public Club update(long id, ClubForm form, String actor) {
        Club club = findClub(id);
        checkAcronym(form.getAcronym(), id);
        String oldName = club.getOfficialName();
        apply(form, club);
        audit.record(actor, "CLUB_UPDATED", id, "Nombre anterior: " + oldName + "; nombre nuevo: "
                + club.getOfficialName());
        return club;
    }

    @Transactional
    public void deactivate(long id, String actor) {
        Club club = findClub(id);
        club.setStatus(ClubStatus.INACTIVE);
        audit.record(actor, "CLUB_DEACTIVATED", id, club.getOfficialName());
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

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
