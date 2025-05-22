package ohio.rizz.homeappliancestore.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import ohio.rizz.homeappliancestore.dto.CategoryDto;
import ohio.rizz.homeappliancestore.entities.Category;
import ohio.rizz.homeappliancestore.services.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CategoryController {
    final private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getCategoryTree());
        return "categories";
    }

    @GetMapping("/categories/{id}")
    public String getCategoryDetails(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена"));

        model.addAttribute("category", category);
        model.addAttribute("productCount", categoryService.getAllChildProductCount(id));
        model.addAttribute("childrenCount", categoryService.getAllChildCategoryIds(id).size()-1);
        return "category-details";
    }

    @GetMapping("/categories/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("allCategories", categoryService.getAllCategories());
        return "add-category";
    }

    @PostMapping("/categories/add")
    public String addCategory(
            Model model,
            @ModelAttribute Category category,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allCategories", categoryService.getAllCategories());
            return "add-category";
        }

        categoryService.save(category);
        redirectAttributes.addFlashAttribute("successMessage", "Категория успешно добавлена!");
        return "redirect:/categories";
    }

    @GetMapping("/categories/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена"));

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());
        categoryDto.setParentCategoryId(
                category.getParentCategory() != null ? category.getParentCategory().getId() : null
        );

        model.addAttribute("categoryDto", categoryDto);
        model.addAttribute("allCategories", categoryService.getAllCategoriesExcept(id));
        return "edit-category";
    }

    @PostMapping("/categories/{id}/edit")
    public String updateCategory(
            @PathVariable Long id,
            @Valid @ModelAttribute CategoryDto categoryDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allCategories", categoryService.getAllCategoriesExcept(id));
            return "edit-category";
        }

        try {
            categoryService.updateCategory(id, categoryDto);
            redirectAttributes.addFlashAttribute("successMessage", "Категория успешно обновлена!");
            return "redirect:/categories";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении: " + e.getMessage());
            return "redirect:/categories/" + id + "/edit";
        }
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(
            @PathVariable Long id,
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
