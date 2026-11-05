package pe.ligacopaperu.cuerpotecnico;

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

@Entity
@Table(name = "technical_staff")
public class TechnicalStaff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false, foreignKey = @ForeignKey(name = "fk_technical_staff_team"))
    private Team team;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "staff_role", nullable = false, length = 32)
    private StaffRole role;

    @Column(name = "contact_email", length = 254)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @Column(name = "identity_document_url", length = 255)
    private String identityDocumentUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StaffStatus status = StaffStatus.ACTIVE;

    public Long getId() { return id; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public StaffRole getRole() { return role; }
    public void setRole(StaffRole role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getIdentityDocumentUrl() { return identityDocumentUrl; }
    public void setIdentityDocumentUrl(String identityDocumentUrl) { this.identityDocumentUrl = identityDocumentUrl; }
    public StaffStatus getStatus() { return status; }
    public void setStatus(StaffStatus status) { this.status = status; }
    public String getFullName() { return firstName + " " + lastName; }
}
