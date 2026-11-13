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
public class CompetitionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompetitionService.class);

    private final CompetitionRepository competitions;
    private final SeasonRepository seasons;
    private final AuditService audit;
    private final Firestore firestore;

    @Autowired
    public CompetitionService(CompetitionRepository competitions, SeasonRepository seasons, AuditService audit,
            Firestore firestore) {
        this.competitions = competitions;
        this.seasons = seasons;
        this.audit = audit;
        this.firestore = firestore;
    }

    public CompetitionService(CompetitionRepository competitions, SeasonRepository seasons, AuditService audit) {
        this(competitions, seasons, audit, null);
    }

    @Transactional(readOnly = true)
    public List<Competition> list() {
        return competitions.findAll(Sort.by(Sort.Direction.DESC, "startDate").and(Sort.by("name")));
    }

    @Transactional(readOnly = true)
    public List<Season> seasons() {
        return seasons.findAllByOrderByYearDescNameAsc();
    }

    @Transactional(readOnly = true)
    public CompetitionForm formFor(long id) {
        Competition competition = find(id);
        CompetitionForm form = new CompetitionForm();
        form.setName(competition.getName());
        form.setSeasonId(competition.getSeason().getId());
        form.setCategory(competition.getCategory());
        form.setStartDate(competition.getStartDate());
        form.setEndDate(competition.getEndDate());
        form.setPointsWin(competition.getPointsWin());
        form.setPointsDraw(competition.getPointsDraw());
        form.setPointsLoss(competition.getPointsLoss());
        form.setStatus(competition.getStatus());
        return form;
    }

    @Transactional
    public Competition save(Long id, CompetitionForm form, String actor) {
        if (form.getStartDate() != null && form.getEndDate() != null
                && form.getEndDate().isBefore(form.getStartDate())) {
            throw new IllegalArgumentException("La fecha de término debe ser posterior al inicio.");
        }
        if (form.getPointsWin() != null && form.getPointsDraw() != null && form.getPointsLoss() != null
                && (form.getPointsWin() <= form.getPointsDraw() || form.getPointsDraw() < form.getPointsLoss())) {
            throw new IllegalArgumentException(
                    "El sistema de puntuación debe cumplir: puntos por victoria > empate >= derrota.");
        }
        Competition competition = id == null ? new Competition() : find(id);
        Season selectedSeason = seasons.findById(form.getSeasonId())
                .orElseThrow(() -> new IllegalArgumentException("Selecciona una temporada válida."));
        String trimmedName = form.getName().trim();
        boolean duplicate = competitions.existsBySeasonIdAndNameIgnoreCase(selectedSeason.getId(), trimmedName);
        if (duplicate && (id == null || !competition.getName().equalsIgnoreCase(trimmedName)
                || !competition.getSeason().getId().equals(selectedSeason.getId()))) {
            throw new IllegalArgumentException("Ya existe un torneo o etapa con ese nombre en la temporada seleccionada.");
        }
        if (form.getStartDate() != null && selectedSeason.getStartDate() != null
                && form.getStartDate().isBefore(selectedSeason.getStartDate())) {
            throw new IllegalArgumentException(
                    "La fecha de inicio del torneo no puede ser anterior al inicio de la temporada.");
        }
        if (form.getEndDate() != null && selectedSeason.getEndDate() != null
                && form.getEndDate().isAfter(selectedSeason.getEndDate())) {
            throw new IllegalArgumentException(
                    "La fecha de término del torneo no puede superar el cierre de la temporada.");
        }
        competition.setName(trimmedName);
        competition.setSeason(selectedSeason);
        competition.setCategory(form.getCategory().trim());
        competition.setStartDate(form.getStartDate());
        competition.setEndDate(form.getEndDate());
        competition.setPointsWin(form.getPointsWin());
        competition.setPointsDraw(form.getPointsDraw());
        competition.setPointsLoss(form.getPointsLoss());
        competition.setStatus(form.getStatus());
        Competition saved = competitions.save(competition);
        syncToFirestore(saved);
        audit.record(actor, id == null ? "COMPETITION_CREATED" : "COMPETITION_UPDATED",
                "COMPETITION", saved.getId(), saved.getName());
        return saved;
    }

    private Competition find(long id) {
        return competitions.findById(id).orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado."));
    }

    @SuppressWarnings("null")
    private void syncToFirestore(Competition competition) {
        if (firestore == null || competition == null || competition.getId() == null) {
            return;
        }
        try {
            Map<String, Object> doc = new LinkedHashMap<>();
            doc.put("id", competition.getId());
            doc.put("name", competition.getName());
            doc.put("seasonId", competition.getSeason() != null ? competition.getSeason().getId() : null);
            doc.put("category", competition.getCategory());
            doc.put("startDate", competition.getStartDate() != null ? competition.getStartDate().toString() : null);
            doc.put("endDate", competition.getEndDate() != null ? competition.getEndDate().toString() : null);
            doc.put("pointsWin", competition.getPointsWin());
            doc.put("pointsDraw", competition.getPointsDraw());
            doc.put("pointsLoss", competition.getPointsLoss());
            doc.put("status", competition.getStatus() != null ? competition.getStatus().name() : null);
            firestore.collection("competitions").document(java.util.Objects.requireNonNull(String.valueOf(competition.getId()))).set(doc);
        } catch (Exception exception) {
            LOGGER.warn("No se pudo sincronizar el torneo en Firestore.", exception);
        }
    }
}
