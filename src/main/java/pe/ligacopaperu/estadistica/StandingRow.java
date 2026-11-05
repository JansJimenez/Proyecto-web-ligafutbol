package pe.ligacopaperu.estadistica;

public record StandingRow(
        Long teamId,
        String teamName,
        String clubName,
        int played,
        int won,
        int drawn,
        int lost,
        int goalsFor,
        int goalsAgainst,
        int points) {

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }
}
