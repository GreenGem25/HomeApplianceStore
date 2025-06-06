package ohio.rizz.homeappliancestore.repositories;

import ohio.rizz.homeappliancestore.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentCategoryIsNull();
    List<Category> findByParentCategory_id(Long parentId);
}
