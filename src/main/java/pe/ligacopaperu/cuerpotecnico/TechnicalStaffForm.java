package pe.ligacopaperu.cuerpotecnico;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class TechnicalStaffForm {
    @NotNull(message = "Selecciona el equipo al que pertenece el integrante.")
    private Long teamId;

    @NotBlank(message = "Ingresa los nombres del integrante del comando técnico.")
    @Size(max = 100, message = "Los nombres no deben superar 100 caracteres.")
    private String firstName;

    @NotBlank(message = "Ingresa los apellidos del integrante del comando técnico.")
    @Size(max = 100, message = "Los apellidos no deben superar 100 caracteres.")
    private String lastName;

    @NotNull(message = "Selecciona el cargo dentro del comando técnico.")
    private StaffRole role = StaffRole.OTHER;

    @Email(message = "Ingresa un correo electrónico válido.")
    @Size(max = 254, message = "El correo no debe superar 254 caracteres.")
    private String email;

    @Size(max = 30, message = "El teléfono no debe superar 30 caracteres.")
    @Pattern(regexp = "^$|^[0-9+()\\-\\s]{6,25}$", message = "Ingresa un teléfono válido (6 a 25 dígitos).")
    private String phone;

    private MultipartFile photoFile;

    private MultipartFile identityDocumentFile;

    private boolean removePhoto;

    private boolean removeIdentityDocument;

    private String photoUrl;

    private String identityDocumentUrl;

    @NotNull(message = "Selecciona el estado de acreditación.")
    private StaffStatus status = StaffStatus.ACTIVE;

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
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
    public StaffStatus getStatus() { return status; }
    public void setStatus(StaffStatus status) { this.status = status; }
}
