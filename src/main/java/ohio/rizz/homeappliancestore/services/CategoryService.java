package ohio.rizz.homeappliancestore.services;

import jakarta.transaction.Transactional;
import ohio.rizz.homeappliancestore.dto.CategoryCreateDto;
import ohio.rizz.homeappliancestore.dto.CategoryDto;
import ohio.rizz.homeappliancestore.dto.CustomerDto;
import ohio.rizz.homeappliancestore.entities.Category;
import ohio.rizz.homeappliancestore.entities.Customer;
import ohio.rizz.homeappliancestore.exceptions.CategoryNotFoundException;
import ohio.rizz.homeappliancestore.exceptions.CustomerNotFoundException;
import ohio.rizz.homeappliancestore.mappers.CategoryMapper;
import ohio.rizz.homeappliancestore.repositories.CategoryRepository;
import ohio.rizz.homeappliancestore.repositories.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryDto> getRootCategories() {
        return categoryMapper.toDto(categoryRepository.findByParentCategoryIsNull());
    }

    public List<CategoryDto> getAllCategories() {
        return categoryMapper.toDto(categoryRepository.findAll());
    }

    public CategoryDto getCategoryById(UUID id) {
        Category category = getCategoryEntityOrThrow(id);
        return categoryMapper.toDto(category);
    }

    public Category getCategoryEntityOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Категория не найдена"));
    }

    public Optional<Category> findCategoryById(UUID id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public CategoryDto createCategory(CategoryCreateDto createDto) {
        Category category = categoryMapper.toEntity(createDto);

        if (createDto.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(createDto.getParentCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Родительская категория не найдена!"));
            category.setParentCategory(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Transactional
    public CategoryDto updateCategory(UUID id, CategoryCreateDto createDto) {
        Category existingCategory = getCategoryEntityOrThrow(id);

        categoryMapper.updateEntity(existingCategory, createDto);

        if (createDto.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(createDto.getParentCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Родительская категория не найдена"));
            existingCategory.setParentCategory(parent);
        }

        Category updatedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toDto(updatedCategory);
    }

    public List<CategoryDto> getAllCategoriesWithChildren() {
        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNull();
        rootCategories.forEach(this::loadChildrenRecursively);
        return categoryMapper.toDto(rootCategories);
    }

    private void loadChildrenRecursively(Category category) {
        List<Category> children = categoryRepository.findByParentCategory_id(category.getId());
        category.setChildCategories(children);
        children.forEach(this::loadChildrenRecursively);
    }

    public List<CategoryDto> getCategoryTree() {
        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNull();
        rootCategories.forEach(this::loadChildrenRecursively);
        return categoryMapper.toDto(rootCategories);
    }

    public List<CategoryDto> getAllCategoriesExcept(UUID id) {
        return categoryMapper.toDto(
                categoryRepository.findAll().stream()
                        .filter(category -> !category.getId().equals(id))
                        .collect(Collectors.toList())
        );
    }

    @Cacheable(value = "categoryHierarchy", key = "#parentCategoryId")
    public List<UUID> getAllChildCategoryIds(UUID parentCategoryId) {
        List<UUID> categoryIds = new ArrayList<>();
        categoryIds.add(parentCategoryId);

        List<Category> children = categoryRepository.findByParentCategory_id(parentCategoryId);
        for (Category child : children) {
            categoryIds.addAll(getAllChildCategoryIds(child.getId()));
        }

        return categoryIds;
    }

    public int getAllChildProductCount(UUID parentCategoryId) {
        List<UUID> categoryIdList = getAllChildCategoryIds(parentCategoryId);
        return categoryIdList.stream()
                .mapToInt(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new CategoryNotFoundException("Категория не найдена"))
                        .getProducts()
                        .size())
                .sum();
    }

    @Transactional
    public void deleteCategory(UUID id) {
        Category category = getCategoryEntityOrThrow(id);

        productRepository.findByCategory_Id(category.getId())
                .forEach(product -> {
                    product.setCategory(null);
                    productRepository.save(product);
                });

        categoryRepository.deleteById(id);
    }

    public int getTotalProductCount(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Категория не найдена"));

        return getTotalProductCountRecursive(category);
    }

    private int getTotalProductCountRecursive(Category category) {
        int count = category.getProducts().size();

        for (Category child : category.getChildCategories()) {
            count += getTotalProductCountRecursive(child);
        }

        return count;
    }

    public CategoryDto getCategoryWithProductCount(UUID id) {
        CategoryDto category = getCategoryById(id);
        category.setProductCount(getTotalProductCount(id));
        return category;
    }

}