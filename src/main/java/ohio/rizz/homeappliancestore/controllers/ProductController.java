package ohio.rizz.homeappliancestore.controllers;

import ohio.rizz.homeappliancestore.entities.Product;
import ohio.rizz.homeappliancestore.exceptions.ProductNotFoundException;
import ohio.rizz.homeappliancestore.services.ProductService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductController {
    private final ProductService productService;

    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String listProducts(
            @RequestParam(defaultValue = "name_asc") String sortBy,
            Model model) {

        List<Product> products = switch (sortBy) {
            case "name_asc" -> productService.getAllProductsNameAsc();
            case "name_desc" -> productService.getAllProductsNameDesc();
            case "price_asc" -> productService.getAllProductsPriceAsc();
            case "price_desc" -> productService.getAllProductsPriceDesc();
            default ->
                    productService.getAllProducts();
        };

        model.addAttribute("activePage", "Список товаров");
        model.addAttribute("products", products);
        model.addAttribute("sortBy", sortBy);
        return "products";
    }

    @GetMapping("/products/{id}")
    public String getProductById(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден!"));
        model.addAttribute("product", product);
        return "product-details";
    }
}