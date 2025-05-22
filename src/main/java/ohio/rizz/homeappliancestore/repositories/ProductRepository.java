package ohio.rizz.homeappliancestore.repositories;

import ohio.rizz.homeappliancestore.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_Id(Long categoryId);
    List<Product> findBySupplier_Id(Long supplierId);

    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            @Param("query") String nameQuery,
            @Param("query") String descriptionQuery);

    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds")
    List<Product> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

}
