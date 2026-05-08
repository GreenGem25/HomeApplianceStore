package ohio.rizz.homeappliancestore.controllers;

import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.AuditLogDto;
import ohio.rizz.homeappliancestore.services.AuditService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/audit")
@Secured("ROLE_ADMIN")
@RequiredArgsConstructor
public class AuditController {
    private final AuditService auditService;

    @GetMapping
    public String listAudit(@RequestParam(required = false) LocalDate from,
                            @RequestParam(required = false) LocalDate to,
                            @RequestParam(required = false) String username,
                            @RequestParam(required = false) String entityType,
                            @RequestParam(required = false) String action,
                            Model model) {
        List<AuditLogDto> logs = auditService.getFilteredLogs(from, to, username, entityType, action);
        model.addAttribute("logs", logs);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("username", username);
        model.addAttribute("entityType", entityType);
        model.addAttribute("action", action);
        return "audit";
    }
}