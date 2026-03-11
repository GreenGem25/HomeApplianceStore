package ohio.rizz.homeappliancestore.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.comparators.ProductComparator;
import ohio.rizz.homeappliancestore.dto.OrderDto;
import ohio.rizz.homeappliancestore.dto.ProductCreateDto;
import ohio.rizz.homeappliancestore.dto.ProductDto;
import ohio.rizz.homeappliancestore.entities.Category;
import ohio.rizz.homeappliancestore.entities.Order;
import ohio.rizz.homeappliancestore.entities.Product;
import ohio.rizz.homeappliancestore.entities.Supplier;
import ohio.rizz.homeappliancestore.exceptions.CategoryNotFoundException;
import ohio.rizz.homeappliancestore.exceptions.OrderNotFoundException;
import ohio.rizz.homeappliancestore.exceptions.ProductNotFoundException;
import ohio.rizz.homeappliancestore.exceptions.SupplierNotFoundException;
import ohio.rizz.homeappliancestore.mappers.ProductMapper;
import ohio.rizz.homeappliancestore.repositories.CategoryRepository;
import ohio.rizz.homeappliancestore.repositories.ProductRepository;
import ohio.rizz.homeappliancestore.repositories.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final SupplierService supplierService;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;

    public List<ProductDto> getAllProducts() {
        return productMapper.toDto(productRepository.findAll());
    }

    public List<ProductDto> getAllProductsNameAsc() {
        return getAllProducts().stream()
                .sorted(ProductComparator.byName())
                .collect(Collectors.toList());
    }

    public List<ProductDto> getAllProductsNameDesc() {
        return getAllProductsNameAsc().reversed();
    }

    public List<ProductDto> getAllProductsPriceAsc() {
        return getAllProducts().stream()
                .sorted(ProductComparator.byPrice())
                .collect(Collectors.toList());
    }

    public List<ProductDto> getAllProductsPriceDesc() {
        return getAllProductsPriceAsc().reversed();
    }

    public ProductDto getProductById(UUID id) {
        Product product = getProductEntityOrThrow(id);
        return productMapper.toDto(product);
    }

    public Product getProductEntityOrThrow(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));
    }

    public Optional<Product> findProductById(UUID id) {
        return productRepository.findById(id);
    }

    public List<ProductDto> getProductsByCategory(UUID categoryId) {
        return productMapper.toDto(productRepository.findByCategory_Id(categoryId));
    }

    @Transactional
    public ProductDto createProduct(ProductCreateDto createDto, MultipartFile imageFile) throws IOException {
        Product product = productMapper.toEntity(createDto);

        // Устанавливаем категорию
        if (createDto.getCategoryId() != null) {
            Category category = categoryService.getCategoryEntityOrThrow(createDto.getCategoryId());
            product.setCategory(category);
        }

        // Устанавливаем поставщика
        if (createDto.getSupplierId() != null) {
            Supplier supplier = supplierService.getSupplierEntityOrThrow(createDto.getSupplierId());
            product.setSupplier(supplier);
        }

        // Сохраняем изображение, если есть
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveProductImage(imageFile, null);
            product.setImagePath(imagePath);
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Transactional
    public ProductDto updateProduct(UUID id, ProductCreateDto updateDto, MultipartFile imageFile) throws IOException {
        Product product = getProductEntityOrThrow(id);

        productMapper.updateEntity(product, updateDto);

        // Обновление категории
        if (updateDto.getCategoryId() != null) {
            Category category = categoryService.getCategoryEntityOrThrow(updateDto.getCategoryId());
            product.setCategory(category);
        }

        // Обновление поставщика
        if (updateDto.getSupplierId() != null) {
            Supplier supplier = supplierService.getSupplierEntityOrThrow(updateDto.getSupplierId());
            product.setSupplier(supplier);
        }

        // Обновление изображения
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveProductImage(imageFile, product.getImagePath());
            product.setImagePath(imagePath);
        }

        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    private String saveProductImage(MultipartFile imageFile, String oldImagePath) throws IOException {
        String uploadDir = "uploads/products/";
        String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + ".jpg";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Удаляем старое изображение
        if (oldImagePath != null) {
            try {
                Files.deleteIfExists(Paths.get("." + oldImagePath));
            } catch (IOException e) {
                System.err.println("Не удалось удалить старое изображение: " + e.getMessage());
            }
        }

        // Сохраняем новое изображение
        Files.copy(imageFile.getInputStream(),
                Paths.get(uploadDir + fileName),
                StandardCopyOption.REPLACE_EXISTING);

        return "/" + uploadDir + fileName;
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = getProductEntityOrThrow(id);

        // Удаляем изображение
        if (product.getImagePath() != null) {
            try {
                Files.deleteIfExists(Paths.get("." + product.getImagePath()));
            } catch (IOException e) {
                System.err.println("Не удалось удалить изображение товара: " + e.getMessage());
            }
        }

        productRepository.delete(product);
    }

    public List<ProductDto> searchProducts(String query) {
        return productMapper.toDto(
                productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query)
        );
    }

    public List<ProductDto> getProductsByCategoryWithChildren(UUID categoryId) {
        List<UUID> categoryIds = categoryService.getAllChildCategoryIds(categoryId);
        return productMapper.toDto(productRepository.findByCategoryIds(categoryIds));
    }

    public List<ProductDto> getProductsBySupplier(UUID supplierId) {
        return productMapper.toDto(productRepository.findBySupplier_Id(supplierId));
    }

    public List<ProductDto> getAllAvailableProducts() {
        return productMapper.toDto(productRepository.findAllAvailableProducts());
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }
}