package pe.ligacopaperu.auditoria;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.firestore.Firestore;

@Service
public class AuditService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditService.class);

    private final AuditRepository auditRepository;
    private final Firestore firestore;

    @Autowired
    public AuditService(AuditRepository auditRepository, Firestore firestore) {
        this.auditRepository = auditRepository;
        this.firestore = firestore;
    }

    public AuditService(AuditRepository auditRepository) {
        this(auditRepository, null);
    }

    @Transactional(readOnly = true)
    public List<AuditEntry> listRecent() {
        return auditRepository.findTop100ByOrderByCreatedAtDescIdDesc();
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
        AuditEntry saved = auditRepository.save(entry);
        syncToFirestore(saved);
    }

    @SuppressWarnings("null")
    private void syncToFirestore(AuditEntry entry) {
        if (firestore == null || entry == null || entry.getId() == null) {
            return;
        }
        try {
            Map<String, Object> doc = new LinkedHashMap<>();
            doc.put("id", entry.getId());
            doc.put("actor", entry.getActor());
            doc.put("action", entry.getAction());
            doc.put("entityType", entry.getEntityType());
            doc.put("entityId", entry.getEntityId());
            doc.put("details", entry.getDetails());
            doc.put("createdAt", entry.getCreatedAt() != null ? entry.getCreatedAt().toString() : null);
            firestore.collection("audit_log").document(java.util.Objects.requireNonNull(String.valueOf(entry.getId()))).set(doc);
        } catch (Exception exception) {
            LOGGER.warn("No se pudo sincronizar el registro de auditoría en Firestore.", exception);
        }
    }
}
