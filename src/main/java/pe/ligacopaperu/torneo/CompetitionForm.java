package pe.ligacopaperu.torneo;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CompetitionForm {
    @NotBlank(message = "Ingresa el nombre oficial del torneo o etapa.")
    @Size(max = 150, message = "El nombre del torneo no debe superar 150 caracteres.")
    private String name;

    @NotNull(message = "Selecciona la temporada a la que pertenece el torneo.")
    private Long seasonId;

    @NotBlank(message = "Indica la etapa o categoría (ej. Etapa Distrital, Etapa Provincial).")
    @Size(max = 80, message = "La categoría o etapa no debe superar 80 caracteres.")
    private String category = "Etapa Distrital · Primera División";

    private LocalDate startDate;
    private LocalDate endDate;

    @NotNull(message = "Indica los puntos por partido ganado.")
    @Min(value = 0, message = "Los puntos por victoria no pueden ser negativos.")
    private Integer pointsWin = 3;

    @NotNull(message = "Indica los puntos por partido empatado.")
    @Min(value = 0, message = "Los puntos por empate no pueden ser negativos.")
    private Integer pointsDraw = 1;

    @NotNull(message = "Indica los puntos por partido perdido.")
    @Min(value = 0, message = "Los puntos por derrota no pueden ser negativos.")
    private Integer pointsLoss = 0;

    @NotNull(message = "Selecciona el estado del torneo.")
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
