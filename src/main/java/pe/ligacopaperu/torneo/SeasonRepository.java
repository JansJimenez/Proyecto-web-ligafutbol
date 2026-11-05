package pe.ligacopaperu.torneo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SeasonRepository extends JpaRepository<Season, Long> {
    List<Season> findAllByOrderByYearDescNameAsc();
    boolean existsByNameIgnoreCaseAndYear(String name, Integer year);
}
