package pe.ligacopaperu.equipo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface TeamRepository extends JpaRepository<Team, Long> {
    @EntityGraph(attributePaths = {"club", "competition", "competition.season"})
    List<Team> findAllByOrderByNameAsc();

    @Override
    @EntityGraph(attributePaths = {"club", "competition", "competition.season"})
    List<Team> findAll(org.springframework.data.domain.Sort sort);

    @EntityGraph(attributePaths = {"club", "competition", "competition.season"})
    List<Team> findByCompetitionIdAndStatusOrderByNameAsc(Long competitionId, TeamStatus status);
    boolean existsByCompetitionIdAndNameIgnoreCase(Long competitionId, String name);
}
