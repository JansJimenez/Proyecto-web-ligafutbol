package pe.ligacopaperu.estadistica;

public record ScorerRow(
        String playerName,
        String teamName,
        String clubName,
        int goals,
        int assists,
        int yellowCards,
        int redCards) {
}
