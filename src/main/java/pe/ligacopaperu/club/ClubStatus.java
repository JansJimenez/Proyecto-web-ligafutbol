package pe.ligacopaperu.club;

public enum ClubStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo");

    private final String label;

    ClubStatus(String label) { this.label = label; }

    public String getLabel() { return label; }
}
