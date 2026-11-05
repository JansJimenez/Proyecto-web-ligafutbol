package pe.ligacopaperu.torneo;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.ligacopaperu.auditoria.AuditService;

@Service
public class SeasonService {
    private final SeasonRepository seasons;
    private final AuditService audit;

    public SeasonService(SeasonRepository seasons, AuditService audit) {
        this.seasons = seasons;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<Season> list() {
        return seasons.findAll(Sort.by(Sort.Direction.DESC, "year").and(Sort.by("name")));
    }

    @Transactional
    public Season save(Long id, SeasonForm form, String actor) {
        if (form.getStartDate() != null && form.getEndDate() != null
                && form.getEndDate().isBefore(form.getStartDate())) {
            throw new IllegalArgumentException("La fecha de término debe ser posterior al inicio.");
        }
        Season season = id == null ? new Season()
                : seasons.findById(id).orElseThrow(() -> new IllegalArgumentException("Temporada no encontrada."));
        if (seasons.existsByNameIgnoreCaseAndYear(form.getName().trim(), form.getYear())
                && (id == null || !season.getName().equalsIgnoreCase(form.getName().trim())
                        || !season.getYear().equals(form.getYear()))) {
            throw new IllegalArgumentException("Ya existe una temporada con ese nombre y año.");
        }
        season.setName(form.getName().trim());
        season.setYear(form.getYear());
        season.setStartDate(form.getStartDate());
        season.setEndDate(form.getEndDate());
        season.setStatus(form.getStatus());
        Season saved = seasons.save(season);
        audit.record(actor, id == null ? "SEASON_CREATED" : "SEASON_UPDATED",
                "SEASON", saved.getId(), saved.getName() + " " + saved.getYear());
        return saved;
    }

    @Transactional(readOnly = true)
    public SeasonForm formFor(long id) {
        Season season = seasons.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Temporada no encontrada."));
        SeasonForm form = new SeasonForm();
        form.setName(season.getName());
        form.setYear(season.getYear());
        form.setStartDate(season.getStartDate());
        form.setEndDate(season.getEndDate());
        form.setStatus(season.getStatus());
        return form;
    }
}
