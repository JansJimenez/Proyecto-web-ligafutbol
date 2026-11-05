package pe.ligacopaperu.equipo;

import pe.ligacopaperu.club.Club;
import pe.ligacopaperu.torneo.Competition;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "teams", uniqueConstraints = @UniqueConstraint(
        name = "uq_team_competition_name", columnNames = {"competition_id", "name"}))
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_id", nullable = false, foreignKey = @ForeignKey(name = "fk_teams_club"))
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competition_id", nullable = false, foreignKey = @ForeignKey(name = "fk_teams_competition"))
    private Competition competition;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "short_name", length = 60)
    private String shortName;

    @Column(name = "group_name", length = 60)
    private String groupName;

    @Column(length = 150)
    private String stadium;

    @Column(name = "crest_url", length = 255)
    private String crestUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TeamStatus status = TeamStatus.ACTIVE;

    public Long getId() { return id; }
    public Club getClub() { return club; }
    public void setClub(Club club) { this.club = club; }
    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getStadium() { return stadium; }
    public void setStadium(String stadium) { this.stadium = stadium; }
    public String getCrestUrl() { return crestUrl; }
    public void setCrestUrl(String crestUrl) { this.crestUrl = crestUrl; }
    public TeamStatus getStatus() { return status; }
    public void setStatus(TeamStatus status) { this.status = status; }
}
