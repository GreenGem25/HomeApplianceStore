package ohio.rizz.homeappliancestore.services;

import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.ExpenseCreateDto;
import ohio.rizz.homeappliancestore.dto.ExpenseDto;
import ohio.rizz.homeappliancestore.entities.Category;
import ohio.rizz.homeappliancestore.entities.Expense;
import ohio.rizz.homeappliancestore.enums.AuditAction;
import ohio.rizz.homeappliancestore.enums.AuditEntityType;
import ohio.rizz.homeappliancestore.enums.ExpenseType;
import ohio.rizz.homeappliancestore.exceptions.CategoryNotFoundException;
import ohio.rizz.homeappliancestore.mappers.ExpenseMapper;
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
    private final ExpenseMapper expenseMapper;

    public List<ExpenseDto> getAllExpenses() {
        return expenseMapper.toDto(expenseRepository.findAll());
    }

    public ExpenseDto getExpenseById(UUID id) {
        Expense expense = getExpenseEntityOrThrow(id);
        return expenseMapper.toDto(expense);
    }

    public Expense getExpenseEntityOrThrow(UUID id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Расход не найден"));
    }

    public List<ExpenseDto> getFilteredExpenses(LocalDate from, LocalDate to, ExpenseType type) {
        return expenseMapper.toDto(expenseRepository.findFiltered(from, to, type));
    }

    @Transactional
    public ExpenseDto createExpense(ExpenseCreateDto expenseDto) {
        Expense expense = expenseMapper.toEntity(expenseDto);
        ExpenseDto saved = expenseMapper.toDto(expenseRepository.save(expense));
        auditService.log(AuditAction.CREATE, AuditEntityType.EXPENSE, saved.getId().toString(),
                String.format("Добавлен расход '%s' на сумму %.2f", saved.getDescription(), saved.getAmount()));
        return saved;
    }

    @Transactional
    public ExpenseDto updateExpense(UUID id, ExpenseCreateDto updated) {
        Expense expense = getExpenseEntityOrThrow(id);
        expense.setAmount(updated.getAmount());
        expense.setDescription(updated.getDescription());
        expense.setType(updated.getType());
        expense.setExpenseDate(updated.getExpenseDate());
        Expense saved = expenseRepository.save(expense);
        auditService.log(AuditAction.UPDATE, AuditEntityType.EXPENSE, saved.getId().toString(),
                String.format("Изменён расход '%s', новая сумма %.2f", saved.getDescription(), saved.getAmount()));
        return expenseMapper.toDto(saved);
    }

    @Transactional
    public void deleteExpense(UUID id) {
        Expense expense = getExpenseEntityOrThrow(id);
        expenseRepository.delete(expense);
        auditService.log(AuditAction.DELETE, AuditEntityType.EXPENSE, id.toString(),
                String.format("Удалён расход '%s'", expense.getDescription()));
    }
}