package pe.ligacopaperu.equipo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class TeamForm {
    @NotNull(message = "Selecciona el club al que representa el equipo.")
    private Long clubId;

    @NotNull(message = "Selecciona el torneo o etapa en la que compite.")
    private Long competitionId;

    @NotBlank(message = "Ingresa el nombre competitivo del equipo.")
    @Size(max = 120, message = "El nombre del equipo no debe superar 120 caracteres.")
    private String name;

    @Size(max = 60, message = "El nombre corto no debe superar 60 caracteres.")
    private String shortName;

    @Size(max = 60, message = "El grupo o serie no debe superar 60 caracteres.")
    private String groupName;

    @Size(max = 150, message = "El estadio local no debe superar 150 caracteres.")
    private String stadium;

    private MultipartFile crestFile;

    private boolean removeCrest;

    private String crestUrl;

    @NotNull(message = "Selecciona el estado del equipo.")
    private TeamStatus status = TeamStatus.ACTIVE;

    public Long getClubId() { return clubId; }
    public void setClubId(Long clubId) { this.clubId = clubId; }
    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getStadium() { return stadium; }
    public void setStadium(String stadium) { this.stadium = stadium; }
    public MultipartFile getCrestFile() { return crestFile; }
    public void setCrestFile(MultipartFile crestFile) { this.crestFile = crestFile; }
    public boolean isRemoveCrest() { return removeCrest; }
    public void setRemoveCrest(boolean removeCrest) { this.removeCrest = removeCrest; }
    public String getCrestUrl() { return crestUrl; }
    public void setCrestUrl(String crestUrl) { this.crestUrl = crestUrl; }
    public TeamStatus getStatus() { return status; }
    public void setStatus(TeamStatus status) { this.status = status; }
}
