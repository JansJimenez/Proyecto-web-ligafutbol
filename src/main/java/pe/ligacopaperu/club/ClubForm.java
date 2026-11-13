package pe.ligacopaperu.club;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClubForm {

    @NotBlank(message = "Ingresa el nombre oficial del club.")
    @Size(max = 150, message = "El nombre oficial no debe superar 150 caracteres.")
    private String officialName;

    @Size(max = 80, message = "El nombre corto no debe superar 80 caracteres.")
    private String shortName;

    @Size(max = 12, message = "La sigla no debe superar 12 caracteres.")
    private String acronym;

    @Size(max = 2000, message = "La reseña histórica no debe superar 2000 caracteres.")
    private String description;

    @Min(value = 1800, message = "El año de fundación debe ser igual o posterior a 1800.")
    @Max(value = 2100, message = "El año de fundación no puede superar 2100.")
    private Integer foundedYear;

    @Size(max = 80, message = "El país no debe superar 80 caracteres.")
    private String country = "Perú";

    @Size(max = 100, message = "El departamento no debe superar 100 caracteres.")
    private String department = "Cajamarca";

    @Size(max = 100, message = "La provincia no debe superar 100 caracteres.")
    private String province = "San Marcos";

    @Size(max = 100, message = "El distrito no debe superar 100 caracteres.")
    private String district = "Pedro Gálvez";

    @Size(max = 150, message = "El estadio no debe superar 150 caracteres.")
    private String stadium;

    @Size(max = 120, message = "El nombre del presidente no debe superar 120 caracteres.")
    private String president;

    @Size(max = 120, message = "El nombre del delegado ante la liga no debe superar 120 caracteres.")
    private String delegateName;

    @Size(max = 40, message = "El teléfono no debe superar 40 caracteres.")
    @Pattern(regexp = "^$|^[0-9+()\\-\\s]{6,25}$", message = "Ingresa un número telefónico válido (6 a 25 dígitos).")
    private String phone;

    @Email(message = "Ingresa un correo electrónico institucional válido.")
    @Size(max = 180, message = "El correo no debe superar 180 caracteres.")
    private String email;

    @Size(max = 255, message = "El sitio web no debe superar 255 caracteres.")
    @Pattern(regexp = "^$|^https?://.+", message = "El sitio web debe iniciar con http:// o https://.")
    private String website;

    @Size(max = 7)
    @Pattern(regexp = "^$|^#[0-9a-fA-F]{6}$", message = "Usa formato hexadecimal (#RRGGBB), por ejemplo #df1732.")
    private String primaryColor = "#df1732";

    @Size(max = 7)
    @Pattern(regexp = "^$|^#[0-9a-fA-F]{6}$", message = "Usa formato hexadecimal (#RRGGBB), por ejemplo #ffffff.")
    private String secondaryColor = "#ffffff";

    @NotNull(message = "Selecciona el estado del club.")
    private ClubStatus status = ClubStatus.ACTIVE;

    public String getOfficialName() { return officialName; }
    public void setOfficialName(String officialName) { this.officialName = officialName; }
    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }
    public String getAcronym() { return acronym; }
    public void setAcronym(String acronym) { this.acronym = acronym; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getFoundedYear() { return foundedYear; }
    public void setFoundedYear(Integer foundedYear) { this.foundedYear = foundedYear; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getStadium() { return stadium; }
    public void setStadium(String stadium) { this.stadium = stadium; }
    public String getPresident() { return president; }
    public void setPresident(String president) { this.president = president; }
    public String getDelegateName() { return delegateName; }
    public void setDelegateName(String delegateName) { this.delegateName = delegateName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }
    public String getSecondaryColor() { return secondaryColor; }
    public void setSecondaryColor(String secondaryColor) { this.secondaryColor = secondaryColor; }
    public ClubStatus getStatus() { return status; }
    public void setStatus(ClubStatus status) { this.status = status; }

    static ClubForm from(Club club) {
        ClubForm form = new ClubForm();
        form.setOfficialName(club.getOfficialName());
        form.setShortName(club.getShortName());
        form.setAcronym(club.getAcronym());
        form.setDescription(club.getDescription());
        form.setFoundedYear(club.getFoundedYear());
        form.setCountry(club.getCountry());
        form.setDepartment(club.getDepartment());
        form.setProvince(club.getProvince());
        form.setDistrict(club.getDistrict());
        form.setStadium(club.getStadium());
        form.setPresident(club.getPresident());
        form.setDelegateName(club.getDelegateName());
        form.setPhone(club.getPhone());
        form.setEmail(club.getEmail());
        form.setWebsite(club.getWebsite());
        form.setPrimaryColor(club.getPrimaryColor() != null ? club.getPrimaryColor() : "#df1732");
        form.setSecondaryColor(club.getSecondaryColor() != null ? club.getSecondaryColor() : "#ffffff");
        form.setStatus(club.getStatus() != null ? club.getStatus() : ClubStatus.ACTIVE);
        return form;
    }
}
