package ohio.rizz.homeappliancestore.repositories;

import ohio.rizz.homeappliancestore.entities.AuditLog;
import ohio.rizz.homeappliancestore.enums.AuditEntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID>, JpaSpecificationExecutor<AuditLog> {
    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(AuditEntityType entityType, String entityId);
    List<AuditLog> findByUsernameOrderByTimestampDesc(String username);
    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime from, LocalDateTime to);
}