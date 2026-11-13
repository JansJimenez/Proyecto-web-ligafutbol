package pe.ligacopaperu.torneo;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class SeasonForm {
    @NotBlank(message = "Ingresa el nombre de la temporada (ej. Copa Perú 2026).")
    @Size(max = 100, message = "El nombre de la temporada no debe superar 100 caracteres.")
    private String name;

    @NotNull(message = "Ingresa el año oficial de la temporada.")
    @Min(value = 1900, message = "El año debe ser igual o posterior a 1900.")
    @Max(value = 2200, message = "El año no puede superar 2200.")
    private Integer year = LocalDate.now().getYear();

    private LocalDate startDate;
    private LocalDate endDate;

    @NotNull(message = "Selecciona el estado de la temporada.")
    private SeasonStatus status = SeasonStatus.PLANNED;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public SeasonStatus getStatus() { return status; }
    public void setStatus(SeasonStatus status) { this.status = status; }
}
