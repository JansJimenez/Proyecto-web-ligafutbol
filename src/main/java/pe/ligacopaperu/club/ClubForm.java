package pe.ligacopaperu.club;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClubForm {

    @NotBlank
    @Size(max = 150)
    private String officialName;

    @Size(max = 80)
    private String shortName;

    @Size(max = 12)
    private String acronym;

    @Size(max = 2000)
    private String description;

    @Min(1800)
    @Max(2100)
    private Integer foundedYear;

    @Size(max = 80)
    private String country;

    @Size(max = 100)
    private String department;

    @Size(max = 100)
    private String province;

    @Size(max = 100)
    private String district;

    @Size(max = 150)
    private String stadium;

    @Size(max = 120)
    private String president;

    @Size(max = 120)
    private String delegateName;

    @Size(max = 40)
    private String phone;

    @Email
    @Size(max = 180)
    private String email;

    @Size(max = 255)
    private String website;

    @Size(max = 7)
    @Pattern(regexp = "^$|^#[0-9a-fA-F]{6}$")
    private String primaryColor;

    @Size(max = 7)
    @Pattern(regexp = "^$|^#[0-9a-fA-F]{6}$")
    private String secondaryColor;

    @NotNull
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
        form.setPrimaryColor(club.getPrimaryColor());
        form.setSecondaryColor(club.getSecondaryColor());
        form.setStatus(club.getStatus());
        return form;
    }
}
