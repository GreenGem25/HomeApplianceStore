package ohio.rizz.homeappliancestore.controllers;

import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.DashboardDto;
import ohio.rizz.homeappliancestore.services.AnalyticsService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping
    public String dashboard(@RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(30)}") LocalDate from,
                            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate to,
                            Model model) {
        DashboardDto dto = analyticsService.getDashboard(from, to);
        model.addAttribute("dashboard", dto);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "analytics";
    }

    @PostMapping("/refresh")
    public String refreshAnalytics(@RequestParam LocalDate date,
                                   @RequestParam(required = false) LocalDate from,
                                   @RequestParam(required = false) LocalDate to,
                                   RedirectAttributes redirectAttributes) {
        try {
            analyticsService.updateDailyAnalytics(date);
            redirectAttributes.addFlashAttribute("successMessage", "Аналитика за " + date + " обновлена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка обновления: " + e.getMessage());
        }
        // Возвращаемся на ту же страницу с теми же фильтрами
        String redirectUrl = "/analytics";
        if (from != null && to != null) {
            redirectUrl += "?from=" + from + "&to=" + to;
        }
        return "redirect:" + redirectUrl;
    }

    @PostMapping("/refresh-range")
    public String refreshRange(@RequestParam LocalDate from, @RequestParam LocalDate to,
                               RedirectAttributes redirectAttributes) {
        analyticsService.refreshAnalytics(from, to);
        redirectAttributes.addFlashAttribute("successMessage", "Аналитика за период обновлена");
        return "redirect:/analytics?from=" + from + "&to=" + to;
    }
}