package ohio.rizz.homeappliancestore.services;

import ohio.rizz.homeappliancestore.comparators.ProductComparator;
import ohio.rizz.homeappliancestore.entities.Product;
import ohio.rizz.homeappliancestore.repositories.ProductRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    final private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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

    public List<Product> getProductsByStore(Long storeId) {
        return productRepository.findByStore_Id(storeId);
    }

    public List<Product> findByproductName(String name) {
        return productRepository.findByName(name);
    }

    public List<Product> findByproductNameContaining(String keyword) {
        return productRepository.findByNameContaining(keyword);
    }

    public List<Product> findByproductNameIgnoreCase(String name) {
        return productRepository.findByNameIgnoreCase(name);
    }

    public List<Product> findByproductManufacturer(String manufacturer) {
        return productRepository.findByManufacturer(manufacturer);
    }

    public void save(Product product) {
        productRepository.save(product);
    }

}