package pe.ligacopaperu.partido;

public enum MatchStatus {
    SCHEDULED("Programado"),
    FINALIZED("Finalizado"),
    POSTPONED("Reprogramado"),
    CANCELLED("Cancelado");

    private final String label;

    MatchStatus(String label) { this.label = label; }

    public String getLabel() { return label; }
}
