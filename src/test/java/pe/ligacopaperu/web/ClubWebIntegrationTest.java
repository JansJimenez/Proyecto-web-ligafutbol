package pe.ligacopaperu.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import pe.ligacopaperu.auditoria.AuditRepository;
import pe.ligacopaperu.club.ClubRepository;
import pe.ligacopaperu.equipo.TeamRepository;
import pe.ligacopaperu.jugador.PlayerRepository;
import pe.ligacopaperu.partido.MatchRepository;
import pe.ligacopaperu.torneo.CompetitionRepository;
import pe.ligacopaperu.torneo.SeasonRepository;
import pe.ligacopaperu.estadistica.StandingsService;
import pe.ligacopaperu.club.ClubForm;
import pe.ligacopaperu.club.ClubService;
import pe.ligacopaperu.equipo.TeamForm;
import pe.ligacopaperu.equipo.TeamService;
import pe.ligacopaperu.equipo.TeamStatus;
import pe.ligacopaperu.jugador.PlayerForm;
import pe.ligacopaperu.jugador.PlayerPosition;
import pe.ligacopaperu.jugador.PlayerService;
import pe.ligacopaperu.partido.MatchForm;
import pe.ligacopaperu.partido.MatchService;
import pe.ligacopaperu.partido.MatchStatus;
import pe.ligacopaperu.partido.MatchEventForm;
import pe.ligacopaperu.partido.MatchEventService;
import pe.ligacopaperu.partido.MatchEventType;
import pe.ligacopaperu.partido.MatchEventRepository;
import pe.ligacopaperu.torneo.CompetitionForm;
import pe.ligacopaperu.torneo.CompetitionService;
import pe.ligacopaperu.torneo.CompetitionStatus;
import pe.ligacopaperu.torneo.SeasonForm;
import pe.ligacopaperu.torneo.SeasonService;
import pe.ligacopaperu.torneo.SeasonStatus;
import pe.ligacopaperu.estadistica.StatisticsService;
import pe.ligacopaperu.noticia.NewsForm;
import pe.ligacopaperu.noticia.NewsService;
import pe.ligacopaperu.noticia.NewsStatus;
import pe.ligacopaperu.noticia.NewsRepository;
import pe.ligacopaperu.cuerpotecnico.StaffRole;
import pe.ligacopaperu.cuerpotecnico.TechnicalStaffForm;
import pe.ligacopaperu.cuerpotecnico.TechnicalStaffRepository;
import pe.ligacopaperu.cuerpotecnico.TechnicalStaffService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:liga;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.enabled=true",
        "app.bootstrap-admin.username=test-admin",
        "app.bootstrap-admin.password=Test-Admin-Password-123"
})
class ClubWebIntegrationTest {

    private static final Path UPLOAD_DIRECTORY = Path.of(System.getProperty("java.io.tmpdir"),
            "liga-copa-peru-upload-tests-" + UUID.randomUUID());

    @DynamicPropertySource
    static void uploadDirectory(DynamicPropertyRegistry registry) {
        registry.add("app.uploads.location", () -> UPLOAD_DIRECTORY.toString());
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ClubRepository clubs;

    @Autowired
    private AuditRepository audits;

    @Autowired
    private TeamRepository teams;

    @Autowired
    private PlayerRepository players;

    @Autowired
    private MatchRepository matches;

    @Autowired
    private MatchEventRepository matchEvents;

    @Autowired
    private TechnicalStaffRepository technicalStaff;

    @Autowired
    private CompetitionRepository competitions;

    @Autowired
    private SeasonRepository seasons;

    @Autowired
    private ClubService clubService;

    @Autowired
    private SeasonService seasonService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private StandingsService standingsService;

    @Autowired
    private MatchEventService matchEventService;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private TechnicalStaffService technicalStaffService;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsService newsService;

    @BeforeEach
    void clearClubs() {
        matchEvents.deleteAll();
        matches.deleteAll();
        technicalStaff.deleteAll();
        newsRepository.deleteAll();
        players.deleteAll();
        teams.deleteAll();
        competitions.deleteAll();
        seasons.deleteAll();
        clubs.deleteAll();
        audits.deleteAll();
    }

    @AfterEach
    void clearUploadedFiles() throws IOException {
        if (Files.exists(UPLOAD_DIRECTORY)) {
            try (var paths = Files.walk(UPLOAD_DIRECTORY)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }

    @Test
    void publicHomeRendersDatabaseBackedEmptyState() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Aún no hay clubes publicados")))
                .andExpect(content().string(containsString("/images/copa-peru-logo.png")))
                .andExpect(content().string(containsString("/images/fpf-logo.png")))
                .andExpect(content().string(containsString("Donde nace la pasión del fútbol peruano.")));
    }

    @Test
    void publicSportsPagesRenderWithoutData() throws Exception {
        mvc.perform(get("/equipos")).andExpect(status().isOk())
                .andExpect(content().string(containsString("No hay equipos publicados")));
        mvc.perform(get("/equipos")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Cuerpo técnico")))
                .andExpect(content().string(containsString("/js/site.js")));
        mvc.perform(get("/jugadores")).andExpect(status().isOk())
                .andExpect(content().string(containsString("No hay jugadores publicados")));
        mvc.perform(get("/partidos")).andExpect(status().isOk())
                .andExpect(content().string(containsString("No hay partidos publicados")));
        mvc.perform(get("/cuerpo-tecnico")).andExpect(status().isOk())
                .andExpect(content().string(containsString("No hay cuerpo técnico publicado")));
        mvc.perform(get("/tabla")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Sin posiciones calculadas")));
        mvc.perform(get("/noticias")).andExpect(status().isOk())
                .andExpect(content().string(containsString("No hay noticias publicadas")));
    }

    @Test
    void administratorCanManageTheCoreCompetitionWorkflow() throws Exception {
        ClubForm clubForm = new ClubForm();
        clubForm.setOfficialName("Club Prueba A");
        var clubA = clubService.create(clubForm, "test-admin");
        clubForm.setOfficialName("Club Prueba B");
        var clubB = clubService.create(clubForm, "test-admin");

        SeasonForm seasonForm = new SeasonForm();
        seasonForm.setName("Temporada de prueba");
        seasonForm.setYear(2026);
        seasonForm.setStatus(SeasonStatus.ACTIVE);
        seasonForm.setStartDate(LocalDate.of(2026, 1, 1));
        seasonForm.setEndDate(LocalDate.of(2026, 12, 31));
        var season = seasonService.save(null, seasonForm, "test-admin");

        CompetitionForm competitionForm = new CompetitionForm();
        competitionForm.setName("Torneo de prueba");
        competitionForm.setSeasonId(season.getId());
        competitionForm.setCategory("Libre");
        competitionForm.setStatus(CompetitionStatus.ACTIVE);
        competitionForm.setPointsWin(3);
        competitionForm.setPointsDraw(1);
        competitionForm.setPointsLoss(0);
        var competition = competitionService.save(null, competitionForm, "test-admin");

        TeamForm teamForm = new TeamForm();
        teamForm.setCompetitionId(competition.getId());
        teamForm.setClubId(clubA.getId());
        teamForm.setName("Equipo A");
        teamForm.setStatus(TeamStatus.ACTIVE);
        var home = teamService.save(null, teamForm, "test-admin");
        teamForm.setClubId(clubB.getId());
        teamForm.setName("Equipo B");
        var away = teamService.save(null, teamForm, "test-admin");

        TechnicalStaffForm staffForm = new TechnicalStaffForm();
        staffForm.setTeamId(home.getId());
        staffForm.setFirstName("Técnico");
        staffForm.setLastName("Prueba");
        staffForm.setRole(StaffRole.HEAD_COACH);
        staffForm.setEmail("tecnico@example.test");
        technicalStaffService.save(null, staffForm, "test-admin");

        PlayerForm playerForm = new PlayerForm();
        playerForm.setTeamId(home.getId());
        playerForm.setFirstName("Nombre");
        playerForm.setLastName("Ficticio");
        playerForm.setPosition(PlayerPosition.STRIKER);
        playerForm.setJerseyNumber(9);
        playerService.save(null, playerForm, "test-admin");

        MatchForm matchForm = new MatchForm();
        matchForm.setCompetitionId(competition.getId());
        matchForm.setHomeTeamId(home.getId());
        matchForm.setAwayTeamId(away.getId());
        matchForm.setScheduledAt(LocalDateTime.of(2026, 6, 1, 15, 0));
        matchForm.setRoundNumber(1);
        matchForm.setStatus(MatchStatus.SCHEDULED);
        var savedMatch = matchService.save(null, matchForm, "test-admin");

        MatchEventForm eventForm = new MatchEventForm();
        eventForm.setTeamId(home.getId());
        eventForm.setPlayerId(players.findAll().get(0).getId());
        eventForm.setMinute(32);
        eventForm.setType(MatchEventType.GOAL);
        matchEventService.add(savedMatch.getId(), eventForm, "test-admin");
        matchForm.setHomeGoals(1);
        matchForm.setAwayGoals(0);
        matchForm.setStatus(MatchStatus.FINALIZED);
        matchService.save(savedMatch.getId(), matchForm, "test-admin");

        org.junit.jupiter.api.Assertions.assertEquals(2, teams.count());
        org.junit.jupiter.api.Assertions.assertEquals(2,
                teamService.activeByCompetition(competition.getId()).size());
        var table = standingsService.table(competition.getId());
        org.junit.jupiter.api.Assertions.assertEquals(2, table.size());
        org.junit.jupiter.api.Assertions.assertEquals(home.getId(), table.get(0).teamId());
        org.junit.jupiter.api.Assertions.assertEquals(3, table.get(0).points());
        org.junit.jupiter.api.Assertions.assertEquals(1, table.get(0).goalsFor());
        org.junit.jupiter.api.Assertions.assertEquals(0, table.get(1).points());
        org.junit.jupiter.api.Assertions.assertEquals(1, players.count());
        org.junit.jupiter.api.Assertions.assertEquals(1, statisticsService.playerStatistics().get(0).goals());
        mvc.perform(get("/admin/partidos/{id}/acta", savedMatch.getId())
                        .session((MockHttpSession) mvc.perform(formLogin()
                                .user("test-admin")
                                .password("Test-Admin-Password-123"))
                                .andReturn().getRequest().getSession(false)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Acta del partido")));
        mvc.perform(get("/estadisticas"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ficticio")));
        mvc.perform(get("/cuerpo-tecnico"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Técnico Prueba")));
        org.junit.jupiter.api.Assertions.assertTrue(audits.count() >= 7);
    }

    @Test
    void onlyPublishedNewsAppearsOnPublicPagesAndArchivedNewsIsHidden() throws Exception {
        NewsForm form = new NewsForm();
        form.setTitle("Comunicado de prueba");
        form.setSummary("Resumen de prueba");
        form.setContent("Contenido no HTML <script>alert('no');</script>");
        form.setStatus(NewsStatus.PUBLISHED);
        var saved = newsService.save(null, form, "test-admin");

        mvc.perform(get("/noticias"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Comunicado de prueba")));
        mvc.perform(get("/noticias/{slug}", saved.getSlug()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("&lt;script&gt;")));

        newsService.archive(saved.getId(), "test-admin");
        mvc.perform(get("/noticias/{slug}", saved.getSlug()))
                .andExpect(status().isNotFound());
    }

    @Test
    void administratorUploadsPublicImagesAndPrivateIdentityDocuments() throws Exception {
        MvcResult login = mvc.perform(formLogin().user("test-admin").password("Test-Admin-Password-123"))
                .andExpect(authenticated())
                .andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

        ClubForm clubForm = new ClubForm();
        clubForm.setOfficialName("Club con archivos");
        var club = clubService.create(clubForm, "test-admin");
        SeasonForm seasonForm = new SeasonForm();
        seasonForm.setName("Temporada con archivos");
        seasonForm.setYear(2026);
        seasonForm.setStatus(SeasonStatus.ACTIVE);
        var season = seasonService.save(null, seasonForm, "test-admin");
        CompetitionForm competitionForm = new CompetitionForm();
        competitionForm.setName("Torneo con archivos");
        competitionForm.setSeasonId(season.getId());
        competitionForm.setCategory("Libre");
        competitionForm.setStatus(CompetitionStatus.ACTIVE);
        var competition = competitionService.save(null, competitionForm, "test-admin");

        mvc.perform(multipart("/admin/equipos")
                        .file(new MockMultipartFile("crestFile", "imagen.png", "image/png",
                                "<script>alert(1)</script>".getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                        .session(session)
                        .with(csrf())
                        .param("clubId", club.getId().toString())
                        .param("competitionId", competition.getId().toString())
                        .param("name", "Equipo con archivo no válido")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("La imagen debe ser un archivo PNG o JPG válido.")));
        org.junit.jupiter.api.Assertions.assertEquals(0, teams.count());

        mvc.perform(multipart("/admin/equipos")
                        .file(imageFile("crestFile"))
                        .session(session)
                        .with(csrf())
                        .param("clubId", club.getId().toString())
                        .param("competitionId", competition.getId().toString())
                        .param("name", "Equipo con escudo")
                        .param("status", "ACTIVE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/equipos"));
        var team = teams.findAll().get(0);

        mvc.perform(multipart("/admin/jugadores")
                        .file(imageFile("photoFile"))
                        .file(pdfFile("identityDocumentFile"))
                        .session(session)
                        .with(csrf())
                        .param("teamId", team.getId().toString())
                        .param("firstName", "Jugador")
                        .param("lastName", "Registrado")
                        .param("birthDate", "2000-01-01")
                        .param("position", "STRIKER")
                        .param("status", "ACTIVE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/jugadores"));
        var player = players.findAll().get(0);

        mvc.perform(multipart("/admin/cuerpo-tecnico")
                        .file(imageFile("photoFile"))
                        .file(pdfFile("identityDocumentFile"))
                        .session(session)
                        .with(csrf())
                        .param("teamId", team.getId().toString())
                        .param("firstName", "Técnico")
                        .param("lastName", "Registrado")
                        .param("role", "HEAD_COACH")
                        .param("status", "ACTIVE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/cuerpo-tecnico"));
        var staffMember = technicalStaff.findAll().get(0);

        mvc.perform(multipart("/admin/noticias")
                        .file(imageFile("imageFile"))
                        .session(session)
                        .with(csrf())
                        .param("title", "Noticia con fotografía")
                        .param("summary", "Resumen de una noticia con fotografía.")
                        .param("content", "Contenido de prueba.")
                        .param("status", "PUBLISHED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/noticias"));
        var article = newsRepository.findAll().get(0);

        mvc.perform(get("/admin/jugadores/{id}/editar", player.getId()).session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Descargar documento cargado")));

        String crestName = team.getCrestUrl().substring(team.getCrestUrl().lastIndexOf('/') + 1);
        String photoName = player.getPhotoUrl().substring(player.getPhotoUrl().lastIndexOf('/') + 1);
        String documentName = player.getIdentityDocumentUrl()
                .substring(player.getIdentityDocumentUrl().lastIndexOf('/') + 1);
        String newsImageName = article.getImageUrl().substring(article.getImageUrl().lastIndexOf('/') + 1);
        mvc.perform(get("/admin/equipos/{id}/editar", team.getId()).session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(crestName)));
        mvc.perform(get("/admin/cuerpo-tecnico/{id}/editar", staffMember.getId()).session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Descargar documento cargado")));
        mvc.perform(get("/admin/noticias/{id}/editar", article.getId()).session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(newsImageName)));
        mvc.perform(get("/equipos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(crestName)));
        mvc.perform(get("/media/{fileName}", crestName))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"));
        mvc.perform(get("/media/{fileName}", photoName)).andExpect(status().isOk());
        mvc.perform(get("/media/{fileName}", newsImageName)).andExpect(status().isOk());
        mvc.perform(get("/admin/documentos/{fileName}", documentName))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        mvc.perform(get("/admin/documentos/{fileName}", documentName).session(session))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Cache-Control", "no-store"));
        mvc.perform(get("/jugadores"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(photoName)))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString(documentName))));
        String staffPhotoName = staffMember.getPhotoUrl()
                .substring(staffMember.getPhotoUrl().lastIndexOf('/') + 1);
        mvc.perform(get("/cuerpo-tecnico"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(staffPhotoName)));
        mvc.perform(get("/noticias"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(newsImageName)));
    }

    @Test
    void administratorPagesAndFormsRenderWithMigratedSchema() throws Exception {
        MvcResult login = mvc.perform(formLogin().user("test-admin").password("Test-Admin-Password-123"))
                .andExpect(authenticated())
                .andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
        List<String> paths = List.of(
                "/admin", "/admin/clubes", "/admin/clubes/nuevo",
                "/admin/temporadas", "/admin/temporadas/nueva",
                "/admin/torneos", "/admin/torneos/nuevo",
                "/admin/equipos", "/admin/equipos/nuevo",
                "/admin/jugadores", "/admin/jugadores/nuevo",
                "/admin/partidos", "/admin/partidos/nuevo", "/admin/tabla",
                "/admin/noticias", "/admin/noticias/nueva");
        for (String path : paths) {
            mvc.perform(get(path).session(session)).andExpect(status().isOk());
        }
    }

    @Test
    void administrationRequiresAnAuthenticatedAdministrator() throws Exception {
        mvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(unauthenticated());
    }

    @Test
    void administratorCanCreateEditAndDeactivateClubWithCsrfProtectionEnabled() throws Exception {
        MvcResult login = mvc.perform(formLogin()
                        .user("test-admin")
                        .password("Test-Admin-Password-123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated())
                .andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

        mvc.perform(post("/admin/clubes")
                        .session(session)
                        .with(csrf())
                        .param("officialName", "Club Integración")
                        .param("acronym", "CI"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/clubes"));

        mvc.perform(get("/clubes"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Club Integración")));

        long clubId = clubs.findAll().get(0).getId();
        mvc.perform(post("/admin/clubes/{id}", clubId)
                        .session(session)
                        .with(csrf())
                        .param("officialName", "Club Integración Actualizado")
                        .param("acronym", "CI")
                        .param("status", "ACTIVE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/clubes"));

        mvc.perform(get("/clubes"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Club Integración Actualizado")));

        mvc.perform(post("/admin/clubes/{id}/desactivar", clubId)
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/clubes"));

        mvc.perform(get("/clubes"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No hay clubes publicados")));
        org.junit.jupiter.api.Assertions.assertEquals(3, audits.count());
    }

    @Test
    void administratorPostWithoutCsrfTokenIsRejected() throws Exception {
        MvcResult login = mvc.perform(formLogin()
                        .user("test-admin")
                        .password("Test-Admin-Password-123"))
                .andExpect(authenticated())
                .andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

        mvc.perform(post("/admin/clubes")
                        .session(session)
                        .param("officialName", "Club Sin CSRF"))
                .andExpect(status().isForbidden());

        org.junit.jupiter.api.Assertions.assertEquals(0, clubs.count());
    }

    private MockMultipartFile imageFile(String fieldName) throws IOException {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);
        return new MockMultipartFile(fieldName, "imagen.png", "image/png", output.toByteArray());
    }

    private MockMultipartFile pdfFile(String fieldName) {
        return new MockMultipartFile(fieldName, "documento.pdf", "application/pdf",
                "%PDF-1.4\nDocumento de prueba\n%%EOF".getBytes(java.nio.charset.StandardCharsets.US_ASCII));
    }
}
