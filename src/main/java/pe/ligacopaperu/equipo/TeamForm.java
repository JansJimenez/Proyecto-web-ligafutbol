package pe.ligacopaperu.equipo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class TeamForm {
    @NotNull
    private Long clubId;

    @NotNull
    private Long competitionId;

    @NotBlank
    @Size(max = 120)
    private String name;

    @Size(max = 60)
    private String shortName;

    @Size(max = 60)
    private String groupName;

    @Size(max = 150)
    private String stadium;

    private MultipartFile crestFile;

    private boolean removeCrest;

    private String crestUrl;

    @NotNull
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
