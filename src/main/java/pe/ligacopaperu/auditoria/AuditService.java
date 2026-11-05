package pe.ligacopaperu.auditoria;

import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public void record(String actor, String action, Long entityId, String details) {
        record(actor, action, "CLUB", entityId, details);
    }

    public void record(String actor, String action, String entityType, Long entityId, String details) {
        AuditEntry entry = new AuditEntry();
        entry.setActor(actor);
        entry.setAction(action);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setDetails(details);
        auditRepository.save(entry);
    }
}
