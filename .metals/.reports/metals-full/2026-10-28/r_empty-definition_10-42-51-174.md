error id: file:///C:/Users/jans7/OneDrive/Documentos/liga-copa-peru/src/main/java/pe/ligacopaperu/estadistica/StandingsService.java:pe/ligacopaperu/equipo/TeamRepository#
file:///C:/Users/jans7/OneDrive/Documentos/liga-copa-peru/src/main/java/pe/ligacopaperu/estadistica/StandingsService.java
empty definition using pc, found symbol in pc: pe/ligacopaperu/equipo/TeamRepository#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 364
uri: file:///C:/Users/jans7/OneDrive/Documentos/liga-copa-peru/src/main/java/pe/ligacopaperu/estadistica/StandingsService.java
text:
```scala
package pe.ligacopaperu.estadistica;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.ligacopaperu.equipo.Team;
import pe.ligacopaperu.equipo.@@TeamRepository;
import pe.ligacopaperu.partido.Match;
import pe.ligacopaperu.partido.MatchRepository;
import pe.ligacopaperu.partido.MatchStatus;

@Service
public class StandingsService {
    private final TeamRepository teams;
    private final MatchRepository matches;

    public StandingsService(TeamRepository teams, MatchRepository matches) {
        this.teams = teams;
        this.matches = matches;
    }

    @Transactional(readOnly = true)
    public List<StandingRow> table(long competitionId) {
        List<Team> participants = teams.findByCompetitionIdAndStatusOrderByNameAsc(
                competitionId, pe.ligacopaperu.equipo.TeamStatus.ACTIVE);
        Map<Long, MutableStanding> table = new LinkedHashMap<>();
        for (Team team : participants) {
            table.put(team.getId(), new MutableStanding(team));
        }
        List<Match> finalMatches = matches.findByCompetitionIdAndStatusOrderByScheduledAtAsc(
                competitionId, MatchStatus.FINALIZED);
        for (Match match : finalMatches) {
            if (match.getHomeGoals() == null || match.getAwayGoals() == null) {
                continue;
            }
            MutableStanding home = table.get(match.getHomeTeam().getId());
            MutableStanding away = table.get(match.getAwayTeam().getId());
            if (home == null || away == null) {
                continue;
            }
            home.played++;
            away.played++;
            home.goalsFor += match.getHomeGoals();
            home.goalsAgainst += match.getAwayGoals();
            away.goalsFor += match.getAwayGoals();
            away.goalsAgainst += match.getHomeGoals();
            if (match.getHomeGoals() > match.getAwayGoals()) {
                home.won++;
                away.lost++;
                home.points += match.getCompetition().getPointsWin();
                away.points += match.getCompetition().getPointsLoss();
            } else if (match.getHomeGoals() < match.getAwayGoals()) {
                away.won++;
                home.lost++;
                away.points += match.getCompetition().getPointsWin();
                home.points += match.getCompetition().getPointsLoss();
            } else {
                home.drawn++;
                away.drawn++;
                home.points += match.getCompetition().getPointsDraw();
                away.points += match.getCompetition().getPointsDraw();
            }
        }
        return table.values().stream()
                .sorted(Comparator.comparingInt((MutableStanding row) -> row.points).reversed()
                        .thenComparing(Comparator.comparingInt(
                                (MutableStanding row) -> row.goalsFor - row.goalsAgainst).reversed())
                        .thenComparing(Comparator.comparingInt(
                                (MutableStanding row) -> row.goalsFor).reversed())
                        .thenComparing(row -> row.team.getName(), String.CASE_INSENSITIVE_ORDER))
                .map(MutableStanding::toRow)
                .toList();
    }

    private static class MutableStanding {
        private final Team team;
        private int played;
        private int won;
        private int drawn;
        private int lost;
        private int goalsFor;
        private int goalsAgainst;
        private int points;

        private MutableStanding(Team team) {
            this.team = team;
        }

        private StandingRow toRow() {
            return new StandingRow(team.getId(), team.getName(), team.getClub().getOfficialName(),
                    played, won, drawn, lost, goalsFor, goalsAgainst, points);
        }
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: pe/ligacopaperu/equipo/TeamRepository#