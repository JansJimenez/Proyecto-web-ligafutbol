package pe.ligacopaperu.torneo;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.ligacopaperu.auditoria.AuditService;

@Service
public class CompetitionService {
    private final CompetitionRepository competitions;
    private final SeasonRepository seasons;
    private final AuditService audit;

    public CompetitionService(CompetitionRepository competitions, SeasonRepository seasons, AuditService audit) {
        this.competitions = competitions;
        this.seasons = seasons;
        this.audit = audit;
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
        Competition competition = id == null ? new Competition() : find(id);
        Season selectedSeason = seasons.findById(form.getSeasonId())
                .orElseThrow(() -> new IllegalArgumentException("Selecciona una temporada válida."));
        competition.setName(form.getName().trim());
        competition.setSeason(selectedSeason);
        competition.setCategory(form.getCategory().trim());
        competition.setStartDate(form.getStartDate());
        competition.setEndDate(form.getEndDate());
        competition.setPointsWin(form.getPointsWin());
        competition.setPointsDraw(form.getPointsDraw());
        competition.setPointsLoss(form.getPointsLoss());
        competition.setStatus(form.getStatus());
        Competition saved = competitions.save(competition);
        audit.record(actor, id == null ? "COMPETITION_CREATED" : "COMPETITION_UPDATED",
                "COMPETITION", saved.getId(), saved.getName());
        return saved;
    }

    private Competition find(long id) {
        return competitions.findById(id).orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado."));
    }
}
