package pe.ligacopaperu.jugador;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    @EntityGraph(attributePaths = {"team", "team.club", "team.competition", "team.competition.season"})
    List<Player> findAllByOrderByLastNameAscFirstNameAsc();

    @EntityGraph(attributePaths = {"team", "team.club", "team.competition", "team.competition.season"})
    List<Player> findByStatusOrderByLastNameAscFirstNameAsc(PlayerStatus status);

    @EntityGraph(attributePaths = {"team", "team.club", "team.competition", "team.competition.season"})
    List<Player> findByStatusAndTeamStatusAndTeamClubStatusOrderByLastNameAscFirstNameAsc(
            PlayerStatus status, pe.ligacopaperu.equipo.TeamStatus teamStatus, pe.ligacopaperu.club.ClubStatus clubStatus);

    @EntityGraph(attributePaths = {"team", "team.club", "team.competition", "team.competition.season"})
    List<Player> findByTeamCompetitionIdAndStatusAndTeamStatusAndTeamClubStatusOrderByLastNameAscFirstNameAsc(
            Long competitionId, PlayerStatus status, pe.ligacopaperu.equipo.TeamStatus teamStatus,
            pe.ligacopaperu.club.ClubStatus clubStatus);

    boolean existsByTeamIdAndJerseyNumberAndIdNot(Long teamId, Integer jerseyNumber, Long id);
    boolean existsByTeamIdAndJerseyNumber(Long teamId, Integer jerseyNumber);

    @EntityGraph(attributePaths = {"team", "team.club", "team.competition", "team.competition.season"})
    List<Player> findByTeamIdInAndStatusOrderByLastNameAscFirstNameAsc(List<Long> teamIds, PlayerStatus status);

    long countByStatus(PlayerStatus status);
}
