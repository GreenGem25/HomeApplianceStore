package ohio.rizz.homeappliancestore.services;

import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.AuditLogDto;
import ohio.rizz.homeappliancestore.entities.AuditLog;
import ohio.rizz.homeappliancestore.enums.AuditAction;
import ohio.rizz.homeappliancestore.enums.AuditEntityType;
import ohio.rizz.homeappliancestore.mappers.AuditLogMapper;
import ohio.rizz.homeappliancestore.repositories.AuditLogRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(AuditAction action, AuditEntityType entityType, String entityId, String details) {
        String username = "system";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            username = auth.getName();
        }

        AuditLog log = new AuditLog();
        log.setTimestamp(LocalDateTime.now());
        log.setUsername(username);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLogDto> getFilteredLogs(LocalDate from, LocalDate to, String username, String entityType, String action) {
        Specification<AuditLog> spec = Specification.where(null);

        if (from != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("timestamp"), from.atStartOfDay()));
        }
        if (to != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("timestamp"), to.plusDays(1).atStartOfDay()));
        }
        if (username != null && !username.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%"));
        }
        if (entityType != null && !entityType.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("entityType")), entityType.toLowerCase()));
        }
        if (action != null && !action.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("action")), action.toLowerCase()));
        }

        return auditLogMapper.toDto(auditLogRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "timestamp")));
    }
}