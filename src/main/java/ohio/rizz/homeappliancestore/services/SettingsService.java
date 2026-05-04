package ohio.rizz.homeappliancestore.services;

import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.SettingsDto;
import ohio.rizz.homeappliancestore.entities.Settings;
import ohio.rizz.homeappliancestore.mappers.SettingsMapper;
import ohio.rizz.homeappliancestore.repositories.StoreInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettingsService {
    private final StoreInfoRepository repository;
    private final SettingsMapper mapper;

    @Transactional(readOnly = true)
    public SettingsDto getSettings() {
        Settings settings = repository.findFirstBy().orElseGet(() -> {
            Settings defaultInfo = new Settings();
            return repository.save(defaultInfo);
        });
        return mapper.toDto(settings);
    }

    @Transactional
    public void updateSettings(SettingsDto updated) {
        SettingsDto current = getSettings();
        current.setShopName(updated.getShopName());
        current.setAddress(updated.getAddress());
        current.setPhone(updated.getPhone());
        current.setEmail(updated.getEmail());
        repository.save(mapper.fromDto(current));
    }
}
