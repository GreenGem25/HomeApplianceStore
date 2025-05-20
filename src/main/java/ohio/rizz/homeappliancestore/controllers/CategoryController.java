package ohio.rizz.homeappliancestore.controllers;

import jakarta.validation.Valid;
import ohio.rizz.homeappliancestore.dto.CategoryDto;
import ohio.rizz.homeappliancestore.entities.Category;
import ohio.rizz.homeappliancestore.services.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
}
