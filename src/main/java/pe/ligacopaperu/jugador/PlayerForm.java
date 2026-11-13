package pe.ligacopaperu.jugador;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class PlayerForm {
    @NotNull(message = "Selecciona el equipo en el que está inscrito el jugador.")
    private Long teamId;

    @NotBlank(message = "Ingresa los nombres del jugador.")
    @Size(max = 100, message = "Los nombres no deben superar 100 caracteres.")
    private String firstName;

    @NotBlank(message = "Ingresa los apellidos del jugador.")
    @Size(max = 100, message = "Los apellidos no deben superar 100 caracteres.")
    private String lastName;

    @NotNull(message = "Ingresa la fecha de nacimiento del jugador.")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy.")
    private LocalDate birthDate;

    @Min(value = 1, message = "El dorsal debe estar entre 1 y 99.")
    @Max(value = 99, message = "El dorsal debe estar entre 1 y 99.")
    private Integer jerseyNumber;

    @NotNull(message = "Selecciona la posición en el campo.")
    private PlayerPosition position = PlayerPosition.OTHER;

    @Size(max = 80, message = "La nacionalidad no debe superar 80 caracteres.")
    private String nationality = "Peruana";

    private MultipartFile photoFile;

    private MultipartFile identityDocumentFile;

    private boolean removePhoto;

    private boolean removeIdentityDocument;

    private String photoUrl;

    private String identityDocumentUrl;

    @NotNull(message = "Selecciona el estado de habilitación del jugador.")
    private PlayerStatus status = PlayerStatus.ACTIVE;

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
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
    public MultipartFile getPhotoFile() { return photoFile; }
    public void setPhotoFile(MultipartFile photoFile) { this.photoFile = photoFile; }
    public MultipartFile getIdentityDocumentFile() { return identityDocumentFile; }
    public void setIdentityDocumentFile(MultipartFile identityDocumentFile) { this.identityDocumentFile = identityDocumentFile; }
    public boolean isRemovePhoto() { return removePhoto; }
    public void setRemovePhoto(boolean removePhoto) { this.removePhoto = removePhoto; }
    public boolean isRemoveIdentityDocument() { return removeIdentityDocument; }
    public void setRemoveIdentityDocument(boolean removeIdentityDocument) { this.removeIdentityDocument = removeIdentityDocument; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getIdentityDocumentUrl() { return identityDocumentUrl; }
    public void setIdentityDocumentUrl(String identityDocumentUrl) { this.identityDocumentUrl = identityDocumentUrl; }
    public PlayerStatus getStatus() { return status; }
    public void setStatus(PlayerStatus status) { this.status = status; }
}
