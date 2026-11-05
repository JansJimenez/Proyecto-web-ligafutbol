package pe.ligacopaperu.torneo;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class SeasonForm {
    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    @Min(1900)
    @Max(2200)
    private Integer year;

    private LocalDate startDate;
    private LocalDate endDate;

    @NotNull
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
