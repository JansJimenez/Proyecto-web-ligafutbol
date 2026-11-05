package pe.ligacopaperu.club;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.ligacopaperu.auditoria.AuditService;

@ExtendWith(MockitoExtension.class)
class ClubServiceTest {

    @Mock
    private ClubRepository clubs;

    @Mock
    private AuditService audit;

    private ClubService service;

    @BeforeEach
    void setUp() {
        service = new ClubService(clubs, audit);
    }

    @Test
    void createNormalizesOptionalFieldsAndAuditsTheOperation() {
        ClubForm form = new ClubForm();
        form.setOfficialName("  Club Deportivo Ejemplo  ");
        form.setAcronym(" ABC ");
        when(clubs.save(any(Club.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Club saved = service.create(form, "admin");

        assertEquals("Club Deportivo Ejemplo", saved.getOfficialName());
        assertEquals("ABC", saved.getAcronym());
        verify(audit).record(eq("admin"), eq("CLUB_CREATED"), any(), eq("Club Deportivo Ejemplo"));
    }

    @Test
    void createRejectsDuplicateAcronymBeforeSaving() {
        ClubForm form = new ClubForm();
        form.setOfficialName("Club Nuevo");
        form.setAcronym("ABC");
        when(clubs.existsByAcronymIgnoreCase("ABC")).thenReturn(true);

        assertThrows(DuplicateClubAcronymException.class, () -> service.create(form, "admin"));

        verify(clubs, never()).save(any(Club.class));
        verify(audit, never()).record(any(), any(), any(), any());
    }

    @Test
    void publicListingOnlyQueriesActiveClubs() {
        service.listPublic();

        verify(clubs).findByStatusOrderByOfficialNameAsc(ClubStatus.ACTIVE);
    }
}
