package pe.ligacopaperu.club;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Long> {
    List<Club> findAllByOrderByOfficialNameAsc();
    List<Club> findByStatusOrderByOfficialNameAsc(ClubStatus status);
    long countByStatus(ClubStatus status);
    boolean existsByAcronymIgnoreCase(String acronym);
}
