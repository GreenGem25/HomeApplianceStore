package ohio.rizz.homeappliancestore.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import ohio.rizz.homeappliancestore.enums.AuditAction;
import ohio.rizz.homeappliancestore.enums.AuditEntityType;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogDto {
    private LocalDateTime timestamp;
    private String username;
    private AuditAction action;
    private AuditEntityType entityType;
    private String entityId;
    private String details;
}
