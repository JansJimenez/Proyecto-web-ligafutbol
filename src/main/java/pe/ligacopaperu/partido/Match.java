package pe.ligacopaperu.partido;

import java.time.LocalDateTime;

import pe.ligacopaperu.equipo.Team;
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
@Table(name = "matches", uniqueConstraints = @UniqueConstraint(
        name = "uq_match_competition_round", columnNames = {"competition_id", "round_number", "home_team_id", "away_team_id"}))
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competition_id", nullable = false, foreignKey = @ForeignKey(name = "fk_matches_competition"))
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "home_team_id", nullable = false, foreignKey = @ForeignKey(name = "fk_matches_home_team"))
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "away_team_id", nullable = false, foreignKey = @ForeignKey(name = "fk_matches_away_team"))
    private Team awayTeam;

    @Column(name = "round_number")
    private Integer roundNumber;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(length = 150)
    private String stadium;

    @Column(name = "home_goals")
    private Integer homeGoals;

    @Column(name = "away_goals")
    private Integer awayGoals;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MatchStatus status = MatchStatus.SCHEDULED;

    public Long getId() { return id; }
    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }
    public Team getHomeTeam() { return homeTeam; }
    public void setHomeTeam(Team homeTeam) { this.homeTeam = homeTeam; }
    public Team getAwayTeam() { return awayTeam; }
    public void setAwayTeam(Team awayTeam) { this.awayTeam = awayTeam; }
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
