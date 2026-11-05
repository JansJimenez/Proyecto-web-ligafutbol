package pe.ligacopaperu.jugador;

public enum PlayerPosition {
    GOALKEEPER("Portero"),
    CENTRAL_DEFENDER("Defensa central"),
    RIGHT_BACK("Lateral derecho"),
    LEFT_BACK("Lateral izquierdo"),
    DEFENSIVE_MIDFIELDER("Mediocampista defensivo"),
    CENTRAL_MIDFIELDER("Mediocampista central"),
    ATTACKING_MIDFIELDER("Mediocampista ofensivo"),
    RIGHT_WINGER("Extremo derecho"),
    LEFT_WINGER("Extremo izquierdo"),
    STRIKER("Delantero centro"),
    OTHER("Otra posición");

    private final String label;

    PlayerPosition(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }
}
