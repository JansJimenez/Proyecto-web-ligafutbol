package pe.ligacopaperu.torneo;

import java.time.LocalDate;

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

@Entity
@Table(name = "competitions")
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "season_id", nullable = false, foreignKey = @ForeignKey(name = "fk_competitions_season"))
    private Season season;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "points_win", nullable = false)
    private Integer pointsWin = 3;

    @Column(name = "points_draw", nullable = false)
    private Integer pointsDraw = 1;

    @Column(name = "points_loss", nullable = false)
    private Integer pointsLoss = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CompetitionStatus status = CompetitionStatus.PLANNED;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Season getSeason() { return season; }
    public void setSeason(Season season) { this.season = season; }
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
