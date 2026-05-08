package ohio.rizz.homeappliancestore.controllers;

import ohio.rizz.homeappliancestore.dto.ProductCreateDto;
import ohio.rizz.homeappliancestore.dto.ProductDto;
import ohio.rizz.homeappliancestore.services.CategoryService;
import ohio.rizz.homeappliancestore.services.ProductService;
import ohio.rizz.homeappliancestore.services.SupplierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;
    @MockitoBean
    private CategoryService categoryService;
    @MockitoBean
    private SupplierService supplierService;

    private final UUID PRODUCT_ID = UUID.randomUUID();

    // Общий ProductDto для возврата из моков
    private ProductDto sampleProductDto() {
        return ProductDto.builder()
                .id(PRODUCT_ID)
                .name("Тестовый товар")
                .price(BigDecimal.valueOf(1000))
                .stockQuantity(5)
                .imagePath("/uploads/test.jpg")
                .build();
    }

    // --------------- Доступ к страницам ---------------

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void listProducts_Authenticated_Returns200() throws Exception {
        when(productService.getAllProductsNameAsc()).thenReturn(List.of(sampleProductDto()));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("products", "sortBy"));
    }

    @Test
    void listProducts_Unauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/products").accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void showAddProductForm_Authenticated_Returns200() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of());
        when(supplierService.getAllSuppliers()).thenReturn(List.of());

        mockMvc.perform(get("/products/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-product"))
                .andExpect(model().attributeExists("product", "categories", "suppliers"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getProductDetails_ProductExists_Returns200() throws Exception {
        when(productService.getProductById(PRODUCT_ID)).thenReturn(sampleProductDto());

        mockMvc.perform(get("/products/{id}", PRODUCT_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("product-details"))
                .andExpect(model().attributeExists("product"));
    }

    // --------------- Добавление товара ---------------

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addProduct_ValidData_RedirectsToProductDetails() throws Exception {
        ProductDto savedDto = sampleProductDto();
        when(productService.createProduct(any(ProductCreateDto.class), any())).thenReturn(savedDto);

        MockMultipartFile image = new MockMultipartFile("imageFile", "test.jpg", "image/jpeg", "file-content".getBytes());

        mockMvc.perform(multipart("/products/add")
                        .file(image)
                        .param("name", "Товар")
                        .param("price", "1000")
                        .param("stockQuantity", "5")
                        .with(csrf()))  // CSRF обязателен для POST‑запросов
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products/" + PRODUCT_ID));

        verify(productService).createProduct(any(ProductCreateDto.class), eq(image));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addProduct_InvalidData_ReturnsAddForm() throws Exception {
        // Отправляем пустые поля, чтобы валидация не прошла
        mockMvc.perform(multipart("/products/add")
                        .file(new MockMultipartFile("imageFile", new byte[0]))
                        .param("name", "")          // нарушение @NotBlank
                        .param("price", "-10")      // нарушение @Min?
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("add-product"))
                .andExpect(model().attributeHasErrors("product"));
    }

    // --------------- Редактирование товара ---------------

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void showEditProductForm_Authenticated_Returns200() throws Exception {
        when(productService.getProductById(PRODUCT_ID)).thenReturn(sampleProductDto());
        when(categoryService.getAllCategories()).thenReturn(List.of());
        when(supplierService.getAllSuppliers()).thenReturn(List.of());

        mockMvc.perform(get("/products/{id}/edit", PRODUCT_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-product"))
                .andExpect(model().attributeExists("productEditDto", "productId", "currentImage"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateProduct_ValidData_RedirectsToProductDetails() throws Exception {
        when(productService.updateProduct(eq(PRODUCT_ID), any(ProductCreateDto.class), any()))
                .thenReturn(sampleProductDto());

        mockMvc.perform(put("/products/{id}", PRODUCT_ID)
                        .param("name", "Обновленный товар")
                        .param("price", "1500")
                        .param("stockQuantity", "3")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products/" + PRODUCT_ID));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateProduct_InvalidData_ReturnsEditForm() throws Exception {
        when(productService.getProductById(PRODUCT_ID)).thenReturn(sampleProductDto());
        when(categoryService.getAllCategories()).thenReturn(List.of());
        when(supplierService.getAllSuppliers()).thenReturn(List.of());

        mockMvc.perform(put("/products/{id}", PRODUCT_ID)
                        .param("name", "")          // невалидно
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-product"))
                .andExpect(model().attributeHasErrors("productEditDto"));
    }

    // --------------- Удаление товара ---------------

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteProduct_Success_RedirectsToList() throws Exception {
        doNothing().when(productService).deleteProduct(PRODUCT_ID);

        mockMvc.perform(delete("/products/{id}", PRODUCT_ID)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attribute("successMessage", "Товар успешно удален"));

        verify(productService).deleteProduct(PRODUCT_ID);
    }
}