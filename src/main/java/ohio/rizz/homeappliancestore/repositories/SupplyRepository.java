package ohio.rizz.homeappliancestore.repositories;

import ohio.rizz.homeappliancestore.entities.Supply;
import ohio.rizz.homeappliancestore.enums.SupplyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplyRepository extends JpaRepository<Supply, UUID> {

    Optional<Supply> findBySupplyNumber(String supplyNumber);

    List<Supply> findBySupplierId(UUID supplierId);

    List<Supply> findByStatus(SupplyStatus status);

    @Query("SELECT s FROM Supply s WHERE s.supplyDate BETWEEN :startDate AND :endDate")
    List<Supply> findBySupplyDateBetween(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Supply s WHERE LOWER(s.supplyNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Supply> searchBySupplyNumber(@Param("searchTerm") String searchTerm);

    @Query("SELECT COUNT(s) FROM Supply s WHERE s.supplier.id = :supplierId")
    int countBySupplierId(@Param("supplierId") UUID supplierId);

    @Query("SELECT s FROM Supply s JOIN FETCH s.supplier WHERE s.id = :id")
    Optional<Supply> findByIdWithSupplier(@Param("id") UUID id);

    @Query("SELECT s FROM Supply s JOIN FETCH s.supplyItems si JOIN FETCH si.product WHERE s.id = :id")
    Optional<Supply> findByIdWithItems(@Param("id") UUID id);
}