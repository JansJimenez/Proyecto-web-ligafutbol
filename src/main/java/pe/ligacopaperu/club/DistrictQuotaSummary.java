package pe.ligacopaperu.club;

public record DistrictQuotaSummary(
        String districtName,
        String capitalName,
        String stadiumName,
        long activeClubs,
        int minimumRequired) {

    public boolean isComplete() {
        return activeClubs >= minimumRequired;
    }

    public long missingClubs() {
        return Math.max(0, minimumRequired - activeClubs);
    }

    public int progressPercent() {
        if (minimumRequired <= 0) {
            return 100;
        }
        return (int) Math.min(100, Math.round((activeClubs * 100.0) / minimumRequired));
    }
}
