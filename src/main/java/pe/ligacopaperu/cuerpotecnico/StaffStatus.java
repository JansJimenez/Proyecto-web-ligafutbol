package pe.ligacopaperu.cuerpotecnico;

public enum StaffStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo");

    private final String label;

    StaffStatus(String label) { this.label = label; }

    public String getLabel() { return label; }
}
