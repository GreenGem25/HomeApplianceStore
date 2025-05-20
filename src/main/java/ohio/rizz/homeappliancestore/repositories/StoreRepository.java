package ohio.rizz.homeappliancestore.repositories;

import ohio.rizz.homeappliancestore.entities.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {

}
