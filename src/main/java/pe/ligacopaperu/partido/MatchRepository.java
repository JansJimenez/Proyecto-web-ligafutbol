package pe.ligacopaperu.partido;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @Override
    @EntityGraph(attributePaths = {"competition", "competition.season", "homeTeam", "homeTeam.club",
            "awayTeam", "awayTeam.club"})
    Optional<Match> findById(Long id);

    @EntityGraph(attributePaths = {"competition", "competition.season", "homeTeam", "homeTeam.club",
            "awayTeam", "awayTeam.club"})
    List<Match> findAllByOrderByScheduledAtAsc();

    @EntityGraph(attributePaths = {"competition", "competition.season", "homeTeam", "homeTeam.club",
            "awayTeam", "awayTeam.club"})
    List<Match> findByCompetitionIdAndStatusOrderByScheduledAtAsc(Long competitionId, MatchStatus status);
}
