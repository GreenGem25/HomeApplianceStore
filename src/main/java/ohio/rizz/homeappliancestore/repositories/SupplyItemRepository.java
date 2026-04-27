package ohio.rizz.homeappliancestore.repositories;

import ohio.rizz.homeappliancestore.entities.SupplyItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SupplyItemRepository extends JpaRepository<SupplyItem, UUID> {

    List<SupplyItem> findBySupplyId(UUID supplyId);

    List<SupplyItem> findByProductId(UUID productId);

    @Query("SELECT SUM(si.quantity) FROM SupplyItem si WHERE si.product.id = :productId")
    Integer getTotalSuppliedQuantityByProductId(@Param("productId") UUID productId);

    @Modifying
    @Query("DELETE FROM SupplyItem si WHERE si.supply.id = :supplyId")
    void deleteBySupplyId(@Param("supplyId") UUID supplyId);
}