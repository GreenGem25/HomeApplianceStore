package ohio.rizz.homeappliancestore.repositories;

import ohio.rizz.homeappliancestore.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByParentCategoryIsNull();
    List<Category> findByParentCategory_id(UUID parentId);
}
