package pe.ligacopaperu.torneo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    @Override
    @EntityGraph(attributePaths = "season")
    Optional<Competition> findById(Long id);

    @EntityGraph(attributePaths = "season")
    List<Competition> findAllByOrderByStartDateDescNameAsc();

    @Override
    @EntityGraph(attributePaths = "season")
    List<Competition> findAll(org.springframework.data.domain.Sort sort);

    boolean existsBySeasonIdAndNameIgnoreCase(Long seasonId, String name);
}
