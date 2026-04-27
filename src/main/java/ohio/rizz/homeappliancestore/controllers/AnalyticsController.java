package ohio.rizz.homeappliancestore.controllers;

import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.DashboardDto;
import ohio.rizz.homeappliancestore.services.AnalyticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping
    public String dashboard(Model model) {
        // Период по умолчанию – последние 30 дней
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(30);
        DashboardDto dto = analyticsService.getDashboard(start, end);
        model.addAttribute("dashboard", dto);
        model.addAttribute("from", start);
        model.addAttribute("to", end);
        return "analytics";
    }
}