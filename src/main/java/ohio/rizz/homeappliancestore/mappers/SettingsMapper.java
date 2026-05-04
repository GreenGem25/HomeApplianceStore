package ohio.rizz.homeappliancestore.mappers;

import ohio.rizz.homeappliancestore.dto.SettingsDto;
import ohio.rizz.homeappliancestore.entities.Settings;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SettingsMapper {
    SettingsDto toDto(Settings settings);
    Settings fromDto(SettingsDto settingsDto);
}
