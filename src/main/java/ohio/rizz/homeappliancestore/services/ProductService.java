package ohio.rizz.homeappliancestore.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ohio.rizz.homeappliancestore.comparators.ProductComparator;
import ohio.rizz.homeappliancestore.dto.ProductDto;
import ohio.rizz.homeappliancestore.entities.Category;
import ohio.rizz.homeappliancestore.entities.Product;
import ohio.rizz.homeappliancestore.entities.Supplier;
import ohio.rizz.homeappliancestore.exceptions.ProductNotFoundException;
import ohio.rizz.homeappliancestore.repositories.CategoryRepository;
import ohio.rizz.homeappliancestore.repositories.ProductRepository;

import ohio.rizz.homeappliancestore.repositories.ReviewRepository;
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
import java.util.stream.Collectors;

@Service
public class ProductService {
    final private ProductRepository productRepository;
    final private CategoryRepository categoryRepository;
    final private SupplierRepository supplierRepository;
    final private ReviewRepository reviewRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, SupplierRepository supplierRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.reviewRepository = reviewRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getAllProductsNameAsc() {
        return getAllProducts().stream().sorted(ProductComparator.byName())
                .collect(Collectors.toList());
    }

    public List<Product> getAllProductsNameDesc() {
        return getAllProductsNameAsc().reversed();
    }

    public List<Product> getAllProductsPriceAsc() {
        return getAllProducts().stream().sorted(ProductComparator.byPrice())
                .collect(Collectors.toList());
    }

    public List<Product> getAllProductsPriceDesc() {
        return getAllProductsPriceAsc().reversed();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategory_Id(categoryId);
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, ProductDto productEditDto, MultipartFile imageFile) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден"));

        product.setName(productEditDto.getName());
        product.setDescription(productEditDto.getDescription());
        product.setPrice(productEditDto.getPrice());
        product.setStockQuantity(productEditDto.getStockQuantity());
        product.setManufacturer(productEditDto.getManufacturer());
        product.setWarrantyPeriod(productEditDto.getWarrantyPeriod());

        // Обновление категории
        Category category = categoryRepository.findById(productEditDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена"));
        product.setCategory(category);

        // Обновление поставщика
        Supplier supplier = supplierRepository.findById(productEditDto.getSupplierId())
                .orElseThrow(() -> new EntityNotFoundException("Поставщик не найден"));
        product.setSupplier(supplier);

        // Обновление изображения (если загружено новое)
        if (imageFile != null && !imageFile.isEmpty()) {
            String uploadDir = "uploads/products/";
            String fileName = System.currentTimeMillis() + "_" + product.getId() + ".jpg";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Удаляем старое изображение (если оно было)
            if (product.getImagePath() != null) {
                try {
                    Files.deleteIfExists(Paths.get("." + product.getImagePath()));
                } catch (IOException e) {
                    System.err.println("Не удалось удалить старое изображение: " + e.getMessage());
                }
            }

            // Сохраняем новое изображение
            Files.copy(imageFile.getInputStream(),
                    Paths.get(uploadDir + fileName),
                    StandardCopyOption.REPLACE_EXISTING);

            product.setImagePath("/" + uploadDir + fileName);
        }

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));

        // Удаляем связанные отзывы (если есть каскадное удаление)
        if (product.getReviews() != null) {
            reviewRepository.deleteAll(product.getReviews());
        }

        // Удаляем изображение (если оно есть)
        if (product.getImagePath() != null) {
            try {
                Files.deleteIfExists(Paths.get("." + product.getImagePath()));
            } catch (IOException e) {
                System.err.println("Не удалось удалить изображение товара: " + e.getMessage());
            }
        }

        productRepository.delete(product);
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }
}