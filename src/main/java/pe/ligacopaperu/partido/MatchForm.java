package pe.ligacopaperu.partido;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MatchForm {
    @NotNull(message = "Selecciona el torneo o etapa del partido.")
    private Long competitionId;

    @NotNull(message = "Selecciona el equipo local.")
    private Long homeTeamId;

    @NotNull(message = "Selecciona el equipo visitante.")
    private Long awayTeamId;

    @Min(value = 1, message = "El número de fecha o jornada debe ser al menos 1.")
    @Max(value = 100, message = "El número de fecha o jornada no puede superar 100.")
    private Integer roundNumber = 1;

    @NotNull(message = "Indica la fecha y hora programada del encuentro.")
    private LocalDateTime scheduledAt;

    @Size(max = 150, message = "El nombre del estadio o escenario no debe superar 150 caracteres.")
    private String stadium;

    @Min(value = 0, message = "Los goles del local no pueden ser negativos.")
    @Max(value = 50, message = "Los goles del local no pueden superar 50.")
    private Integer homeGoals;

    @Min(value = 0, message = "Los goles del visitante no pueden ser negativos.")
    @Max(value = 50, message = "Los goles del visitante no pueden superar 50.")
    private Integer awayGoals;

    @NotNull(message = "Selecciona el estado del encuentro.")
    private MatchStatus status = MatchStatus.SCHEDULED;

    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }
    public Long getHomeTeamId() { return homeTeamId; }
    public void setHomeTeamId(Long homeTeamId) { this.homeTeamId = homeTeamId; }
    public Long getAwayTeamId() { return awayTeamId; }
    public void setAwayTeamId(Long awayTeamId) { this.awayTeamId = awayTeamId; }
    public Integer getRoundNumber() { return roundNumber; }
    public void setRoundNumber(Integer roundNumber) { this.roundNumber = roundNumber; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    public String getStadium() { return stadium; }
    public void setStadium(String stadium) { this.stadium = stadium; }
    public Integer getHomeGoals() { return homeGoals; }
    public void setHomeGoals(Integer homeGoals) { this.homeGoals = homeGoals; }
    public Integer getAwayGoals() { return awayGoals; }
    public void setAwayGoals(Integer awayGoals) { this.awayGoals = awayGoals; }
    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }
}
