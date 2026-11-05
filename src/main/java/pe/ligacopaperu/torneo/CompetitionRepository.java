package pe.ligacopaperu.torneo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    @EntityGraph(attributePaths = "season")
    List<Competition> findAllByOrderByStartDateDescNameAsc();

    @EntityGraph(attributePaths = "season")
    List<Competition> findAll(org.springframework.data.domain.Sort sort);
}
