package pe.ligacopaperu.partido;

import pe.ligacopaperu.equipo.Team;
import pe.ligacopaperu.jugador.Player;

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
@Table(name = "match_events")
public class MatchEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false, foreignKey = @ForeignKey(name = "fk_match_events_match"))
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false, foreignKey = @ForeignKey(name = "fk_match_events_team"))
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false, foreignKey = @ForeignKey(name = "fk_match_events_player"))
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_player_id", foreignKey = @ForeignKey(name = "fk_match_events_related_player"))
    private Player relatedPlayer;

    @Column(name = "event_minute", nullable = false)
    private Integer minute;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 20)
    private MatchEventType type;

    @Column(length = 500)
    private String notes;

    public Long getId() { return id; }
    public Match getMatch() { return match; }
    public void setMatch(Match match) { this.match = match; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
    public Player getRelatedPlayer() { return relatedPlayer; }
    public void setRelatedPlayer(Player relatedPlayer) { this.relatedPlayer = relatedPlayer; }
    public Integer getMinute() { return minute; }
    public void setMinute(Integer minute) { this.minute = minute; }
    public MatchEventType getType() { return type; }
    public void setType(MatchEventType type) { this.type = type; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
