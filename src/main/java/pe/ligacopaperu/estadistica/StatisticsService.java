package pe.ligacopaperu.estadistica;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.ligacopaperu.partido.MatchEvent;
import pe.ligacopaperu.partido.MatchEventRepository;
import pe.ligacopaperu.partido.MatchEventType;
import pe.ligacopaperu.partido.MatchStatus;

@Service
public class StatisticsService {
    private final MatchEventRepository events;

    public StatisticsService(MatchEventRepository events) {
        this.events = events;
    }

    @Transactional(readOnly = true)
    public List<ScorerRow> playerStatistics() {
        Map<String, MutableScorer> statistics = new LinkedHashMap<>();
        for (MatchEvent event : events.findAllByOrderByMinuteAsc()) {
            if (event.getMatch().getStatus() != MatchStatus.FINALIZED) {
                continue;
            }
            String key = event.getPlayer().getId() + ":" + event.getTeam().getId();
            MutableScorer row = statistics.computeIfAbsent(key, ignored -> new MutableScorer(
                    event.getPlayer().getFullName(), event.getTeam().getName(),
                    event.getTeam().getClub().getOfficialName()));
            switch (event.getType()) {
                case GOAL -> row.goals++;
                case ASSIST -> row.assists++;
                case YELLOW_CARD -> row.yellowCards++;
                case RED_CARD -> row.redCards++;
                case OWN_GOAL, SUBSTITUTION -> {
                }
            }
        }
        return statistics.values().stream()
                .sorted(Comparator.comparingInt((MutableScorer row) -> row.goals).reversed()
                        .thenComparing(Comparator.comparingInt(
                                (MutableScorer row) -> row.assists).reversed())
                        .thenComparing(row -> row.playerName, String.CASE_INSENSITIVE_ORDER))
                .map(MutableScorer::toRow)
                .toList();
    }

    private static class MutableScorer {
        private final String playerName;
        private final String teamName;
        private final String clubName;
        private int goals;
        private int assists;
        private int yellowCards;
        private int redCards;

        private MutableScorer(String playerName, String teamName, String clubName) {
            this.playerName = playerName;
            this.teamName = teamName;
            this.clubName = clubName;
        }

        private ScorerRow toRow() {
            return new ScorerRow(playerName, teamName, clubName, goals, assists, yellowCards, redCards);
        }
    }
}
