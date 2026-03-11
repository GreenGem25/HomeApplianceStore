package ohio.rizz.homeappliancestore.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.CategoryCreateDto;
import ohio.rizz.homeappliancestore.dto.CategoryDto;
import ohio.rizz.homeappliancestore.exceptions.CategoryNotFoundException;
import ohio.rizz.homeappliancestore.services.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getCategoryTree());
        return "categories";
    }

    @GetMapping("/{id}")
    public String getCategoryDetails(@PathVariable UUID id, Model model) {
        CategoryDto category = categoryService.getCategoryWithProductCount(id);

        model.addAttribute("category", category);
        model.addAttribute("productCount", category.getProductCount());
        model.addAttribute("childrenCount", categoryService.getAllChildCategoryIds(id).size() - 1);

        return "category-details";
    }

    @GetMapping("/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new CategoryCreateDto());
        model.addAttribute("allCategories", categoryService.getAllCategories());
        return "add-category";
    }

    @PostMapping("/add")
    public String addCategory(
            @Valid @ModelAttribute("category") CategoryCreateDto createDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allCategories", categoryService.getAllCategories());
            return "add-category";
        }

        try {
            categoryService.createCategory(createDto);
            redirectAttributes.addFlashAttribute("successMessage", "Категория успешно добавлена!");
            return "redirect:/categories";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении: " + e.getMessage());
            return "redirect:/categories/add";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditCategoryForm(@PathVariable UUID id, Model model) {
        CategoryDto category = categoryService.getCategoryById(id);

        CategoryCreateDto createDto = new CategoryCreateDto();
        createDto.setName(category.getName());
        createDto.setDescription(category.getDescription());
        createDto.setParentCategoryId(category.getParentCategoryId());

        model.addAttribute("categoryDto", createDto);
        model.addAttribute("allCategories", categoryService.getAllCategoriesExcept(id));
        return "edit-category";
    }

    @PutMapping("/{id}")
    public String updateCategory(
            @PathVariable UUID id,
            @Valid @ModelAttribute("categoryDto") CategoryCreateDto createDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allCategories", categoryService.getAllCategoriesExcept(id));
            return "edit-category";
        }

        try {
            // Проверяем, не пытаемся ли сделать категорию родителем самой себя
            if (createDto.getParentCategoryId() != null &&
                    createDto.getParentCategoryId().equals(id)) {
                throw new IllegalArgumentException("Категория не может быть родителем самой себя");
            }

            categoryService.updateCategory(id, createDto);
            redirectAttributes.addFlashAttribute("successMessage", "Категория успешно обновлена!");
            return "redirect:/categories";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении: " + e.getMessage());
            return "redirect:/categories/" + id + "/edit";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteCategory(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Категория успешно удалена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении категории: " + e.getMessage());
        }
        return "redirect:/categories";
    }
}