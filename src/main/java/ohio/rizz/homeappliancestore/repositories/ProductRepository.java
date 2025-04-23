package ohio.rizz.homeappliancestore.repositories;

import ohio.rizz.homeappliancestore.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_Id(Long categoryId);
    List<Product> findByStore_Id(Long storeId);
    List<Product> findByName(String name);
    List<Product> findByNameContaining(String keyword);
    List<Product> findByNameIgnoreCase(String name);
    List<Product> findByManufacturer(String manufacturer);
}
