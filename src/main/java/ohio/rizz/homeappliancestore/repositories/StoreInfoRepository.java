package ohio.rizz.homeappliancestore.repositories;

import ohio.rizz.homeappliancestore.entities.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StoreInfoRepository extends JpaRepository<Settings, UUID> {
    Optional<Settings> findFirstBy();
}