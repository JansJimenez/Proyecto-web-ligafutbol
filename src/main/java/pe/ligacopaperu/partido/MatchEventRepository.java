package pe.ligacopaperu.partido;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchEventRepository extends JpaRepository<MatchEvent, Long> {
    @EntityGraph(attributePaths = {"team", "team.club", "player", "relatedPlayer"})
    List<MatchEvent> findByMatchIdOrderByMinuteAscIdAsc(Long matchId);

    @EntityGraph(attributePaths = {"match", "team", "team.club", "player"})
    List<MatchEvent> findAllByOrderByMinuteAsc();

    @EntityGraph(attributePaths = {"match", "team", "team.club", "player"})
    List<MatchEvent> findByMatchStatusOrderByMinuteAsc(MatchStatus status);

    @EntityGraph(attributePaths = {"match", "team", "team.club", "player"})
    List<MatchEvent> findByMatchCompetitionIdAndMatchStatusOrderByMinuteAsc(Long competitionId, MatchStatus status);

    boolean existsByMatchIdAndPlayerIdAndType(Long matchId, Long playerId, MatchEventType type);

    long countByMatchId(Long matchId);
}
