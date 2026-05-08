package ohio.rizz.homeappliancestore.services;

import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.entities.Expense;
import ohio.rizz.homeappliancestore.enums.AuditAction;
import ohio.rizz.homeappliancestore.enums.AuditEntityType;
import ohio.rizz.homeappliancestore.enums.ExpenseType;
import ohio.rizz.homeappliancestore.repositories.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final AuditService auditService;

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense getExpenseById(UUID id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Расход не найден"));
    }

    public List<Expense> getFilteredExpenses(LocalDate from, LocalDate to, ExpenseType type) {
        return expenseRepository.findFiltered(from, to, type);
    }

    @Transactional
    public Expense createExpense(Expense expense) {
        Expense saved = expenseRepository.save(expense);
        auditService.log(AuditAction.CREATE, AuditEntityType.EXPENSE, saved.getId().toString(),
                String.format("Добавлен расход '%s' на сумму %.2f", saved.getDescription(), saved.getAmount()));
        return saved;
    }

    @Transactional
    public Expense updateExpense(UUID id, Expense updated) {
        Expense expense = getExpenseById(id);
        expense.setAmount(updated.getAmount());
        expense.setDescription(updated.getDescription());
        expense.setType(updated.getType());
        expense.setExpenseDate(updated.getExpenseDate());
        Expense saved = expenseRepository.save(expense);
        auditService.log(AuditAction.UPDATE, AuditEntityType.EXPENSE, saved.getId().toString(),
                String.format("Изменён расход '%s', новая сумма %.2f", saved.getDescription(), saved.getAmount()));
        return saved;
    }

    @Transactional
    public void deleteExpense(UUID id) {
        Expense expense = getExpenseById(id);
        expenseRepository.delete(expense);
        auditService.log(AuditAction.DELETE, AuditEntityType.EXPENSE, id.toString(),
                String.format("Удалён расход '%s'", expense.getDescription()));
    }
}