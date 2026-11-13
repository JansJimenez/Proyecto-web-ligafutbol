package pe.ligacopaperu.torneo;

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

import pe.ligacopaperu.auditoria.AuditService;

@Service
public class SeasonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeasonService.class);

    private final SeasonRepository seasons;
    private final AuditService audit;
    private final Firestore firestore;

    @Autowired
    public SeasonService(SeasonRepository seasons, AuditService audit, Firestore firestore) {
        this.seasons = seasons;
        this.audit = audit;
        this.firestore = firestore;
    }

    public SeasonService(SeasonRepository seasons, AuditService audit) {
        this(seasons, audit, null);
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
        syncToFirestore(saved);
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

    @SuppressWarnings("null")
    private void syncToFirestore(Season season) {
        if (firestore == null || season == null || season.getId() == null) {
            return;
        }
        try {
            Map<String, Object> doc = new LinkedHashMap<>();
            doc.put("id", season.getId());
            doc.put("name", season.getName());
            doc.put("year", season.getYear());
            doc.put("startDate", season.getStartDate() != null ? season.getStartDate().toString() : null);
            doc.put("endDate", season.getEndDate() != null ? season.getEndDate().toString() : null);
            doc.put("status", season.getStatus() != null ? season.getStatus().name() : null);
            firestore.collection("seasons").document(java.util.Objects.requireNonNull(String.valueOf(season.getId()))).set(doc);
        } catch (Exception exception) {
            LOGGER.warn("No se pudo sincronizar la temporada en Firestore.", exception);
        }
    }
}
