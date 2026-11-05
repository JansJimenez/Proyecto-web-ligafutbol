package pe.ligacopaperu.partido;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchEventRepository extends JpaRepository<MatchEvent, Long> {
    @EntityGraph(attributePaths = {"team", "team.club", "player", "relatedPlayer"})
    List<MatchEvent> findByMatchIdOrderByMinuteAscIdAsc(Long matchId);

    @EntityGraph(attributePaths = {"match", "team", "team.club", "player"})
    List<MatchEvent> findAllByOrderByMinuteAsc();

    long countByMatchId(Long matchId);
}
