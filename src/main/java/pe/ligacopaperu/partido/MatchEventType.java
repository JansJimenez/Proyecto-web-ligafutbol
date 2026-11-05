package pe.ligacopaperu.partido;

public enum MatchEventType {
    GOAL("Gol"),
    OWN_GOAL("Autogol"),
    ASSIST("Asistencia"),
    YELLOW_CARD("Tarjeta amarilla"),
    RED_CARD("Tarjeta roja"),
    SUBSTITUTION("Sustitución");

    private final String label;

    MatchEventType(String label) { this.label = label; }

    public String getLabel() { return label; }
}
