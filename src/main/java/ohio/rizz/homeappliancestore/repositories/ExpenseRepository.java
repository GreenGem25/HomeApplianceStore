package ohio.rizz.homeappliancestore.repositories;

import ohio.rizz.homeappliancestore.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.expenseDate BETWEEN :from AND :to")
    Optional<BigDecimal> sumAmountBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    List<Expense> findByExpenseDateBetween(LocalDate from, LocalDate to);
}