package ohio.rizz.homeappliancestore.controllers;

import jakarta.validation.Valid;
import ohio.rizz.homeappliancestore.dto.ProductDto;
import ohio.rizz.homeappliancestore.entities.Product;
import ohio.rizz.homeappliancestore.entities.Supplier;
import ohio.rizz.homeappliancestore.exceptions.ProductNotFoundException;
import ohio.rizz.homeappliancestore.exceptions.SupplierNotFoundException;
import ohio.rizz.homeappliancestore.repositories.ProductRepository;
import ohio.rizz.homeappliancestore.services.CategoryService;
import ohio.rizz.homeappliancestore.services.ProductService;

import ohio.rizz.homeappliancestore.services.SupplierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;

    ProductController(ProductService productService, CategoryService categoryService, SupplierService supplierService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.supplierService = supplierService;
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

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "add-product";
    }

    @PostMapping("/products/add")
    public String addProduct(
            @ModelAttribute Product product,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam Long supplierId,
            RedirectAttributes redirectAttributes) {

        Supplier supplier = supplierService.getSupplierById(supplierId)
                .orElseThrow(() -> new SupplierNotFoundException("Поставщик не найден"));
        product.setSupplier(supplier);

        try {
            // Обработка изображения
            if (!imageFile.isEmpty()) {
                String uploadDir = "uploads/products/";
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();

                // Создаем директорию, если не существует
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Сохраняем файл
                try (InputStream inputStream = imageFile.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + fileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }

                product.setImagePath("/" + uploadDir + fileName);
            }

            productService.save(product);
            redirectAttributes.addFlashAttribute("successMessage", "Товар успешно добавлен!");
            return "redirect:/products";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при загрузке изображения");
            return "redirect:/products/add";
        }
    }

    @GetMapping("/products/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден"));

        // Создаем DTO для формы редактирования
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setStockQuantity(product.getStockQuantity());
        productDto.setManufacturer(product.getManufacturer());
        productDto.setWarrantyPeriod(product.getWarrantyPeriod());
        productDto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        productDto.setSupplierId(product.getSupplier() != null ? product.getSupplier().getId() : null);

        model.addAttribute("productEditDto", productDto);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "edit-product";
    }

    @PostMapping("/products/{id}/edit")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("productEditDto") ProductDto productDto,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            return "edit-product";
        }

        try {
            Product product = productService.updateProduct(id, productDto, imageFile);
            redirectAttributes.addFlashAttribute("successMessage", "Товар успешно обновлен!");
            return "redirect:/products/" + product.getId();
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении изображения");
            return "redirect:/products/" + id + "/edit";
        }
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Товар успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении товара: " + e.getMessage());
        }

        return "redirect:/products";
    }
}