package pe.ligacopaperu.cuerpotecnico;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnicalStaffRepository extends JpaRepository<TechnicalStaff, Long> {
    @EntityGraph(attributePaths = {"team", "team.club", "team.competition"})
    List<TechnicalStaff> findAllByOrderByLastNameAscFirstNameAsc();

    @EntityGraph(attributePaths = {"team", "team.club", "team.competition"})
    List<TechnicalStaff> findByStatusOrderByLastNameAscFirstNameAsc(StaffStatus status);
}
