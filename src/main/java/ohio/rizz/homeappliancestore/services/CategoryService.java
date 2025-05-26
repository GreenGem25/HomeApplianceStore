package ohio.rizz.homeappliancestore.services;

import jakarta.transaction.Transactional;
import ohio.rizz.homeappliancestore.dto.CategoryDto;
import ohio.rizz.homeappliancestore.entities.Category;
import ohio.rizz.homeappliancestore.exceptions.CategoryNotFoundException;
import ohio.rizz.homeappliancestore.repositories.CategoryRepository;
import ohio.rizz.homeappliancestore.repositories.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    final private CategoryRepository categoryRepository;
    final private ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<Category> getRootCategories() {
        return categoryRepository.findByParentCategoryIsNull();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category createCategory(CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());

        if (dto.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(dto.getParentCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Родительская категория не найдена!"));
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

    public List<Category> getAllCategoriesExcept(Long id) {
        return getAllCategories().stream()
                .filter(category -> !category.getId().equals(id))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "categoryHierarchy", key = "#parentCategoryId")
    public List<Long> getAllChildCategoryIds(Long parentCategoryId) {
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(parentCategoryId); // Добавляем саму родительскую категорию

        // Рекурсивно получаем все дочерние ID
        List<Category> children = categoryRepository.findByParentCategory_id(parentCategoryId);
        for (Category child : children) {
            categoryIds.addAll(getAllChildCategoryIds(child.getId()));
        }

        return categoryIds;
    }

    public int getAllChildProductCount(Long parentCategoryId) {
        int count = 0;
        List<Long> categoryIdList = getAllChildCategoryIds(parentCategoryId);
        for (Long id : categoryIdList) {
            count += categoryRepository.findById(id)
                    .orElseThrow(() -> new CategoryNotFoundException("Категория не найдена")).getProducts().size();
        }
        return count;
    }

    @Transactional
    public void updateCategory(Long id, CategoryDto dto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Категория не найдена"));

        existingCategory.setName(dto.getName());
        existingCategory.setDescription(dto.getDescription());
        if (dto.getParentCategoryId() != null) {
            existingCategory.setParentCategory(categoryRepository.findById(dto.getParentCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Родительская категория не найдена")));
        }

        categoryRepository.save(existingCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Категория не найдена"));
        productRepository.findByCategory_Id(category.getId()).forEach(product -> {
            product.setCategory(null);
            productRepository.save(product);
        });
        categoryRepository.deleteById(id);
    }
}
