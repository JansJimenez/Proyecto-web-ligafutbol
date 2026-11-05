package pe.ligacopaperu.equipo;

public enum TeamStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo");

    private final String label;

    TeamStatus(String label) { this.label = label; }

    public String getLabel() { return label; }
}
