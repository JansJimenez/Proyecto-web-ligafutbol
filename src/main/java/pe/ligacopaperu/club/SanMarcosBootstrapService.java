package pe.ligacopaperu.club;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.ligacopaperu.equipo.TeamForm;
import pe.ligacopaperu.equipo.TeamRepository;
import pe.ligacopaperu.equipo.TeamService;
import pe.ligacopaperu.equipo.TeamStatus;
import pe.ligacopaperu.torneo.Competition;
import pe.ligacopaperu.torneo.CompetitionForm;
import pe.ligacopaperu.torneo.CompetitionRepository;
import pe.ligacopaperu.torneo.CompetitionService;
import pe.ligacopaperu.torneo.CompetitionStatus;
import pe.ligacopaperu.torneo.Season;
import pe.ligacopaperu.torneo.SeasonForm;
import pe.ligacopaperu.torneo.SeasonRepository;
import pe.ligacopaperu.torneo.SeasonService;
import pe.ligacopaperu.torneo.SeasonStatus;

@Service
@Order(20)
public class SanMarcosBootstrapService implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SanMarcosBootstrapService.class);

    private record SeedClubSpec(
            String officialName,
            String shortName,
            String acronym,
            String district,
            String stadium,
            int foundedYear,
            String primaryColor,
            String secondaryColor,
            String groupName) {
    }

    private static final List<SeedClubSpec> SAN_MARCOS_CLUBS = List.of(
            // 1. Distrito Pedro Gálvez (Sede Provincial - San Marcos)
            new SeedClubSpec("Club Deportivo Municipal de San Marcos", "Municipal San Marcos", "CDMSM",
                    "Pedro Gálvez", "Estadio Municipal de San Marcos", 1965, "#df1732", "#ffffff", "Serie A · Pedro Gálvez"),
            new SeedClubSpec("Club Cultural Deportivo San Marcos", "Cultural San Marcos", "CCDSM",
                    "Pedro Gálvez", "Estadio Municipal de San Marcos", 1972, "#1c3d6e", "#f2d783", "Serie A · Pedro Gálvez"),
            new SeedClubSpec("Club Sport Choloque de Pedro Gálvez", "Sport Choloque", "CSCPG",
                    "Pedro Gálvez", "Estadio Municipal de San Marcos", 1980, "#197a46", "#ffffff", "Serie A · Pedro Gálvez"),
            new SeedClubSpec("Club Defensor Pedro Gálvez", "Defensor Pedro Gálvez", "CDPG",
                    "Pedro Gálvez", "Estadio Municipal de San Marcos", 1978, "#b01528", "#162832", "Serie B · Pedro Gálvez"),
            new SeedClubSpec("Club Juventud San Marcos", "Juventud San Marcos", "CJSM",
                    "Pedro Gálvez", "Estadio Municipal de San Marcos", 1986, "#d97706", "#ffffff", "Serie B · Pedro Gálvez"),
            new SeedClubSpec("Club Unión Huayanay de Pedro Gálvez", "Unión Huayanay", "CUHPG",
                    "Pedro Gálvez", "Estadio Municipal de San Marcos", 1991, "#1e3a8a", "#ffffff", "Serie B · Pedro Gálvez"),

            // 2. Distrito Chancay
            new SeedClubSpec("Club Deportivo Municipal de Chancay", "Municipal Chancay", "CDMCH",
                    "Chancay", "Estadio Municipal de Chancay", 1974, "#df1732", "#ffffff", "Serie A · Chancay"),
            new SeedClubSpec("Club Defensor Chancay", "Defensor Chancay", "CDCH",
                    "Chancay", "Estadio Municipal de Chancay", 1981, "#15803d", "#ffffff", "Serie A · Chancay"),
            new SeedClubSpec("Club Unión Chancay", "Unión Chancay", "CUCH",
                    "Chancay", "Estadio Municipal de Chancay", 1985, "#1d4ed8", "#facc15", "Serie A · Chancay"),
            new SeedClubSpec("Club Sport Chuquiamo de Chancay", "Sport Chuquiamo", "CSCCH",
                    "Chancay", "Estadio Municipal de Chancay", 1989, "#991b1b", "#ffffff", "Serie B · Chancay"),
            new SeedClubSpec("Club Juventud Chancay", "Juventud Chancay", "CJCH",
                    "Chancay", "Estadio Municipal de Chancay", 1993, "#0f766e", "#ffffff", "Serie B · Chancay"),
            new SeedClubSpec("Club Atlético Curhuaz de Chancay", "Atlético Curhuaz", "CACCH",
                    "Chancay", "Estadio Municipal de Chancay", 1996, "#6d28d9", "#ffffff", "Serie B · Chancay"),

            // 3. Distrito Eduardo Villanueva (La Grama)
            new SeedClubSpec("Club Deportivo Municipal de La Grama", "Municipal La Grama", "CDMLG",
                    "Eduardo Villanueva", "Estadio Municipal de La Grama", 1976, "#16a34a", "#ffffff", "Serie A · Eduardo Villanueva"),
            new SeedClubSpec("Club Defensor Eduardo Villanueva", "Defensor Villanueva", "CDEV",
                    "Eduardo Villanueva", "Estadio Municipal de La Grama", 1984, "#df1732", "#ffffff", "Serie A · Eduardo Villanueva"),
            new SeedClubSpec("Club Sport Valle Condebamba de La Grama", "Valle Condebamba", "CSVCLG",
                    "Eduardo Villanueva", "Estadio Municipal de La Grama", 1988, "#0284c7", "#ffffff", "Serie A · Eduardo Villanueva"),
            new SeedClubSpec("Club Unión La Grama", "Unión La Grama", "CULG",
                    "Eduardo Villanueva", "Estadio Municipal de La Grama", 1990, "#ca8a04", "#15252f", "Serie B · Eduardo Villanueva"),
            new SeedClubSpec("Club Juventud Campo Alegre", "Campo Alegre", "CJCA",
                    "Eduardo Villanueva", "Estadio Municipal de La Grama", 1994, "#dc2626", "#facc15", "Serie B · Eduardo Villanueva"),
            new SeedClubSpec("Club Atlético Colpón de Eduardo Villanueva", "Atlético Colpón", "CACEV",
                    "Eduardo Villanueva", "Estadio Municipal de La Grama", 1998, "#1e40af", "#ffffff", "Serie B · Eduardo Villanueva"),

            // 4. Distrito Gregorio Pita (Paucamarca)
            new SeedClubSpec("Club Deportivo Municipal de Paucamarca", "Municipal Paucamarca", "CDMP",
                    "Gregorio Pita", "Estadio Municipal de Paucamarca", 1975, "#df1732", "#ffffff", "Serie A · Gregorio Pita"),
            new SeedClubSpec("Club Defensor Gregorio Pita", "Defensor Gregorio Pita", "CDGP",
                    "Gregorio Pita", "Estadio Municipal de Paucamarca", 1982, "#1e3a8a", "#facc15", "Serie A · Gregorio Pita"),
            new SeedClubSpec("Club Sport Illuca de Paucamarca", "Sport Illuca", "CSIP",
                    "Gregorio Pita", "Estadio Municipal de Paucamarca", 1987, "#15803d", "#ffffff", "Serie A · Gregorio Pita"),
            new SeedClubSpec("Club Unión Paucamarca", "Unión Paucamarca", "CUP",
                    "Gregorio Pita", "Estadio Municipal de Paucamarca", 1991, "#b91c1c", "#ffffff", "Serie B · Gregorio Pita"),
            new SeedClubSpec("Club Juventud Gregorio Pita", "Juventud Gregorio Pita", "CJGP",
                    "Gregorio Pita", "Estadio Municipal de Paucamarca", 1995, "#0369a1", "#ffffff", "Serie B · Gregorio Pita"),
            new SeedClubSpec("Club Real Manzanilla de Gregorio Pita", "Real Manzanilla", "CRMGP",
                    "Gregorio Pita", "Estadio Municipal de Paucamarca", 1999, "#b45309", "#ffffff", "Serie B · Gregorio Pita"),

            // 5. Distrito Ichocán
            new SeedClubSpec("Club Deportivo Municipal de Ichocán", "Municipal Ichocán", "CDMI",
                    "Ichocán", "Estadio Municipal de Ichocán", 1968, "#df1732", "#ffffff", "Serie A · Ichocán"),
            new SeedClubSpec("Club Cultural Ichocán", "Cultural Ichocán", "CCI",
                    "Ichocán", "Estadio Municipal de Ichocán", 1973, "#1d4ed8", "#ffffff", "Serie A · Ichocán"),
            new SeedClubSpec("Club Defensor San Isidro de Ichocán", "San Isidro Ichocán", "CDSII",
                    "Ichocán", "Estadio Municipal de Ichocán", 1979, "#15803d", "#facc15", "Serie A · Ichocán"),
            new SeedClubSpec("Club Sport Chancaybaños de Ichocán", "Sport Chancaybaños", "CSCHI",
                    "Ichocán", "Estadio Municipal de Ichocán", 1985, "#991b1b", "#ffffff", "Serie B · Ichocán"),
            new SeedClubSpec("Club Unión Llanupacha de Ichocán", "Unión Llanupacha", "CULLI",
                    "Ichocán", "Estadio Municipal de Ichocán", 1990, "#0f766e", "#ffffff", "Serie B · Ichocán"),
            new SeedClubSpec("Club Juventud Pomabamba de Ichocán", "Juventud Pomabamba", "CJPI",
                    "Ichocán", "Estadio Municipal de Ichocán", 1994, "#4338ca", "#ffffff", "Serie B · Ichocán"),

            // 6. Distrito José Manuel Quiroz (Shirac)
            new SeedClubSpec("Club Deportivo Municipal de Shirac", "Municipal Shirac", "CDMSH",
                    "José Manuel Quiroz", "Estadio Municipal de Shirac", 1977, "#df1732", "#ffffff", "Serie A · José M. Quiroz"),
            new SeedClubSpec("Club Defensor José Manuel Quiroz", "Defensor Quiroz", "CDJMQ",
                    "José Manuel Quiroz", "Estadio Municipal de Shirac", 1983, "#1e3a8a", "#ffffff", "Serie A · José M. Quiroz"),
            new SeedClubSpec("Club Sport Licliconga de Shirac", "Sport Licliconga", "CSLSH",
                    "José Manuel Quiroz", "Estadio Municipal de Shirac", 1988, "#16a34a", "#ffffff", "Serie A · José M. Quiroz"),
            new SeedClubSpec("Club Unión Shirac", "Unión Shirac", "CUSH",
                    "José Manuel Quiroz", "Estadio Municipal de Shirac", 1992, "#b45309", "#ffffff", "Serie B · José M. Quiroz"),
            new SeedClubSpec("Club Juventud Jucat de Shirac", "Juventud Jucat", "CJJSH",
                    "José Manuel Quiroz", "Estadio Municipal de Shirac", 1996, "#dc2626", "#162832", "Serie B · José M. Quiroz"),
            new SeedClubSpec("Club Atlético Pauca de José Manuel Quiroz", "Atlético Pauca", "CAPJQ",
                    "José Manuel Quiroz", "Estadio Municipal de Shirac", 2000, "#0284c7", "#ffffff", "Serie B · José M. Quiroz"),

            // 7. Distrito José Sabogal (Venecia)
            new SeedClubSpec("Club Deportivo Municipal de Venecia", "Municipal Venecia", "CDMV",
                    "José Sabogal", "Estadio Municipal de Venecia", 1980, "#df1732", "#ffffff", "Serie A · José Sabogal"),
            new SeedClubSpec("Club Defensor José Sabogal", "Defensor Sabogal", "CDJS",
                    "José Sabogal", "Estadio Municipal de Venecia", 1985, "#15803d", "#ffffff", "Serie A · José Sabogal"),
            new SeedClubSpec("Club Sport Malat de José Sabogal", "Sport Malat", "CSMJS",
                    "José Sabogal", "Estadio Municipal de Venecia", 1989, "#1d4ed8", "#facc15", "Serie A · José Sabogal"),
            new SeedClubSpec("Club Unión Venecia de Sabogal", "Unión Venecia", "CUVS",
                    "José Sabogal", "Estadio Municipal de Venecia", 1993, "#991b1b", "#ffffff", "Serie B · José Sabogal"),
            new SeedClubSpec("Club Juventud Aguas Verdes de Sabogal", "Aguas Verdes", "CJAVS",
                    "José Sabogal", "Estadio Municipal de Venecia", 1997, "#0f766e", "#ffffff", "Serie B · José Sabogal"),
            new SeedClubSpec("Club Real Tambul de José Sabogal", "Real Tambul", "CRTJS",
                    "José Sabogal", "Estadio Municipal de Venecia", 2001, "#7c2d12", "#ffffff", "Serie B · José Sabogal"));

    private final ClubRepository clubs;
    private final ClubService clubService;
    private final SeasonRepository seasons;
    private final SeasonService seasonService;
    private final CompetitionRepository competitions;
    private final CompetitionService competitionService;
    private final TeamRepository teams;
    private final TeamService teamService;
    private final String bootstrapUsername;

    public SanMarcosBootstrapService(
            ClubRepository clubs,
            ClubService clubService,
            SeasonRepository seasons,
            SeasonService seasonService,
            CompetitionRepository competitions,
            CompetitionService competitionService,
            TeamRepository teams,
            TeamService teamService,
            @Value("${app.bootstrap-admin.username:admin}") String bootstrapUsername) {
        this.clubs = clubs;
        this.clubService = clubService;
        this.seasons = seasons;
        this.seasonService = seasonService;
        this.competitions = competitions;
        this.competitionService = competitionService;
        this.teams = teams;
        this.teamService = teamService;
        this.bootstrapUsername = bootstrapUsername;
    }

    @Override
    public void run(ApplicationArguments args) {
        if ("test-admin".equalsIgnoreCase(bootstrapUsername)) {
            return;
        }
        if (clubs.count() == 0) {
            int created = ensureSanMarcosLeagueData(bootstrapUsername);
            LOGGER.info("Se inicializó el padrón oficial de la Liga Provincial de San Marcos - Pedro Gálvez (Cajamarca) con {} clubes en los 7 distritos.", created);
        }
    }

    @Transactional
    public int ensureSanMarcosLeagueData(String actor) {
        int currentYear = LocalDate.now().getYear();
        String seasonName = "Copa Perú · Provincia de San Marcos";
        Season season = seasons.findAll().stream()
                .filter(s -> s.getName().equalsIgnoreCase(seasonName) && s.getYear().equals(currentYear))
                .findFirst()
                .orElseGet(() -> {
                    SeasonForm seasonForm = new SeasonForm();
                    seasonForm.setName(seasonName);
                    seasonForm.setYear(currentYear);
                    seasonForm.setStartDate(LocalDate.of(currentYear, 2, 1));
                    seasonForm.setEndDate(LocalDate.of(currentYear, 11, 30));
                    seasonForm.setStatus(SeasonStatus.ACTIVE);
                    return seasonService.save(null, seasonForm, actor);
                });

        String provincialCompName = "Liga Provincial de San Marcos · Sede Pedro Gálvez";
        Competition provincialCompetition = competitions.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(provincialCompName)
                        && c.getSeason().getId().equals(season.getId()))
                .findFirst()
                .orElseGet(() -> {
                    CompetitionForm compForm = new CompetitionForm();
                    compForm.setName(provincialCompName);
                    compForm.setSeasonId(season.getId());
                    compForm.setCategory("Etapa Provincial · San Marcos (Cajamarca)");
                    compForm.setStartDate(LocalDate.of(currentYear, 5, 1));
                    compForm.setEndDate(LocalDate.of(currentYear, 8, 31));
                    compForm.setPointsWin(3);
                    compForm.setPointsDraw(1);
                    compForm.setPointsLoss(0);
                    compForm.setStatus(CompetitionStatus.ACTIVE);
                    return competitionService.save(null, compForm, actor);
                });

        int createdClubs = 0;
        for (SeedClubSpec spec : SAN_MARCOS_CLUBS) {
            Club club = clubs.findAll().stream()
                    .filter(existing -> existing.getOfficialName().equalsIgnoreCase(spec.officialName())
                            || (existing.getAcronym() != null
                                    && existing.getAcronym().equalsIgnoreCase(spec.acronym())))
                    .findFirst()
                    .orElse(null);

            if (club == null) {
                ClubForm form = new ClubForm();
                form.setOfficialName(spec.officialName());
                form.setShortName(spec.shortName());
                form.setAcronym(spec.acronym());
                form.setDescription("Institución representativa del distrito de " + spec.district()
                        + ", afiliada a la Liga Provincial de Fútbol de San Marcos (Sede Pedro Gálvez, Región Cajamarca).");
                form.setFoundedYear(spec.foundedYear());
                form.setCountry("Perú");
                form.setDepartment("Cajamarca");
                form.setProvince("San Marcos");
                form.setDistrict(spec.district());
                form.setStadium(spec.stadium());
                form.setPrimaryColor(spec.primaryColor());
                form.setSecondaryColor(spec.secondaryColor());
                form.setStatus(ClubStatus.ACTIVE);
                club = clubService.create(form, actor);
                createdClubs++;
            }

            if (!teams.existsByCompetitionIdAndNameIgnoreCase(provincialCompetition.getId(), spec.shortName())) {
                TeamForm teamForm = new TeamForm();
                teamForm.setClubId(club.getId());
                teamForm.setCompetitionId(provincialCompetition.getId());
                teamForm.setName(spec.shortName());
                teamForm.setShortName(spec.acronym());
                teamForm.setGroupName(spec.groupName());
                teamForm.setStadium(spec.stadium());
                teamForm.setStatus(TeamStatus.ACTIVE);
                teamService.save(null, teamForm, actor);
            }
        }
        return createdClubs;
    }
}
