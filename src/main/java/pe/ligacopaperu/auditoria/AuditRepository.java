package pe.ligacopaperu.auditoria;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AuditEntry, Long> {
}
