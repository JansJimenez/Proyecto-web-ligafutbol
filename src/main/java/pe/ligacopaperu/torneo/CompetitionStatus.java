package pe.ligacopaperu.torneo;

public enum CompetitionStatus {
    PLANNED("Planificado"),
    ACTIVE("En curso"),
    CLOSED("Cerrado");

    private final String label;

    CompetitionStatus(String label) { this.label = label; }

    public String getLabel() { return label; }
}
