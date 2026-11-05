package pe.ligacopaperu.jugador;

public enum PlayerStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo");

    private final String label;

    PlayerStatus(String label) { this.label = label; }

    public String getLabel() { return label; }
}
