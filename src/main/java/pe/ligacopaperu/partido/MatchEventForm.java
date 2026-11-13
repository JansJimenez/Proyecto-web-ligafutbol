package pe.ligacopaperu.partido;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MatchEventForm {
    @NotNull(message = "Selecciona el equipo al que corresponde la incidencia.")
    private Long teamId;

    @NotNull(message = "Selecciona el jugador protagonista del evento.")
    private Long playerId;

    private Long relatedPlayerId;

    @NotNull(message = "Ingresa el minuto oficial del evento.")
    @Min(value = 0, message = "El minuto debe estar entre 0 y 150.")
    @Max(value = 150, message = "El minuto debe estar entre 0 y 150.")
    private Integer minute;

    @NotNull(message = "Selecciona el tipo de incidencia para el acta.")
    private MatchEventType type;

    @Size(max = 500, message = "Las observaciones del árbitro o mesa no deben superar 500 caracteres.")
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
