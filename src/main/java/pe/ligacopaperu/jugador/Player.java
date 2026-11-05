package pe.ligacopaperu.jugador;

import java.time.LocalDate;

import pe.ligacopaperu.equipo.Team;

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
@Table(name = "players", uniqueConstraints = @UniqueConstraint(
        name = "uq_player_team_jersey", columnNames = {"team_id", "jersey_number"}))
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false, foreignKey = @ForeignKey(name = "fk_players_team"))
    private Team team;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "jersey_number")
    private Integer jerseyNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PlayerPosition position = PlayerPosition.OTHER;

    @Column(length = 80)
    private String nationality;

    @Column(length = 255)
    private String photoUrl;

    @Column(name = "identity_document_url", length = 255)
    private String identityDocumentUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlayerStatus status = PlayerStatus.ACTIVE;

    public Long getId() { return id; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public Integer getJerseyNumber() { return jerseyNumber; }
    public void setJerseyNumber(Integer jerseyNumber) { this.jerseyNumber = jerseyNumber; }
    public PlayerPosition getPosition() { return position; }
    public void setPosition(PlayerPosition position) { this.position = position; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getIdentityDocumentUrl() { return identityDocumentUrl; }
    public void setIdentityDocumentUrl(String identityDocumentUrl) { this.identityDocumentUrl = identityDocumentUrl; }
    public PlayerStatus getStatus() { return status; }
    public void setStatus(PlayerStatus status) { this.status = status; }
    public String getFullName() { return firstName + " " + lastName; }
}
