package pe.ligacopaperu.torneo;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CompetitionForm {
    @NotBlank
    @Size(max = 150)
    private String name;

    @NotNull
    private Long seasonId;

    @NotBlank
    @Size(max = 80)
    private String category;

    private LocalDate startDate;
    private LocalDate endDate;

    @NotNull
    @Min(0)
    private Integer pointsWin = 3;

    @NotNull
    @Min(0)
    private Integer pointsDraw = 1;

    @NotNull
    @Min(0)
    private Integer pointsLoss = 0;

    @NotNull
    private CompetitionStatus status = CompetitionStatus.PLANNED;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getSeasonId() { return seasonId; }
    public void setSeasonId(Long seasonId) { this.seasonId = seasonId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Integer getPointsWin() { return pointsWin; }
    public void setPointsWin(Integer pointsWin) { this.pointsWin = pointsWin; }
    public Integer getPointsDraw() { return pointsDraw; }
    public void setPointsDraw(Integer pointsDraw) { this.pointsDraw = pointsDraw; }
    public Integer getPointsLoss() { return pointsLoss; }
    public void setPointsLoss(Integer pointsLoss) { this.pointsLoss = pointsLoss; }
    public CompetitionStatus getStatus() { return status; }
    public void setStatus(CompetitionStatus status) { this.status = status; }
}
