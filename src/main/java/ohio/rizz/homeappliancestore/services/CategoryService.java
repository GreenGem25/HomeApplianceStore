package ohio.rizz.homeappliancestore.services;

import jakarta.persistence.EntityNotFoundException;
import ohio.rizz.homeappliancestore.dto.CategoryDto;
import ohio.rizz.homeappliancestore.entities.Category;
import ohio.rizz.homeappliancestore.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    final private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getRootCategories() {
        return categoryRepository.findByParentCategoryIsNull();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());

        if (dto.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(dto.getParentCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found"));
            category.setParentCategory(parent);
        }

        return categoryRepository.save(category);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategoriesWithChildren() {
        // Получаем все корневые категории
        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNull();

        // Для каждой корневой категории загружаем дочерние
        rootCategories.forEach(category -> {
            category.setChildCategories(categoryRepository.findByParentCategory_id(category.getId()));
        });

        return rootCategories;
    }

    public List<Category> getCategoryTree() {
        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNull();
        return rootCategories.stream()
                .peek(this::loadChildrenRecursively)
                .collect(Collectors.toList());
    }

    private void loadChildrenRecursively(Category category) {
        List<Category> children = categoryRepository.findByParentCategory_id(category.getId());
        category.setChildCategories(children);
        children.forEach(this::loadChildrenRecursively);
    }
}
