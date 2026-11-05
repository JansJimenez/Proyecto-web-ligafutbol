package pe.ligacopaperu.jugador;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    @EntityGraph(attributePaths = {"team", "team.club", "team.competition", "team.competition.season"})
    List<Player> findAllByOrderByLastNameAscFirstNameAsc();

    @EntityGraph(attributePaths = {"team", "team.club", "team.competition", "team.competition.season"})
    List<Player> findByStatusOrderByLastNameAscFirstNameAsc(PlayerStatus status);

    boolean existsByTeamIdAndJerseyNumberAndIdNot(Long teamId, Integer jerseyNumber, Long id);
    boolean existsByTeamIdAndJerseyNumber(Long teamId, Integer jerseyNumber);

    @EntityGraph(attributePaths = {"team", "team.club", "team.competition", "team.competition.season"})
    List<Player> findByTeamIdInAndStatusOrderByLastNameAscFirstNameAsc(List<Long> teamIds, PlayerStatus status);
}
