package pe.ligacopaperu.partido;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MatchForm {
    @NotNull
    private Long competitionId;

    @NotNull
    private Long homeTeamId;

    @NotNull
    private Long awayTeamId;

    @Min(1)
    private Integer roundNumber;

    @NotNull
    private LocalDateTime scheduledAt;

    @Size(max = 150)
    private String stadium;

    @Min(0)
    private Integer homeGoals;

    @Min(0)
    private Integer awayGoals;

    @NotNull
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
