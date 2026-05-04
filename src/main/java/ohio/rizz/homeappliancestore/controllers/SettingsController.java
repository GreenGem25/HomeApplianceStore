package ohio.rizz.homeappliancestore.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.SettingsDto;
import ohio.rizz.homeappliancestore.services.SettingsService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings")
@Secured("ROLE_ADMIN")
@RequiredArgsConstructor
public class SettingsController {
    private final SettingsService service;

    @GetMapping
    public String editForm(Model model) {
        model.addAttribute("settings", service.getSettings());
        return "shop-settings";
    }

    @PostMapping
    public String update(@Valid @ModelAttribute("settings") SettingsDto settings,
                         RedirectAttributes redirectAttributes) {
        service.updateSettings(settings);
        redirectAttributes.addFlashAttribute("successMessage", "Настройки сохранены");
        return "redirect:/settings";
    }
}
