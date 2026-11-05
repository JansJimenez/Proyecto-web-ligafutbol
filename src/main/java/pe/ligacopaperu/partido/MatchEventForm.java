package pe.ligacopaperu.partido;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MatchEventForm {
    @NotNull
    private Long teamId;

    @NotNull
    private Long playerId;

    private Long relatedPlayerId;

    @NotNull
    @Min(0)
    @Max(150)
    private Integer minute;

    @NotNull
    private MatchEventType type;

    @Size(max = 500)
    private String notes;

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public Long getRelatedPlayerId() { return relatedPlayerId; }
    public void setRelatedPlayerId(Long relatedPlayerId) { this.relatedPlayerId = relatedPlayerId; }
    public Integer getMinute() { return minute; }
    public void setMinute(Integer minute) { this.minute = minute; }
    public MatchEventType getType() { return type; }
    public void setType(MatchEventType type) { this.type = type; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
