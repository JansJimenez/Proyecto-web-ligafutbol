package pe.ligacopaperu.torneo;

public enum SeasonStatus {
    PLANNED("Planificada"),
    ACTIVE("En curso"),
    CLOSED("Cerrada");

    private final String label;

    SeasonStatus(String label) { this.label = label; }

    public String getLabel() { return label; }
}
