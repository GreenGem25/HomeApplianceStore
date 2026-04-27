package ohio.rizz.homeappliancestore.repositories;

import ohio.rizz.homeappliancestore.entities.DailyAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyAnalyticsRepository extends JpaRepository<DailyAnalytics, LocalDate> {
    List<DailyAnalytics> findByDateBetweenOrderByDate(LocalDate from, LocalDate to);
}