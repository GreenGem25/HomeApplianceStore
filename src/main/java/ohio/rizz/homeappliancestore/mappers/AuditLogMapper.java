package ohio.rizz.homeappliancestore.mappers;

import ohio.rizz.homeappliancestore.dto.AuditLogDto;
import ohio.rizz.homeappliancestore.entities.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuditLogMapper {
    List<AuditLogDto> toDto(List<AuditLog> auditLogs);
}
