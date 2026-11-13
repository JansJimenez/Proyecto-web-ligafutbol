package pe.ligacopaperu.club;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Long> {
    List<Club> findAllByOrderByOfficialNameAsc();
    List<Club> findByStatusOrderByOfficialNameAsc(ClubStatus status);
    List<Club> findByStatusAndDistrictIgnoreCaseOrderByOfficialNameAsc(ClubStatus status, String district);
    long countByStatus(ClubStatus status);
    long countByStatusAndDistrictIgnoreCase(ClubStatus status, String district);
    boolean existsByAcronymIgnoreCase(String acronym);
    boolean existsByOfficialNameIgnoreCase(String officialName);
}
