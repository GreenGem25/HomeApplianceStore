package ohio.rizz.homeappliancestore.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.ProductCreateDto;
import ohio.rizz.homeappliancestore.dto.ProductDto;
import ohio.rizz.homeappliancestore.exceptions.CategoryNotFoundException;
import ohio.rizz.homeappliancestore.exceptions.SupplierNotFoundException;
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
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;

    @GetMapping
    public String listProducts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID supplierId,
            @RequestParam(defaultValue = "name_asc") String sortBy,
            @RequestParam(required = false) String search,
            Model model) {

        List<ProductDto> products;

        if (search != null && !search.isEmpty()) {
            products = productService.searchProducts(search);
        } else if (categoryId != null) {
            products = productService.getProductsByCategoryWithChildren(categoryId);
            model.addAttribute("currentCategory", categoryService.getCategoryById(categoryId));
        } else if (supplierId != null) {
            products = productService.getProductsBySupplier(supplierId);
            model.addAttribute("currentSupplier", supplierService.getSupplierById(supplierId));
        } else {
            products = switch (sortBy) {
                case "name_asc" -> productService.getAllProductsNameAsc();
                case "name_desc" -> productService.getAllProductsNameDesc();
                case "price_asc" -> productService.getAllProductsPriceAsc();
                case "price_desc" -> productService.getAllProductsPriceDesc();
                default -> productService.getAllProducts();
            };
        }

        model.addAttribute("activePage", "Список товаров");
        model.addAttribute("products", products);
        model.addAttribute("sortBy", sortBy);
        return "products";
    }

    @GetMapping("/{id}")
    public String getProductDetails(@PathVariable UUID id, Model model) {
        ProductDto product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "product-details";
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new ProductCreateDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "add-product";
    }

    @PostMapping("/add")
    public String addProduct(
            @Valid @ModelAttribute("product") ProductCreateDto createDto,
            BindingResult bindingResult,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            return "add-product";
        }

        try {
            ProductDto savedProduct = productService.createProduct(createDto, imageFile);
            redirectAttributes.addFlashAttribute("successMessage", "Товар успешно добавлен!");
            return "redirect:/products/" + savedProduct.getId();
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при загрузке изображения");
            return "redirect:/products/add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении: " + e.getMessage());
            return "redirect:/products/add";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditProductForm(@PathVariable UUID id, Model model) {
        ProductDto product = productService.getProductById(id);

        ProductCreateDto createDto = new ProductCreateDto();
        createDto.setName(product.getName());
        createDto.setDescription(product.getDescription());
        createDto.setPrice(product.getPrice());
        createDto.setStockQuantity(product.getStockQuantity());
        createDto.setManufacturer(product.getManufacturer());
        createDto.setWarrantyPeriod(product.getWarrantyPeriod());
        createDto.setCategoryId(product.getCategoryId());
        createDto.setSupplierId(product.getSupplierId());

        model.addAttribute("productEditDto", createDto);
        model.addAttribute("productId", id);
        model.addAttribute("currentImage", product.getImagePath());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "edit-product";
    }

    @PutMapping("/{id}")
    public String updateProduct(
            @PathVariable UUID id,
            @Valid @ModelAttribute("productEditDto") ProductCreateDto createDto,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            model.addAttribute("currentImage", productService.getProductById(id).getImagePath());
            return "edit-product";
        }

        try {
            productService.updateProduct(id, createDto, imageFile);
            redirectAttributes.addFlashAttribute("successMessage", "Товар успешно обновлен!");
            return "redirect:/products";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении изображения");
            return "redirect:/products/" + id + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении: " + e.getMessage());
            return "redirect:/products/" + id + "/edit";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(
            @PathVariable UUID id,
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