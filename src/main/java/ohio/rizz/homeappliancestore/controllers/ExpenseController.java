package ohio.rizz.homeappliancestore.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.entities.Expense;
import ohio.rizz.homeappliancestore.enums.ExpenseType;
import ohio.rizz.homeappliancestore.services.ExpenseService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/expenses")
@Secured("ROLE_ADMIN")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    public String listExpenses(@RequestParam(required = false) LocalDate from,
                               @RequestParam(required = false) LocalDate to,
                               @RequestParam(required = false) ExpenseType type,
                               Model model) {
        List<Expense> expenses = expenseService.getFilteredExpenses(from, to, type);
        model.addAttribute("expenses", expenses);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("type", type);
        model.addAttribute("types", ExpenseType.values());
        return "expenses";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("expense", new Expense());
        model.addAttribute("types", ExpenseType.values());
        return "add-expense";
    }

    @PostMapping("/add")
    public String addExpense(@Valid @ModelAttribute("expense") Expense expense,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("types", ExpenseType.values());
            return "add-expense";
        }
        expenseService.createExpense(expense);
        redirectAttributes.addFlashAttribute("successMessage", "Расход добавлен");
        return "redirect:/expenses";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable UUID id, Model model) {
        Expense expense = expenseService.getExpenseById(id);
        model.addAttribute("expense", expense);
        model.addAttribute("types", ExpenseType.values());
        return "edit-expense";
    }

    @PostMapping("/{id}/edit")
    public String updateExpense(@PathVariable UUID id,
                                @Valid @ModelAttribute("expense") Expense expense,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("types", ExpenseType.values());
            return "edit-expense";
        }
        expenseService.updateExpense(id, expense);
        redirectAttributes.addFlashAttribute("successMessage", "Расход обновлён");
        return "redirect:/expenses";
    }

    @PostMapping("/{id}/delete")
    public String deleteExpense(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        expenseService.deleteExpense(id);
        redirectAttributes.addFlashAttribute("successMessage", "Расход удалён");
        return "redirect:/expenses";
    }
}