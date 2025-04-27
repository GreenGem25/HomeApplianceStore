package ohio.rizz.homeappliancestore.controllers;

import ohio.rizz.homeappliancestore.services.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CategoryController {
    final private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("activePage", "Список категорий");
        model.addAttribute("categories", categoryService.getRootCategories());
        return "categories";
    }
}
